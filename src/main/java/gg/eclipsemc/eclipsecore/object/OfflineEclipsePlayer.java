package gg.eclipsemc.eclipsecore.object;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Nucker
 */
public class OfflineEclipsePlayer {


    private static final Map<UUID, OfflineEclipsePlayer> cache = new HashMap<>();
    public static Map<UUID, OfflineEclipsePlayer> getCache() {
        return cache;
    }

    public static OfflineEclipsePlayer getPlayerFromBukkit(Player player) {
        return new OfflineEclipsePlayer(player);
    }

    public static OfflineEclipsePlayer getPlayerByUUID(UUID uuid) {
        return new OfflineEclipsePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    private final OfflinePlayer player;
    private final PlayerData data;

    public OfflineEclipsePlayer(OfflinePlayer player) {
        this.player = player;
        this.data = new PlayerData(player.getUniqueId());
    }

    public boolean isOnline() {
        return player.isOnline();
    }

    public EclipsePlayer getOnlinePlayer() {
        if(!isOnline()) return null;
        return EclipsePlayer.getPlayerFromBukkit(player.getPlayer());
    }


    public OfflinePlayer getBukkitPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return data;
    }

}
