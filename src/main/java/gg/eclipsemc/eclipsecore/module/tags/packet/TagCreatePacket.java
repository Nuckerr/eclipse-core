package gg.eclipsemc.eclipsecore.module.tags.packet;

import com.google.gson.JsonElement;
import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.RedisPacket;

/**
 * @author Nucker
 */
public class TagCreatePacket extends RedisPacket {

    public TagCreatePacket(Tag tag) {
        super("tagCreate");
    }

    @Override
    public void receivePacket(final JsonElement message) {

    }

    @Override
    public JsonElement sendPacket() {
        return null;
    }

}
