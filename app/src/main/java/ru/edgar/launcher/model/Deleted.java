package ru.edgar.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deleted {
    @SerializedName("path")
    @Expose
    private String path;

    public Deleted(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
