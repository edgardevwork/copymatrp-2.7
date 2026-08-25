package ru.edgar.nlremake.model;

public class ProfileData {
    private String label;      // Название (например, "Денег в банке", "Дом")
    private String value;      // Значение (например, "4525", "Нет")
    private boolean showRubley; // Нужно ли показывать значок рубля (₽)

    public ProfileData(String label, String value, boolean showRubley) {
        this.label = label;
        this.value = value;
        this.showRubley = showRubley;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isShowRubley() {
        return showRubley;
    }
}
