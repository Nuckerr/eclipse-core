package gg.eclipsemc.eclipsecore.module.staffutils.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leonhard.storage.Yaml;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.utils.Utilities;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import jdk.jshell.execution.Util;
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

        Component msg = Utilities.getMessage("staff-join");
        msg = msg.replaceText(b -> b.matchLiteral("%player%").replacement(playerName));
        msg = msg.replaceText(b -> b.matchLiteral("%server%").replacement(serverName));

        Utilities.broadcastStaffMessage(msg);

        if(Bukkit.getPlayer(playerName) != null) {
            Bukkit.getPlayer(playerName).sendMessage(msg);
        }
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
        return "StaffJoin";
    }

}
