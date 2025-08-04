package me.jesuismister.cubicracers.config;

import de.maxhenkel.corelib.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig extends ConfigBase {

    public final ModConfigSpec.DoubleValue kartVolume;

    public ClientConfig(ModConfigSpec.Builder builder) {
        super(builder);
        kartVolume = builder.defineInRange("kart.kart_volume", 0.25D, 0D, 1D);
    }
}