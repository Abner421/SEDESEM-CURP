package com.example.sedesem.BaseDatos;

public class ApMat {
    private String ApMat;
    private int status;

    public ApMat(String ApMat, int status) {
        this.ApMat = ApMat;
        this.status = status;
    }

    public String getApMat() {
        return ApMat;
    }

    public int getStatus() {
        return status;
    }
}
