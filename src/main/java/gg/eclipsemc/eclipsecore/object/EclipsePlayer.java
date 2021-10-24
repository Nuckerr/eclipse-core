package gg.eclipsemc.eclipsecore.object;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.nucker.simplemenus.adventure.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class EclipsePlayer extends OfflineEclipsePlayer implements EclipseSender {

    private static final Map<UUID, EclipsePlayer> cache = new HashMap<>();
    public static Map<UUID, EclipsePlayer> getPlayerCache() {
        return cache;
    }

    public static EclipsePlayer getPlayerFromBukkit(Player player) {
        if(cacheContainsUUID(player.getUniqueId()))
            return getPlayerCache().get(player.getUniqueId());
        return new EclipsePlayer(player);
    }

    public static EclipsePlayer getPlayerByUUID(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load player class for `" + uuid + "` because they are not online");
            return null;
        }
        return getPlayerFromBukkit(player);
    }

    public static EclipsePlayer getFromSender(EclipseSender sender) {
        if(sender instanceof EclipsePlayer) {
            return (EclipsePlayer) sender;
        }
        return null;
    }

    private static boolean cacheContainsUUID(UUID uuid) {
        for (final UUID id : getPlayerCache().keySet()) {
            if(uuid == id) return true;
        }

        return false;
    }


    private final Player player;
    private final PlayerData data;

    public EclipsePlayer(Player player) {
        super(player);
        this.player = player;
        this.data = new PlayerData(player.getUniqueId());
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    public void sendTranslatedMessage(String message) {
        message = PlaceholderAPI.setPlaceholders(player, message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        Component comp = MiniMessage.get().parse(message);
        player.sendMessage(comp);
    }

    public void openMenu(Menu menu) {
        menu.switchMenu(this.getBukkitPlayer());
    }

    public void sendMessage(Component component) {
        player.sendMessage(component);
    }

    @Override
    public void sendMiniMessage(final String string) {
        player.sendMessage(MiniMessage.get().parse(string));
    }


    @NotNull
    public Player getBukkitPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

}
