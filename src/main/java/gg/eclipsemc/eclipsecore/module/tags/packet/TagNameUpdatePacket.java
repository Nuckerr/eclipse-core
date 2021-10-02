package gg.eclipsemc.eclipsecore.module.tags.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.module.tags.TagManager;
import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.RedisPacket;

/**
 * @author Nucker
 */
public class TagNameUpdatePacket extends RedisPacket {

    private final String oldName;
    private final String newName;
    private final TagManager manager;

    public TagNameUpdatePacket(String oldName, String newName, TagManager manager) {
        super("tagUpdateName");
        this.oldName = oldName;
        this.newName = newName;
        this.manager = manager;
    }

    @Override
    public void receivePacket(final JsonElement message) {
        Tag tag = manager.getTag(message.getAsJsonObject().get("oldName").getAsString());
        tag.setName(message.getAsJsonObject().get("newName").getAsString());
    }

    @Override
    public JsonElement sendPacket() {
        JsonObject object = new JsonObject();
        object.addProperty("oldName", this.oldName);
        object.addProperty("newName", this.newName);

        return object;
    }

}
