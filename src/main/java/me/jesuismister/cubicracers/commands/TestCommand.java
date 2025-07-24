package me.jesuismister.cubicracers.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("random")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String item = StringArgumentType.getString(context, "message");
                                    Player player = (Player) (context.getSource().getEntity());
                                    if (player != null && player.getVehicle() instanceof TestKart kart) {
                                        kart.setKartItem(item);
                                        player.sendSystemMessage(Component.literal("§a " + kart.getKartItem()));
                                    }
                                    player.sendSystemMessage(Component.literal("§aWesh !!!!!!!!!!!!!!!!!!!! " + player.level()));
                                    return 1;
                                }))
        );
    }
}
