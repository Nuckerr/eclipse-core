package gg.eclipsemc.eclipsecore.object;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.nucker.simplemenus.adventure.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    public static EclipsePlayer getFromCraftPlayer(CraftPlayer player) {
        return EclipsePlayer.getPlayerByUUID(player.getUniqueId());
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

    public void playSound(Sound sound, float volume, float pitch) {
        this.player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSound(Sound sound, float volume) {
        this.playSound(sound, volume, 1f);
    }

    public void playSound(Sound sound) {
        this.playSound(sound, 1f, 1f);
    }

    public void playSoundPitch(Sound sound, float pitch) {
        this.playSound(sound, 1f, pitch);
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

    @NotNull
    public PlayerData getPlayerData() {
        return data;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof EclipsePlayer player) {
            return player.getUniqueId().equals(this.getUniqueId());
        }else if(o instanceof OfflinePlayer player) {
            return player.getUniqueId().equals(this.getUniqueId());
        }
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EclipsePlayer player1 = (EclipsePlayer) o;
        return Objects.equals(player, player1.player) && Objects.equals(data, player1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, data);
    }

}
