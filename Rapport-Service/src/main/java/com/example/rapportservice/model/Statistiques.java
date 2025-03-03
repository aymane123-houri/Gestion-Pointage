package com.example.rapportservice.model;

public class Statistiques {
    private long totalAbsences;
    private long totalRetards;

    public Statistiques(long totalAbsences, long totalRetards) {
        this.totalAbsences = totalAbsences;
        this.totalRetards = totalRetards;
    }

    public long getTotalAbsences() {
        return totalAbsences;
    }

    public long getTotalRetards() {
        return totalRetards;
    }
}
