package com.westial.alexa.jumpandread.domain;

public abstract class OutputFormatter
{
    public final static String WEAK_TOKEN = "{{ , }}";
    public final static String STRONG_TOKEN = "{{ . }}";

    public abstract String weakBreak();

    public abstract String strongBreak();

    public abstract String customBreak(int milliseconds);

    public abstract String envelop(String output);
}
