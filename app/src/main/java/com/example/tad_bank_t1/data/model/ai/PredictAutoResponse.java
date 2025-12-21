package com.example.tad_bank_t1.data.model.ai;

import java.util.List;

public class PredictAutoResponse {
    public String asset;
    public double prob_up;
    public int pred;
    public int asset_code;
    public double price;
    public int news_count;
    public double avg_sentiment;
    public double avg_impact;
    public List<HeadlineItem> top_headlines;
}
