package gg.eclipsemc.eclipsecore.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.EclipseCore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nucker
 */
public class BungeeMessagingManager {

    private final EclipseCore core;

    public BungeeMessagingManager(final EclipseCore core) {
        this.core = core;

        core.getServer().getMessenger().registerIncomingPluginChannel(core, "eclipsecore", (channel, player, bytes) -> {
            if(!channel.equals("eclipsecore")) return;

            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subChannel = in.readUTF();

            // Respond too data
        });

        core.getServer().getMessenger().registerOutgoingPluginChannel(core, "eclipsecore");
    }

    public EclipseCore getCore() {
        return core;
    }

    public void sendMessage(String subChannel, JsonElement object, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(new Gson().toJson(object));

        player.sendPluginMessage(this.core, "eclipsecore", out.toByteArray());
    }
}
