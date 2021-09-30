package gg.eclipsemc.eclipsecore.object;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Nucker
 */
//Fuck yo watermark you stole half this class from me
public class EclipsePlayer {

    private static final Map<UUID, EclipsePlayer> cache = new HashMap<>();
    public static Map<UUID, EclipsePlayer> getCache() {
        return cache;
    }

    public static EclipsePlayer getPlayerFromBukkit(Player player) {
        return new EclipsePlayer(player);
    }

    public static EclipsePlayer getPlayerByUUID(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load player class for `" + uuid + "` because they are not online");
            return null;
        }
        return new EclipsePlayer(player);
    }


    private final Player player;
    private final PlayerData data;

    public EclipsePlayer(Player player) {
        this.player = player;
        this.data = new PlayerData(player.getUniqueId());
    }

    public void sendMiniMessage(String message) {
        player.sendMessage(MiniMessage.get().parse(message));
    }

    public void sendMessage(Component component) {
        player.sendMessage(component);
    }


    public Player getBukkitPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return data;
    }

}
