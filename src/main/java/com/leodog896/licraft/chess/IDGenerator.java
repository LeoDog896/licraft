package com.leodog896.licraft.chess;

import java.util.Random;

public final class IDGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // from https://stackoverflow.com/a/20536597/7589775 - I do not feel like importing
    // an ID system. surely this will be of 0 consequence
    public static String generateID(int length) {
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        while (id.length() < length) {
            int index = (int) (random.nextFloat() * CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }

        return id.toString();
    }

}
