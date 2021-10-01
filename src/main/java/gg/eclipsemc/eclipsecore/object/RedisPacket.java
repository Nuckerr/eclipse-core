package gg.eclipsemc.eclipsecore.object;

import com.google.gson.JsonElement;

/**
 * @author Nucker
 */
public abstract class RedisPacket {

    private final String channel;

    public RedisPacket(String channel) {
        this.channel = channel;
    }

    public abstract void receivePacket(JsonElement message);

    public abstract JsonElement sendPacket();

    public String getChannel() {
        return channel;
    }

}
