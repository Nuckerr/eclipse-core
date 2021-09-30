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
public class GamePlayer {

    private static final Map<UUID, GamePlayer> cache = new HashMap<>();

    public static GamePlayer getPlayerFromBukkit(Player player) {
        return new GamePlayer(player);
    }

    public static GamePlayer getPlayerByUUID(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load player class for `" + uuid + "` because they are not online");
            return null;
        }
        return new GamePlayer(Bukkit.getPlayer(uuid));
    }


    private final Player player;
    private final PlayerData data;

    public GamePlayer(Player player) {
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

    public static Map<UUID, GamePlayer> getCache() {
        return cache;
    }

}
