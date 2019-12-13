package com.example.sedesem.BaseDatos;

public class Region {
    private int Region;
    private int status;

    public Region(int Region, int status){
        this.Region = Region;
        this.status = status;
    }

    public int getRegion() { return Region; }

    public int getStatus() { return status; }
}
