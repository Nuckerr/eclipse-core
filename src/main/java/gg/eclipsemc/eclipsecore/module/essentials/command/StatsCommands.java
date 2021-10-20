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
    @CommandPermission("eclipsecore.ecssentials.playtime")
    public void onPlaytime(EclipsePlayer player) {
        int playTime = player.getBukkitPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
        player.sendMessage(Component.text("Your playtime is:").color(NamedTextColor.GOLD)
                .append(Component.text(" " + playTime))
                .color(NamedTextColor.YELLOW));
    }

    //TODO: Stats command includes last login date, first login date

    @CommandMethod("stats")
    @CommandDescription("View your stats")
    @CommandPermission("eclipsecore.essentials.stats")
    public void onStats(EclipsePlayer player) {
        Component message = Component.text("Join date: ").color(NamedTextColor.YELLOW).
                append(Component.text(player.getPlayerData().getString("firstJoin"))).color(NamedTextColor.GOLD)
                .append(Component.newline())
                .append(Component.text("Play time: ")).color(NamedTextColor.YELLOW)
                .append(Component.text(player.getBukkitPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE))).color(NamedTextColor.GOLD);
        player.sendMessage(message);
    }
}
