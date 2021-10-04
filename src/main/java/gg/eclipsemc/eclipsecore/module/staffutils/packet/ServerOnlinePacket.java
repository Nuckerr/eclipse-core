package gg.eclipsemc.eclipsecore.module.staffutils.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.utils.Utilities;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import net.kyori.adventure.text.Component;

/**
 * @author Nucker
 */
public class ServerOnlinePacket extends RedisPacket {

    public ServerOnlinePacket() {super();}

    private StaffUtilsModule module;

    public ServerOnlinePacket(StaffUtilsModule module) {
        this.module = module;
    }

    @Override
    public void handlePacket(final JsonElement json) {
        String serverName = json.getAsJsonObject().get("serverName").getAsString();
        Component message = Utilities.getMessage("server-online");
        message = message.replaceText(b -> b.matchLiteral("%server%").replacement(serverName));

        Utilities.broadcastStaffMessage(message);
    }

    @Override
    public JsonElement getPacketData() {
        JsonObject object = new JsonObject();
        object.addProperty("serverName", module.getServerName());
        return object;
    }

    @Override
    public String getIdentifier() {
        return "ServerOnlinePacket";
    }
}
