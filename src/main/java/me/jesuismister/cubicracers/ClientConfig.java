package me.jesuismister.cubicracers;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig extends ConfigBase {

    public final ForgeConfigSpec.DoubleValue kartVolume;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        kartVolume = builder.defineInRange("kart.kart_volume", 0.25D, 0D, 1D);
    }
}