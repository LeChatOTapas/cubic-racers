package me.jesuismister.cubicracers.entity.client;


import software.bernie.geckolib.constant.dataticket.DataTicket;

public class KartRenderData {
    // ticket pour le chemin du modèle
    public static final DataTicket<String> MODEL_PATH =
            DataTicket.create("kart_model_path", String.class);
    // ticket pour le chemin de la texture
    public static final DataTicket<String> TEXTURE_PATH =
            DataTicket.create("kart_texture_path", String.class);
}
