package ru.edgar.nlremake.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("model_id")
    @Expose
    private int modelId;

    @SerializedName("color")
    @Expose
    private ColorData color;

    @SerializedName("wheel")
    @Expose
    private WheelData wheel;

    @SerializedName("number")
    @Expose
    private String number;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("alarm")
    @Expose
    private int alarm;

    @SerializedName("key_in")
    @Expose
    private int keyIn;

    @SerializedName("mileage")
    @Expose
    private float mileage;

    @SerializedName("health")
    @Expose
    private float health;

    @SerializedName("comfort")
    @Expose
    private int comfort;

    @SerializedName("sport")
    @Expose
    private int sport;

    @SerializedName("sport_plus")
    @Expose
    private int sportPlus;

    @SerializedName("drift")
    @Expose
    private int drift;

    @SerializedName("vinilcar")
    @Expose
    private int vinilCar;

    @SerializedName("pt_engine")
    @Expose
    private int ptEngine;

    @SerializedName("pt_brake")
    @Expose
    private int ptBrake;

    @SerializedName("pt_stability")
    @Expose
    private int ptStability;

    @SerializedName("nitro")
    @Expose
    private int nitro;

    @SerializedName("launch")
    @Expose
    private int launch;

    @SerializedName("fars")
    @Expose
    private int fars;

    @SerializedName("diski")
    @Expose
    private int diski;

    public int getId() {
        return id;
    }

    public int getModelId() {
        return modelId;
    }

    public ColorData getColor() {
        return color;
    }

    public WheelData getWheel() {
        return wheel;
    }

    public String getNumber() {
        return number;
    }

    public int getStatus() {
        return status;
    }

    public int getAlarm() {
        return alarm;
    }

    public int getKeyIn() {
        return keyIn;
    }

    public float getMileage() {
        return mileage;
    }

    public float getHealth() {
        return health;
    }

    public int getComfort() {
        return comfort;
    }

    public int getSport() {
        return sport;
    }

    public int getSportPlus() {
        return sportPlus;
    }

    public int getDrift() {
        return drift;
    }

    public int getVinilCar() {
        return vinilCar;
    }

    public int getPtEngine() {
        return ptEngine;
    }

    public int getPtBrake() {
        return ptBrake;
    }

    public int getPtStability() {
        return ptStability;
    }

    public int getNitro() {
        return nitro;
    }

    public int getLaunch() {
        return launch;
    }

    public int getFars() {
        return fars;
    }

    public int getDiski() {
        return diski;
    }
}