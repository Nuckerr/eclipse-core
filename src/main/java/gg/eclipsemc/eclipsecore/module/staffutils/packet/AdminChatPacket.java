package gg.eclipsemc.eclipsecore.module.staffutils.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.utils.Utilities;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

/**
 * @author Nucker
 */
public class AdminChatPacket extends RedisPacket {

    public AdminChatPacket() {super();}

    private OfflineEclipsePlayer player;
    private Component message;
    private StaffUtilsModule module;

    public AdminChatPacket(OfflineEclipsePlayer player, Component component, StaffUtilsModule module) {
        this.player = player;
        this.message = component;
        this.module = module;
    }

    @Override
    public void handlePacket(final JsonElement message) {
        String serverName = message.getAsJsonObject().get("server").getAsString();
        String playerName = message.getAsJsonObject().get("player").getAsString();
        Component playerMessage = GsonComponentSerializer.gson().deserializeFromTree(message.getAsJsonObject().get("message"));

        Component msg = Utilities.getMessage("admin-chat");
        msg = msg.replaceText(b -> b.matchLiteral("%player%").replacement(playerName));
        msg = msg.replaceText(b -> b.matchLiteral("%server%").replacement(serverName));
        msg = msg.replaceText(b -> b.matchLiteral("%message%").replacement(playerMessage));

        Utilities.broadcastStaffMessage(msg, "eclipsecore.staffutils.adminchat");
    }

    @Override
    public JsonElement getPacketData() {
        JsonObject object = new JsonObject();

        object.addProperty("player", player.getBukkitPlayer().getName());
        object.addProperty("server", module.getServerName());
        object.add("message", GsonComponentSerializer.gson().serializeToTree(message));

        return object;
    }

    @Override
    public String getIdentifier() {
        return "AdminChat";
    }

}
