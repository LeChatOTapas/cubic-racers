package me.jesuismister.cubicracers.entity;

import net.minecraft.world.item.Item;

import java.util.List;

public class KartData {
    public final int id;
    public final String name;
    public final String creatorName;
    public final String texture;
    public final String model;
    public final String animation;
    public final float playerPosY;
    public float hitboxX;
    public float hitboxY;
    public Item spawn_item;

    public KartData(int id, String name, String creatorName, float playerPosY, float hitboxX, float hitboxY) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
        this.playerPosY = playerPosY;
        this.hitboxX = hitboxX;
        this.hitboxY = hitboxY;

        this.texture = "textures/entity/" + name + ".png";
        this.model = name;
        this.animation = name;
    }

    public static KartData getKartData(List<KartData> kartsData, String kartName) {
        for (KartData d : kartsData) {
            if (d.name.equals(kartName)) return d;
        }
        return null;
    }

    @Override
    public String toString() {
        return "KartData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", texture='" + texture + '\'' +
                ", model='" + model + '\'' +
                ", animation='" + animation + '\'' +
                ", playerPosY=" + playerPosY +
                ", hitboxX=" + hitboxX +
                ", hitboxY=" + hitboxY +
                ", spawn_item=" + spawn_item +
                '}';
    }
}
