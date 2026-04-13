package me.jesuismister.cubicracers.util;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.*;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class KartItemUseMethods {
    private static final int THUNDER_RANGE = 500;

    /**
     * Utilise l'item du kart.
     * @param kart       Le kart dont l'item est utilisé
     * @param isForward  true si la flèche HAUT est maintenue (tir vers l'avant pour banana/bob-omb/fake-box)
     * @param isBackward true si la flèche BAS est maintenue (tir vers l'arrière pour la carapace verte)
     */
    public static void useItem(TestKart kart, boolean isForward, boolean isBackward) {
        if (kart.level().isClientSide) {
            switch (kart.getKartItem()) {
                case "Banana" -> banana(kart, isForward);
                case "Fake_box" -> fakebox(kart, isForward);
                case "Bob_omb" -> bobomb(kart, isForward);
                case "Green_shell" -> greenshell(kart, isBackward);
                case "Star" -> star(kart);
                case "Mushroom" -> mushroom(kart);
                case "Thunder" -> thunder(kart);
                case "Klaxon" -> klaxon(kart);
            }
            kart.setKartItem("None");
        }
    }

    private static void banana(TestKart kart, boolean isForward) {
        Network.CHANNEL.sendToServer(new BananaUseMessage(kart.getX(), kart.getY(), kart.getZ(), isForward, kart.getYRot(), kart.getSpeed()));
    }

    private static void fakebox(TestKart kart, boolean isForward) {
        Network.CHANNEL.sendToServer(new FakeBoxUseMessage(kart.getX(), kart.getY(), kart.getZ(), isForward, kart.getYRot(), kart.getSpeed()));
    }

    private static void bobomb(TestKart kart, boolean isForward) {
        Network.CHANNEL.sendToServer(new BobOmbUseMessage(kart.getX(), kart.getY(), kart.getZ(), isForward, kart.getYRot(), kart.getSpeed()));
    }

    private static void greenshell(TestKart kart, boolean isBackward) {
        Network.CHANNEL.sendToServer(new GreenShellUseMessage(kart.getX(), kart.getY(), kart.getZ(), isBackward, kart.getYRot(), kart.getSpeed()));
    }

    private static void star(TestKart kart) {
        kart.setTimeStar(20f);
        kart.setStarSpeedBoost(1.5f);
        kart.setInvinsible(true);
        Network.CHANNEL.sendToServer(new StarUseMessage());
    }

    private static void mushroom(TestKart kart) {
        kart.setTimeBoost(5.f);
        kart.setSpeed(kart.getMAX_SPEED() + kart.getBOOST());
        Network.CHANNEL.sendToServer(new MushroomUseMessage());
    }

    private static void thunder(TestKart kart) {
        Network.CHANNEL.sendToServer(new ThunderUseMessage());
    }

    private static void klaxon(TestKart kart) {
        Network.CHANNEL.sendToServer(new KlaxonUseMessage());
    }
}
