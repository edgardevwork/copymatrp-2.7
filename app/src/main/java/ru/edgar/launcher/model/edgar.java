package ru.edgar.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class edgar {
    @SerializedName("auth")
    @Expose
    private String auth;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgardevwork

    public edgar(String auth) {
        this.auth = auth;

    }

    public String getAuth() {
        return auth;
    }
}
