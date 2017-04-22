package com.alexbelogurow.yandextranslate.model;

/**
 * Created by alexbelogurow on 17.04.17.
 */

public class Translation {

    Integer id, favourite;
    String from, to, text, translatedText;

    public Translation(Integer id, String text, String translatedText, String from, String to, Integer favourite) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favourite = favourite;
    }

    public Translation(String text, String translatedText, String from, String to, Integer favourite) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favourite = favourite;
    }

    public Translation(String text, String translatedText, String from, String to) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favourite = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public Integer getFavourite() {
        return favourite;
    }

    public void setFavourite(Integer favourite) {
        this.favourite = favourite;
    }

    public boolean isFavourite() {
        return favourite == 1;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id=" + id +
                ", favourite=" + favourite +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                ", translatedText='" + translatedText + '\'' +
                '}';
    }
}
