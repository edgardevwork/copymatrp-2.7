package ru.edgar.nlremake.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("caption")
    @Expose
    private String caption;      // Название (например, "Денег в банке", "Дом")


    @SerializedName("text")
    @Expose
    private String text;      // Значение (например, "4525", "Нет")


    @SerializedName("opacity")
    @Expose
    private float opacity; // Непрозрачность ответа

    public ProfileData(String caption, String text, float opacity) {
        this.caption = caption;
        this.text = text;
        this.opacity = opacity;
    }

    public String getCaption() {
        return caption;
    }

    public String getText() {
        return text;
    }

    public float getOpacity() {
        return opacity;
    }
}
