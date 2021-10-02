package gg.eclipsemc.eclipsecore.module.tags.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.tags.TagManager;
import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Nucker
 */
public class TagDisplayUpdatePacket extends RedisPacket {

    private final String display;
    private final String tagName;

    public TagDisplayUpdatePacket(String tagName, String display) {
        super("tagUpdateDisplay");
        this.display = display;
        this.tagName = tagName;
    }

    @Override
    public void receivePacket(final JsonElement message) {
        // FIXME: 02/10/2021 We need to get instance of manager on the other server
        Tag tag = manager.getTag(message.getAsJsonObject().get("name").getAsString());
        tag.setDisplay(message.getAsJsonObject().get("display").getAsString());
    }

    @Override
    public JsonElement sendPacket() {
        JsonObject object = new JsonObject();
        object.addProperty("name", this.tagName);
        object.addProperty("display", this.display);

        return object;
    }

}
