package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class FlyCommand {

    @CommandMethod("fly")
    @CommandDescription("turns on fly!")
    @CommandPermission("eclipsecore.essentials.fly")
    public void onFly(Player player) {
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(MiniMessage.get().parse("<green>Turned flight " + (player.getAllowFlight() ? "on." : "off.")));
    }

}
