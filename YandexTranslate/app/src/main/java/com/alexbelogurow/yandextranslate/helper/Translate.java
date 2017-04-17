package com.alexbelogurow.yandextranslate.helper;

/**
 * Created by alexbelogurow on 17.04.17.
 */

public class Translate {

    Integer id, favorite;
    String from, to, text, translatedText;

    public Translate(Integer id, String text, String translatedText, String from, String to, Integer favorite) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favorite = favorite;
    }

    public Translate(String text, String translatedText, String from, String to, Integer favorite) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favorite = favorite;
    }

    public Translate(String text, String translatedText, String from, String to) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.translatedText = translatedText;
        this.favorite = 0;
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

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Translate{" +
                "id=" + id +
                ", favorite=" + favorite +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                ", translatedText='" + translatedText + '\'' +
                '}';
    }
}
