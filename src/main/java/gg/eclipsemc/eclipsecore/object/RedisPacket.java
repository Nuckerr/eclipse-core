package gg.eclipsemc.eclipsecore.object;

import com.google.gson.JsonElement;

/**
 * @author Nucker
 * **YOU MUST ADD AN EMPTY CONSTRUCOTOR FOR REGISTERING**
 */
public abstract class RedisPacket {

    public abstract void handlePacket(JsonElement message);

    public abstract JsonElement getPacketData();

    public abstract String getIdentifier();

}
