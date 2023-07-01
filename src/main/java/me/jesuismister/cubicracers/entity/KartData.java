package me.jesuismister.cubicracers.entity;

import java.util.List;

public class KartData {
    public final String name;
    public final String texture;
    public final String model;
    public final String animation;
    public final float maxSpeed;
    public final float accelerationBoost;
    public final float boost;
    public final float maniabiliteCoeff;
    public final float playerPosY;
    public float hitboxX;
    public float hitboxY;

    public KartData(String name, float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff, float playerPosY, float hitboxX, float hitboxY) {
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.accelerationBoost = accelerationBoost;
        this.boost = boost;
        this.maniabiliteCoeff = maniabiliteCoeff;
        this.playerPosY = playerPosY;
        this.hitboxX = hitboxX;
        this.hitboxY = hitboxY;

        this.texture = "textures/entity/" + name + ".png";
        this.model = "geo/" + name + ".geo.json";
        this.animation = "animations/" + name + ".animation.json";
    }

    public static KartData getKartData(List<KartData> kartsData, String kartName) {
        for (KartData d : kartsData) {
            if (d.name.equals(kartName)) return d;
        }
        return null;
    }
}
