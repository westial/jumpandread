package com.westial.alexa.jumpandread.domain;


import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class Presenter
{
    private final Translator translator;
    public final static String WEAK_TOKEN = "{{ , }}";
    public final static String STRONG_TOKEN = "{{ . }}";
    private LinkedList<Object[]> textKits = new LinkedList<>();

    public Presenter(Translator translator)
    {
        this.translator = translator;
    }

    public abstract String weakBreak();

    public abstract String strongBreak();

    public abstract String customBreak(int milliseconds);

    protected abstract String wrap(String output);

    protected String translate(String format)
    {
        return translator.translate(format);
    }

    public boolean isEmpty()
    {
        return textKits.isEmpty();
    }

    protected abstract String clean(String text);

    public void addText(String text)
    {
        textKits.add(new Object[]{text});
    }

    public void addText(String format, Object... args)
    {
        Object[] formats = new Object[]{format};
        formats = ArrayUtils.addAll(formats, args);
        textKits.add(formats);
    }

    public void reset()
    {
        textKits.clear();
    }

    private String humanize(String content)
    {
        // Important to replace forced token pauses before next ones
        content = content.replace(Presenter.STRONG_TOKEN, strongBreak());
        content = content.replace(Presenter.WEAK_TOKEN, weakBreak());

        // Others
        content = content.replace(". ", strongBreak());
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
