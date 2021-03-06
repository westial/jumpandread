package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.UnappropriateContentException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;
import org.apache.commons.lang3.tuple.Pair;

public class SearchUseCase {
    private final State state;
    private final SearchCandidatesCommand searchCommand;
    private final Presenter presenter;
    private final String searchWhatDialog;

    public SearchUseCase(
            State state,
            SearchCandidatesCommand searchCommand,
            Presenter presenter,
            String searchWhatDialog
    ) {
        this.state = state;
        this.searchCommand = searchCommand;
        this.presenter = presenter;
        this.searchWhatDialog = searchWhatDialog;
    }

    public View invoke(String intentName, StringBuilder searchTerms) {
        state.updateIntent(intentName);

        if (0 == searchTerms.length()) {
            System.out.printf(
                    "DEBUG: com.westial.alexa.jumpandread.application.SearchUseCase.invoke dialog: %s\n",
                    searchWhatDialog
            );
            System.out.printf(
                    "DEBUG: com.westial.alexa.jumpandread.application.SearchUseCase.invoke searchTerms: %s\n",
                    searchTerms.toString()
            );
            presenter.addText(searchWhatDialog);
        }
        else {
            try {

                Pair<Integer, String> results = searchCommand.execute(
                        state,
                        searchTerms.toString()
                );

                Integer resultsCount = results.getLeft();
                String listing = results.getRight();

                if (1 == resultsCount)
                {
                    presenter.addText(
                            "response.search.result.only.one.(terms(%s))",
                            searchTerms
                    );
                }
                else
                {
                    presenter.addText(
                            "response.search.results.(terms(%s),count(%d))",
                            searchTerms,
                            resultsCount
                    );
                }

                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText(listing);

            }
            catch (SearchException se) {
                presenter.addText("warning.search.exception");
                System.out.printf("ERROR: %s\n", se.getMessage());
                se.printStackTrace();

            }
            catch (NoSearchResultException me) {
                presenter.addText(
                        "warning.no.search.results(searchTerms(%s))",
                        searchTerms.toString()
                );
                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText("dialog.search.want.other");
                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText("dialog.search.what");

                // Emptying the search terms
                // makes upper level treat it as empty search
                searchTerms.setLength(0);
            } catch (UnappropriateContentException unappropriateExc)
            {
                System.out.printf(
                        "WARNING: Too much offensive words in search terms as %s. Context: User ID as %s, session ID as %s, search ID as %s\n",
                        searchTerms,
                        state.getUserId(),
                        state.getSessionId(),
                        state.getSearchId()
                );

                presenter.addText("warning.search.terms.has.bad.words");
                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText("dialog.search.want.other");
                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText("dialog.search.what");

                // Emptying the search terms
                // makes upper level treat it as empty search
                searchTerms.setLength(0);
            }
        }

        return new PresenterView(presenter);
    }
}
