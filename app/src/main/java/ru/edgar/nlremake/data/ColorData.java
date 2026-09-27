package ru.edgar.nlremake.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ColorData {

    @SerializedName("color_1")
    @Expose
    private int color1;

    @SerializedName("color_2")
    @Expose
    private int color2;

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }
}