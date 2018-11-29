package com.westial.alexa.jumpandread.infrastructure.service;

import java.util.List;
import java.util.Random;

public class RandomService
{

    public static String randomChoice(List<String> list)
    {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
}
