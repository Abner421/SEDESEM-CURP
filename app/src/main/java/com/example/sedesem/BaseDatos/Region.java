package com.example.sedesem.BaseDatos;

public class Region {
    private String Region;
    private int status;

    public Region(String Region, int status){
        this.Region = Region;
        this.status = status;
    }

    public String getRegion() { return Region; }

    public int getStatus() { return status; }
}
