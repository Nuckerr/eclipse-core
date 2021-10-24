package gg.eclipsemc.eclipsecore.module.tags;

import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

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

    public Tag setPlayerTag(EclipsePlayer player, Tag tag) {
        player.getPlayerData().set("tag", tag.getName());
        return tag;
    }

    public Tag getPlayerTag(EclipsePlayer player) {
        return this.getTag(player.getPlayerData().getString("tag"));
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
                module.saveDocument(new Document("name", this.getName()), "name", name);
            }
        };

        module.saveDocument(new Document("name", name), new Document("name", name).append("display",
                GsonComponentSerializer.gson().serialize(tag.getDisplay())));

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

    // FIXME: 24/10/2021 There is an issue with item components being broken with NBT API. Issue reported - https://github.com/tr7zw/Item-NBT-API/issues/143
    public List<ItemStack> getTagsAsItemStacks(EclipsePlayer player) {
        List<ItemStack> items = new ArrayList<>();
        for (final Tag tag : this.getTags()) {
            ItemStack item = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(tag.getName()).color(NamedTextColor.GREEN));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("&Your tag will look like: "));
            lore.add(tag.getDisplay());
            lore.add(Component.empty());
            meta.lore(lore);
            meta.setLocalizedName(tag.getName());
            if(this.getPlayerTag(player).equals(tag)) {
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);

            items.add(item);
        }

        return items;
    }

    public void deleteTag(Tag tag) {
        module.deleteDocument(new Document("name", tag.getName()));
    }

    public void deleteTag(String name) {
        this.deleteTag(this.getTag(name));
    }
}
