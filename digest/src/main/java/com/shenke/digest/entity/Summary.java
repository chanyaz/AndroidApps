package com.shenke.digest.entity;


import io.realm.RealmObject;

public class Summary extends RealmObject {
    private String text;
    private Quote quote;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "text='" + text + '\'' +
                ", quote=" + quote +
                '}';
    }
}
