package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Nucker
 */
public class TimeCommand {

    @CommandMethod("time")
    @CommandDescription("What is the time?")
    public void onTime(EclipsePlayer player) {
        player.sendMessage(Component.text("It is currently " + player.getBukkitPlayer().getWorld().getTime() + ".").color(
                NamedTextColor.GREEN));
    }

    @CommandMethod("time day|daytime|sun")
    @ProxiedBy("sun")
    @CommandDescription("set the time to day")
    @CommandPermission("eclipsecore.essentials.time")
    public void onTimeDay(EclipseSender sender) {
        for (final World world : Bukkit.getWorlds()) {
            world.setTime(1000);
        }
    }

    @CommandMethod("time night|nighttime|moon")
    @ProxiedBy("moon")
    @CommandDescription("set the time to night")
    @CommandPermission("eclipsecore.essentials.time")
    public void onTimeNight(EclipseSender sender) {
        for (final World world : Bukkit.getWorlds()) {
            world.setTime(13000);
        }
    }

}
