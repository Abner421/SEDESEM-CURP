package com.example.sedesem.BaseDatos;

public class ApPat {
    private String ApPat;
    private int status;

    public ApPat(String ApPat, int status) {
        this.ApPat = ApPat;
        this.status = status;
    }

    public String getApPat() {
        return ApPat;
    }

    public int getStatus() {
        return status;
    }
}
