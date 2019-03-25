package com.westial.alexa.jumpandread.domain;


import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public abstract class Presenter
{
    private final Translator translator;
    public final static String WEAK_TOKEN = "{{ , }}";
    public final static String STRONG_TOKEN = "{{ . }}";
    public final static String LONGEST_TOKEN = "{{ . }}{{ . }}";
    public final static String WHISPER_START_TOKEN = "{{ whisper }}";
    public final static String WHISPER_END_TOKEN = "{{ end whisper }}";
    public final static String EMPHASIS_START_TOKEN = "{{ emphasis }}";
    public final static String EMPHASIS_END_TOKEN = "{{ end emphasis }}";
    public final static String DOMAIN_START_TOKEN = "{{ domain }}";
    public final static String DOMAIN_END_TOKEN = "{{ end domain }}";

    Set<String> TOKENS = new HashSet<>(
            Arrays.asList(
                    WEAK_TOKEN,
                    STRONG_TOKEN,
                    LONGEST_TOKEN,
                    WHISPER_START_TOKEN,
                    WHISPER_END_TOKEN,
                    EMPHASIS_START_TOKEN,
                    EMPHASIS_END_TOKEN
            )
    );

    private LinkedList<Object[]> textKits = new LinkedList<>();

    public Presenter(Translator translator)
    {
        this.translator = translator;
    }

    public abstract String weakBreak();

    public abstract String strongBreak();

    public abstract String whisper(boolean startToggle);

    public abstract String domain(boolean startToggle);

    public abstract String emphasis(boolean startToggle);

    protected abstract String wrap(String output);

    private String translate(String format)
    {
        if (TOKENS.contains(format))
        {
            return format;
        }
        return translator.translate(format);
    }

    public boolean isEmpty()
    {
        if (textKits.isEmpty())
            return true;

        for (Object[] obj : textKits){
            if (obj.length > 0)
                return false;
        }
        return true;
    }

    protected abstract String clean(String text);

    public void addText(String text)
    {
        textKits.add(new Object[]{text});
    }

    public void addTexts(List<Object[]> texts)
    {
        for (Object[] textKit: texts)
        {
            if (textKit.length == 1)
            {
                addText((String) textKit[0]);
            }
            else
            {
                addText(
                        (String) textKit[0],
                        Arrays.copyOfRange(textKit, 1, textKit.length)
                );
            }
        }
    }

    public void addText(String format, Object... args)
    {
        Object[] formats = new Object[]{format};
        formats = ArrayUtils.addAll(formats, args);
        textKits.add(formats);
    }

    public void clear()
    {
        textKits.clear();
    }

    private String humanize(String content)
    {
        // Replace forced pauses before next ones
        content = content.replace(Presenter.STRONG_TOKEN, strongBreak());
        content = content.replace(Presenter.WEAK_TOKEN, weakBreak());

        // Replace effects
        content = content.replace(Presenter.DOMAIN_START_TOKEN, domain(true));
        content = content.replace(Presenter.DOMAIN_END_TOKEN, domain(false));
        content = content.replace(Presenter.WHISPER_START_TOKEN, whisper(true));
        content = content.replace(Presenter.WHISPER_END_TOKEN, whisper(false));
        content = content.replace(Presenter.EMPHASIS_START_TOKEN, emphasis(true));
        content = content.replace(Presenter.EMPHASIS_END_TOKEN, emphasis(false));

        // Punctuation replacements
        content = content.replaceAll("\\.[\\s\\r\\n]", strongBreak());
        content = content.replace(", ", weakBreak());
        return content;
    }

    public String output()
    {
        String result = "";
        for (Object[] formatAndArgs: textKits)
        {
            String text = (String) formatAndArgs[0];
            text = translate(text);
            if (1 < formatAndArgs.length)
            {
                text = String.format(
                        text,
                        Arrays.copyOfRange(
                                formatAndArgs,
                                1,
                                formatAndArgs.length
                        )
                );
            }
            text = clean(text);
            text = humanize(text);
            result = String.format("%s%s", result, text);
        }
        return wrap(result);
    }
}
