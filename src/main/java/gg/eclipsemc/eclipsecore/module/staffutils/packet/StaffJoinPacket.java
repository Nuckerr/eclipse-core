package gg.eclipsemc.eclipsecore.module.staffutils.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leonhard.storage.Yaml;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Nucker
 */
public class StaffJoinPacket extends RedisPacket {

    private OfflineEclipsePlayer player;
    private StaffUtilsModule module;

    public StaffJoinPacket() {
        super();
    }

    public StaffJoinPacket(OfflineEclipsePlayer player, StaffUtilsModule module) {
        this.player = player;
        this.module = module;
    }

    @Override
    public void handlePacket(final JsonElement message) {
        String playerName = message.getAsJsonObject().get("player").getAsString();
        String serverName = message.getAsJsonObject().get("server").getAsString();

        Yaml config = new Yaml(new File(JavaPlugin.getPlugin(EclipseCore.class).getDataFolder(), "staffutils.yml"));
        Component joinMessage = MiniMessage.get().parse(config.getString("messages.server-join").replace("%player%",
                playerName).replace("%server%", serverName));

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("eclipsecore.staffutils.staffmessages")) {
                player.sendMessage(joinMessage);
            }
        }
        Bukkit.getConsoleSender().sendMessage(joinMessage);
    }

    @Override
    public JsonElement getPacketData() {
        JsonObject object = new JsonObject();
        object.addProperty("player", player.getBukkitPlayer().getName());
        object.addProperty("server", module.getServerName());

        return object;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

}
