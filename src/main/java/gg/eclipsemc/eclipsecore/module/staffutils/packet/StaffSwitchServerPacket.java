package gg.eclipsemc.eclipsecore.module.staffutils.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.utils.Utilities;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import net.kyori.adventure.text.Component;

/**
 * @author Nucker
 */
public class StaffSwitchServerPacket extends RedisPacket {

    public StaffSwitchServerPacket() {super();}

    private OfflineEclipsePlayer player;
    private StaffUtilsModule module;
    private String previousServer;

    public StaffSwitchServerPacket(OfflineEclipsePlayer player, StaffUtilsModule module, String previousServerName) {
        this.player = player;
        this.module = module;
        this.previousServer = previousServerName;
    }

    @Override
    public void handlePacket(final JsonElement message) {
        String player = message.getAsJsonObject().get("player").getAsString();
        String newServer = message.getAsJsonObject().get("newServer").getAsString();
        String oldServer = message.getAsJsonObject().get("oldServer").getAsString();

        Component msg = Utilities.getMessage("staff-switch");
        msg = msg.replaceText(b -> b.matchLiteral("%server%").replacement(newServer));
        msg = msg.replaceText(b -> b.matchLiteral("%previousServer%").replacement(oldServer));
        msg = msg.replaceText(b -> b.matchLiteral("%player%").replacement(player));

        Utilities.broadcastStaffMessage(msg);
    }

    @Override
    public JsonElement getPacketData() {
        JsonObject object = new JsonObject();

        object.addProperty("player", player.getBukkitPlayer().getName());
        object.addProperty("newServer", module.getServerName());
        object.addProperty("oldServer", previousServer);

        return object;
    }

    @Override
    public String getIdentifier() {
        return "StaffSwitch";
    }
}
