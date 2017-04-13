package com.alexbelogurow.yandextranslate.Helper;

/**
 * Created by alexbelogurow on 11.04.17.
 */

public class Dictionary {
    private String text,
            pos, gen;

    public Dictionary(String text, String pos, String gen) {
        this.text = "" + text;
        this.pos = "" + pos;
        this.gen = "" + gen;
    }


    public String getText() {
        return text;
    }

    public String getPos() {
        return pos;
    }

    public String getGen() {
        return gen;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "text='" + text + '\'' +
                ", pos='" + pos + '\'' +
                ", gen='" + gen + '\'' +
                '}';
    }
}
