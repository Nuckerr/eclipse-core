package gg.eclipsemc.eclipsecore.module.tags;

import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.module.tags.packet.TagDisplayUpdatePacket;
import gg.eclipsemc.eclipsecore.module.tags.packet.TagNameUpdatePacket;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nucker
 */
public class TagManager {

    private final TagModule module;

    public TagManager(TagModule module) {
        this.module = module;
    }

    public List<Tag> getTags() {
        List<Tag> res = new ArrayList<>();
        for (final Document document : module.getCollection().find()) {
            if(document.getString("name") != null) {
                res.add(this.getTag(document.getString("name")));
            }
        }

        return res;
    }

    public Tag createTag(String name) {
        Tag tag = new Tag() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDisplay() {
                return name;
            }

            @Override
            public void setDisplay(final String display) {
                module.saveDocument(new Document("name", this.getName()), "display", display);
                module.sendPacket(new TagDisplayUpdatePacket(this.getName(), display, TagManager.this));
            }

            @Override
            public void setName(final String name) {
                String oldName = this.getName();
                module.saveDocument(new Document("name", this.getName()), "name", name);
                module.sendPacket(new TagNameUpdatePacket(oldName, name, TagManager.this));
            }
        };

        module.saveDocument(new Document("name", name), new Document("name", name).append("display", tag.getDisplay()));

    }

    public Tag getTag(String name) {
        Document doc = module.getDocument(new Document("name", name));
        if(doc == null) throw new NullPointerException("Unable to find tag with name " + name);

        return new Tag() {
            @Override
            public String getName() {
                return doc.getString("name");
            }

            @Override
            public String getDisplay() {
                return doc.getString("display");
            }

            @Override
            public void setDisplay(String display) {
                module.saveDocument(new Document("name", this.getName()), "display", display);
                module.sendPacket(new TagDisplayUpdatePacket(this.getName(), display, TagManager.this));
            }

            @Override
            public void setName(String name) {
                String oldName = this.getName();
                module.saveDocument(new Document("name", this.getName()), "name", name);
                module.sendPacket(new TagNameUpdatePacket(oldName, name, TagManager.this));
            }
        };
    }
}
