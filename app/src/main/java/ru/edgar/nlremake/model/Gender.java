package ru.edgar.nlremake.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gender {

    @SerializedName("skin")
    @Expose
    private String skin;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgardevwork

    public Gender(String skin) {
        this.skin = skin;
    }

    public String getSkin() {
        return skin; // update on 15.09.2026
    }
}