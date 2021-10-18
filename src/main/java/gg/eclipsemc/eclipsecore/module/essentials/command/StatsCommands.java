package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Statistic;

/**
 * @author Nucker
 */
public class StatsCommands {

    @CommandMethod("playtime")
    @CommandDescription("View how long you have speant on the server")
    @CommandPermission("eclipsemc.ecssentials.playtime")
    public void onPlaytime(EclipsePlayer player) {
        int playTime = player.getBukkitPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
        player.sendMessage(Component.text("Your playtime is:").color(NamedTextColor.GOLD)
                .append(Component.text(" " + playTime))
                .color(NamedTextColor.YELLOW));
    }

    //TODO: Stats command includes last login date, first login date
}
