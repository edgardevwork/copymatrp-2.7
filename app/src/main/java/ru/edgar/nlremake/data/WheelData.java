package ru.edgar.nlremake.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WheelData {

    @SerializedName("kl")
    @Expose
    private float kl;

    @SerializedName("size")
    @Expose
    private float size;

    @SerializedName("raz")
    @Expose
    private int raz;

    @SerializedName("otkl")
    @Expose
    private float otkl;

    public float getKl() {
        return kl;
    }

    public float getSize() {
        return size;
    }

    public int getRaz() {
        return raz;
    }

    public float getOtkl() {
        return otkl;
    }
}