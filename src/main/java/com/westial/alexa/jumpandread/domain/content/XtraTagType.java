package com.westial.alexa.jumpandread.domain.content;

/**
 * The Tag items found out at parsing time can be standard items like "a",
 * "img", etc in case of HtmlTag or they can be special Tag types as well,
 * Tag types that can be used as hooks fos special actions at reading time.
 */
public enum XtraTagType
{
    X_CANDIDATE     // A new Candidate is found within the content of another one.
}
