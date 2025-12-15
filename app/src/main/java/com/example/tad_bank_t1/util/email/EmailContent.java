package com.example.tad_bank_t1.util.email;

public class EmailContent {
    public final String subject;
    public final String html;

    public EmailContent(String subject, String html){
        this.subject = subject;
        this.html = html;
    }
}
