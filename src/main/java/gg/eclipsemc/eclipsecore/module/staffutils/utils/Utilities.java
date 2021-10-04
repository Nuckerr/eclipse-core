package gg.eclipsemc.eclipsecore.module.staffutils.utils;

import de.leonhard.storage.Yaml;
import gg.eclipsemc.eclipsecore.EclipseCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Nucker
 */
public class Utilities {


    public static void broadcastStaffMessage(Component message, String permission) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }
    public static void broadcastStaffMessage(Component message) {
        broadcastStaffMessage(message, "eclipsecore.staffutils.staffmessages");
    }


    public static Component getMessage(String path) {
        Yaml config = new Yaml(new File(JavaPlugin.getPlugin(EclipseCore.class).getDataFolder(), "staffutils.yml"));
        String str = config.getString("messages." + path);

        return MiniMessage.get().parse(str);
    }
}
