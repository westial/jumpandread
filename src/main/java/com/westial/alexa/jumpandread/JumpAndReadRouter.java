package com.westial.alexa.jumpandread;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.ResponseEnvelope;
import com.amazon.ask.model.services.Serializer;
import com.amazon.ask.util.JacksonSerializer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.application.command.ChildrenToSearchCommand;
import com.westial.alexa.jumpandread.application.service.wordcheck.FileToWordList;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.intent.*;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.ByConfigurationTextContentProviderFactory;
import com.westial.alexa.jumpandread.infrastructure.service.content.ContentGetterFactory;
import com.westial.alexa.jumpandread.infrastructure.service.content.DefaultAddressModifier;
import com.westial.alexa.jumpandread.infrastructure.service.content.ParserFactory;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebSearchTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.wordcheck.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class JumpAndReadRouter implements RequestStreamHandler
{
    private static final Serializer serializer = new JacksonSerializer();

    private static Configuration config = new EnvironmentConfiguration();

    private static final StateRepository stateRepository =
            new DynamoDbStateRepository(config.retrieve("STATE_TABLE_NAME"));

    private static final StateFactory stateFactory = new DynamoDbStateFactory(stateRepository);

    private static final StateManager stateManager = new StateManager(stateFactory);

    CandidatesSearchFactory searchFactory;

    private static final int DEFAULT_CANDIDATES_FACTOR = 1;
    private static final int DEFAULT_PARAGRAPHS_GROUP_FACTOR = 1;

    private static WordsValidator wordsValidator;
    private static FileToWordList fileToWordList;

    public final void jumpAndRead(InputStream input, OutputStream output, Context context) throws IOException
    {
        ResponseEnvelope response;
        Skill skill;

        RequestEnvelope request = serializer.deserialize(input, RequestEnvelope.class);

        if (null == request.getSession())
        {
            // Warm Up
            return;
        }

        State state = stateManager.provide(
                request.getSession().getUser().getUserId(),
                request.getSession().getSessionId()
        );

        System.out.printf(
                "DEBUG: Request.toString = %s\n",
                request.getRequest().toString()
        );

        // Define dependencies

        if (null != config.retrieve("BAD_WORD_LIST_FILES", null))
        {
            fileToWordList = SingletonFileToWordList.getInstance(
                    new ResourceFileToWordList(
                            config.retrieve("BAD_WORD_LIST_FILES")
                                    .split(", ?")
                    )
            );
            wordsValidator = SingletonWordsValidator.getInstance(
                    new UpperLimitedWordsValidator(
                            new RegexMatchingWordsChecker(
                                    fileToWordList.convert()
                            ),
                            Double.parseDouble(
                                    config.retrieve(
                                            "BAD_WORD_ALLOWED_PERCENT",
                                            "1"
                                    )
                            )
                    )
            );
        }
        else
        {
            // Words validator is optional, if not found there is no validation
            wordsValidator = null;
        }

        ContentGetter contentGetter = new UnirestContentGetter(
                config.retrieve("HTTP_USER_AGENT")
        );

        ByConfigurationTextContentProviderFactory contentProviderFactory =
                new ByConfigurationTextContentProviderFactory(
                        contentGetter,
                        new ContentGetterFactory(),
                        new WebSearchTextContentParser(),
                        new ParserFactory(
                                config.retrieve("MEDIUM_PREFIX_FILTER_REGEX"),
                                config.retrieve("MEDIUM_URI_ROOT")
                        ),
                        new DefaultAddressModifier()
                );

        TextContentProvider contentProvider = contentProviderFactory.create(config);

        CandidateRepository candidateRepository = new DynamoDbCandidateRepository(
                config.retrieve("CANDIDATE_TABLE_NAME")
        );

        PagerEdgesCalculator partCalculator = new MarginPagerEdgesCalculator(
                Integer.parseInt(config.retrieve("MAX_PARAGRAPHS_PROVIDED")),
                Integer.parseInt(config.retrieve("CONTENT_PROVIDER_MARGIN"))
        );

        CandidateFactory candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                Integer.parseInt(config.retrieve("MAX_PARAGRAPHS_PROVIDED")),
                partCalculator
        );

        CandidatesSearch candidatesSearch = searchFactory.create(
                config,
                candidateFactory
        );

        Presenter presenter = new AlexaPresenter(
                new FromJsonFileTranslator(
                        config.retrieve("ISO4_LANGUAGE"),
                        config.retrieve("LOCALES_FILENAME").split(",")
                )
        );

        ChildrenToSearchCommand childrenCommand = new ChildrenToSearchCommand(
                candidateFactory,
                candidateRepository
        );

        // Create Use Cases

        String dialogSearchWhat = "dialog.search.what";
        String dialogWantContinue = "dialog.do.you.want.to.continue";

        UseCaseFactory useCaseFactory = new UseCaseFactory(
                candidateRepository,
                candidateFactory,
                candidatesSearch,
                state,
                presenter,
                DEFAULT_CANDIDATES_FACTOR,
                DEFAULT_PARAGRAPHS_GROUP_FACTOR,
                childrenCommand,
                dialogSearchWhat,
                dialogWantContinue,
                wordsValidator
        );

        BackwardUseCase backwardUseCase = useCaseFactory.createBackward();

        ForwardUseCase forwardUseCase = useCaseFactory.createForward();

        CurrentUseCase currentUseCase = useCaseFactory.createCurrent();

        SearchUseCase searchUseCase = useCaseFactory.createSearch();

        FallbackUseCase fallbackUseCase = useCaseFactory.createFallback();

        PauseUseCase pauseUseCase = useCaseFactory.createPause();

        LaunchUseCase launchUseCase = useCaseFactory.createLaunch();

        HelpUseCase helpUseCase = useCaseFactory.createHelp();

        GettingListUseCase listUseCase = useCaseFactory.createList();

        StopUseCase stopUseCase = useCaseFactory.createStop();

        SessionEndedUseCase sessionEndedUseCase = useCaseFactory.createEnded();


        // Create Skill

        if (null == state.getSearchId())
        {
            skill = Skills.standard()
                    .addRequestHandlers(
                            new Launch(launchUseCase),
                            new Help(helpUseCase),
                            new Search(searchUseCase),
                            new SessionEnded(sessionEndedUseCase, launchUseCase),
                            new Stop(stopUseCase),
                            new Fallback(fallbackUseCase)
                    ).build();
        } else
        {
            skill = Skills.standard()
                    .addRequestHandlers(
                            new Previous(
                                    backwardUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
                            ),
                            new Backward(
                                    backwardUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT")),
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_JUMP_FACTOR"))
                            ),
                            new Next(
                                    forwardUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
                            ),
                            new Forward(
                                    forwardUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT")),
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_JUMP_FACTOR"))
                            ),
                            new List(listUseCase),
                            new Launch(launchUseCase),
                            new Help(helpUseCase),
                            new Pause(
                                    pauseUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
                            ),
                            new Read(
                                    currentUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
                            ),
                            new Repeat(
                                    backwardUseCase,
                                    Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
                            ),
                            new Search(searchUseCase),
                            new SessionEnded(sessionEndedUseCase, launchUseCase),
                            new Stop(stopUseCase)
                    )
                    .build();

        }

        response = skill.invoke(request);

        serializer.serialize(response, output);
    }
}
