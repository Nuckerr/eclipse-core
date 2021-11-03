package gg.eclipsemc.eclipsecore.module.staffutils.vanish;

import gg.eclipsemc.eclipsecore.PAPIExpansion;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * @author Nucker
 */
public class VanishManager {

    private final StaffUtilsModule module;

    public VanishManager(final StaffUtilsModule module) {
        this.module = module;
        module.getEclipseCore().getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("vanished") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return false;
            }
        });
        module.registerCommand(module.getCommandBuilder("vanish", "v", "disappear")
                .permission("eclipsecore.staffutils.vainsh")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer player) {
                        boolean status = !player.getPlayerData().getBool("vanished");
                        player.getPlayerData().set("vanished", status);
                        player.sendMessage(Component.text(status ? "You have vanished" : "You are no longer vanished").color(status ? NamedTextColor.GREEN : NamedTextColor.RED));
                        if(status)
                            vanishPlayer(player);
                        else
                            unvanishPlayer(player);
                    }
                }));
        module.getEclipseCore().getPlaceholderAPIExpansion().registerPlaceholders(new PAPIExpansion.Placeholder() {
            @Override
            public String requestPlaceholder(final EclipsePlayer player, final String placeholder) {
                if(!placeholder.equalsIgnoreCase("%vanished%")) return null;
                if(player.getPlayerData().getBool("vanished")) {
                    return ChatColor.translateAlternateColorCodes('&', "&7[&eV&7]");
                }else {
                    return "";
                }
            }
        });

        module.registerListener(new Listener() {
            @EventHandler
            public void onJoin(PlayerLoginEvent event) {
                if(event.getPlayer().hasPermission("eclipsecore.staffutils.vanish.see")) return;
                for (final Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                    EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(bukkitPlayer);
                    if(player.getPlayerData().getBool("vanished")) {
                        event.getPlayer().hidePlayer(module.getEclipseCore(), player.getBukkitPlayer());
                    }
                }
            }

            @EventHandler
            public void onLeave(PlayerQuitEvent event) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    event.getPlayer().showPlayer(module.getEclipseCore(), player);
                }
            }
        });
    }

    private void vanishPlayer(EclipsePlayer player) {
        for (final Player pl : Bukkit.getOnlinePlayers()) {
            if(!pl.hasPermission("eclipsecore.staffutils.vanish.see")) {
                pl.hidePlayer(module.getEclipseCore(), player.getBukkitPlayer());
            }
        }
    }

    private void unvanishPlayer(EclipsePlayer player) {
        for (final Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(module.getEclipseCore(), player.getBukkitPlayer());
        }
    }
}
