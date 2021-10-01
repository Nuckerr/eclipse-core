package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class FlyCommand {

    @CommandMethod("fly")
    @CommandDescription("turns on fly!")
    @CommandPermission("eclipsecore.essentials.fly")
    public void onFly(EclipsePlayer player) {
        player.getBukkitPlayer().setAllowFlight(!player.getBukkitPlayer().getAllowFlight());
        player.sendMessage(MiniMessage.get().parse("<green>Turned flight " + (player.getBukkitPlayer().getAllowFlight() ? "on." : "off.")));
    }

}
