package gg.eclipsemc.eclipsecore.module.tags;

import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
            public Component getDisplay() {
                return Component.text(name);
            }

            @Override
            public void setDisplay(final Component display) {
                module.saveDocument(new Document("name", this.getName()), "display",
                        GsonComponentSerializer.gson().serialize(display));
            }

            @Override
            public void setName(final String name) {
                String oldName = this.getName();
                module.saveDocument(new Document("name", this.getName()), "name", name);
            }
        };

        module.saveDocument(new Document("name", name), new Document("name", name).append("display", tag.getDisplay()));

        return tag;
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
            public Component getDisplay() {
                return GsonComponentSerializer.gson().deserialize(doc.getString("display"));
            }

            @Override
            public void setDisplay(Component display) {
                module.saveDocument(new Document("name", this.getName()), "display",
                        GsonComponentSerializer.gson().serialize(display));
            }

            @Override
            public void setName(String name) {
                String oldName = this.getName();
                module.saveDocument(new Document("name", this.getName()), "name", name);
            }
        };
    }

    public void deleteTag(Tag tag) {
        module.deleteDocument(new Document("name", tag.getName()));
    }

    public void deleteTag(String name) {
        this.deleteTag(this.getTag(name));
    }
}
