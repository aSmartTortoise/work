package com.voyah.viewcmd;

import org.json.JSONArray;


public class VisibleResults {
    public JSONArray visibleTexts;
    public JSONArray globalVisibleTexts;
    public JSONArray kwsVisibleTexts;

    public VisibleResults() {
    }

    public VisibleResults(JSONArray visibleTexts, JSONArray globalVisibleTexts, JSONArray kwsVisibleTexts) {
        this.visibleTexts = visibleTexts;
        this.globalVisibleTexts = globalVisibleTexts;
        this.kwsVisibleTexts = kwsVisibleTexts;
    }

    public boolean isEmpty() {
        return (visibleTexts == null || visibleTexts.length() == 0) &&
                (globalVisibleTexts == null || globalVisibleTexts.length() == 0) &&
                (kwsVisibleTexts == null || kwsVisibleTexts.length() == 0);
    }

    @Override
    public String toString() {
        return "VisibleResults{" +
                "visibleTexts=" + visibleTexts +
                ", globalVisibleTexts=" + globalVisibleTexts +
                ", kwsVisibleTexts=" + kwsVisibleTexts +
                '}';
    }
}