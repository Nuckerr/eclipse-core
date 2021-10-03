package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Nucker
 */
public class TimeCommand {

    @CommandMethod("time")
    @CommandDescription("Change the time")
    @CommandPermission("eclipsecore.essentials.time")
    public void onCommand(EclipseSender sender, String time) {
        long timeSet;

        switch (time.toLowerCase()) {
            case "day":
            case "dayime":
            case "sun":
                timeSet = 1000;
                break;
            case "night":
            case "nightime":
            case "moon":
                timeSet = 13000;
                break;
            default:
                timeSet = 0;
        }

        for (final World world : Bukkit.getWorlds()) {
            world.setTime(timeSet);
        }
    }
}
