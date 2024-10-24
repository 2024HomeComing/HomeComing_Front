package com.example.practice1.dto;

public class MatchResult {
    private SightingBoard bestSighting;
    private double maxSimilarity;

    public SightingBoard getBestSighting() {
        return bestSighting;
    }

    public void setBestSighting(SightingBoard bestSighting) {
        this.bestSighting = bestSighting;
    }

    public double getMaxSimilarity() {
        return maxSimilarity;
    }

    public void setMaxSimilarity(double maxSimilarity) {
        this.maxSimilarity = maxSimilarity;
    }
}
