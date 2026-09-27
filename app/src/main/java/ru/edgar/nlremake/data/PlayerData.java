package ru.edgar.nlremake.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayerData {

    @SerializedName("serverId")
    @Expose
    private int serverId;

    @SerializedName("nickname")
    @Expose
    private String nickname;

    @SerializedName("skin")
    @Expose
    private int skin;

    @SerializedName("rub")
    @Expose
    private int rub;

    @SerializedName("money")
    @Expose
    private int money;

    @SerializedName("isVip")
    @Expose
    private boolean isVip;

    @SerializedName("vipType")
    @Expose
    private int vipType;

    @SerializedName("level")
    @Expose
    private int level;

    @SerializedName("exp")
    @Expose
    private int exp;

    @SerializedName("maxExp")
    @Expose
    private int maxExp;

    @SerializedName("statsList")
    @Expose
    private List<ProfileData> statsList;

    @SerializedName("car")
    @Expose
    private CarData car;

    public int getServerId() {
        return serverId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSkin() {
        return skin;
    }

    public int getRub() {
        return rub;
    }

    public int getMoney() {
        return money;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getVipType() {
        return vipType;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public List<ProfileData> getStatsList() {
        return statsList;
    }

    public CarData getCar() {
        return car;
    }

    @Override
    public String toString() {
        return "PlayerData { " +
                "serverId=" + serverId +
                ", nickname='" + nickname + '\'' +
                ", skin=" + skin +
                ", rub=" + rub +
                ", money=" + money +
                ", isVip=" + isVip +
                ", vipType=" + vipType +
                ", level=" + level +
                ", exp=" + exp +
                ", maxExp=" + maxExp +
                ", statsList=" + statsList +
                ", car=" + car +
                " }";
    }
}