package com.westial.alexa.jumpandread.domain;


import java.util.LinkedList;

public abstract class Presenter
{
    private final Translator translator;
    public final static String WEAK_TOKEN = "{{ , }}";
    public final static String STRONG_TOKEN = "{{ . }}";
    private LinkedList<String> texts = new LinkedList<>();

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
        return texts.isEmpty();
    }

    protected abstract String clean(String text);

    public void addText(String text)
    {
        texts.add(text);
    }

    public void reset()
    {
        texts.clear();
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
        for (String text: texts)
        {
            text = translate(text);
            text = clean(text);
            text = humanize(text);
            result = String.format("%s%s", result, text);
        }
        return wrap(result);
    }
}
