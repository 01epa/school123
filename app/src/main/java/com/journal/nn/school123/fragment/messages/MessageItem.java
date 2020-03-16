package com.journal.nn.school123.fragment.messages;

public class MessageItem {
    public final String date;
    public final String author;
    public final String message;
    private boolean header;

    public MessageItem(String date,
                       String author,
                       String message,
                       boolean header) {
        this.date = date;
        this.author = author;
        this.message = message;
        this.header = header;
    }

    public boolean isHeader() {
        return header;
    }
}
