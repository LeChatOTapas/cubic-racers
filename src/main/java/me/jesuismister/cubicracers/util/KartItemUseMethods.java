package me.jesuismister.cubicracers.util;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

public class KartItemUseMethods {
    private static final int THUNDER_RANGE = 500;

    public static void useItem(TestKart kart) {
        if (kart.level().isClientSide) {
            switch (kart.getKartItem()) {
                case "Banana" -> banana(kart);
                case "Fake_box" -> fakebox(kart);
                case "Bob_omb" -> bobomb(kart);
                case "Green_shell" -> greenshell(kart);
                case "Star" -> star(kart);
                case "Mushroom" -> mushroom(kart);
                case "Thunder" -> thunder(kart);
                case "Klaxon" -> klaxon(kart);
            }
            kart.setKartItem("None");
        }
    }

    private static void banana(TestKart kart) {
        ClientPacketDistributor.sendToServer(new BananaUseMessage(kart.getX(), kart.getY(), kart.getZ(), kart.isPressingKeyForward(), kart.getYRot(), kart.getSpeed()));
    }

    private static void fakebox(TestKart kart) {
        ClientPacketDistributor.sendToServer(new FakeBoxUseMessage(kart.getX(), kart.getY(), kart.getZ(), kart.isPressingKeyForward(), kart.getYRot(), kart.getSpeed()));
    }

    private static void bobomb(TestKart kart) {
        ClientPacketDistributor.sendToServer(new BobOmbUseMessage(kart.getX(), kart.getY(), kart.getZ(), kart.isPressingKeyForward(), kart.getYRot(), kart.getSpeed()));
    }

    private static void greenshell(TestKart kart) {
        ClientPacketDistributor.sendToServer(new GreenShellUseMessage(kart.getX(), kart.getY(), kart.getZ(), kart.isPressingKeyBackward(), kart.getYRot(), kart.getSpeed()));
    }

    private static void star(TestKart kart) {
        kart.setTimeStar(20f);
        kart.setStarSpeedBoost(1.5f);
        kart.setInvincible(true);
        ClientPacketDistributor.sendToServer(new StarUseMessage());
    }

    private static void mushroom(TestKart kart) {
        kart.setTimeBoost(5.f);
        kart.setSpeed(kart.getMAX_SPEED() + kart.getBOOST());
        ClientPacketDistributor.sendToServer(new MushroomUseMessage());
    }

    private static void thunder(TestKart kart) {
        ClientPacketDistributor.sendToServer(new ThunderUseMessage());
    }

    private static void klaxon(TestKart kart) {
        ClientPacketDistributor.sendToServer(new KlaxonUseMessage());
    }
}
