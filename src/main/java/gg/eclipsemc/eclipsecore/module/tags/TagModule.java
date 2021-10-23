package gg.eclipsemc.eclipsecore.module.tags;

import cloud.commandframework.arguments.standard.StringArgument;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.PAPIExpansion;
import gg.eclipsemc.eclipsecore.module.tags.menu.TagMenu;
import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * @author Nucker
 */
public class TagModule extends EclipseModule {

    private TagManager manager;

    public TagModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "Tags";
    }

    @Override
    protected void onEnable() {
        this.manager = new TagManager(this);
        this.registerCommands();

        eclipseCore.getPlaceholderAPIExpansion().registerPlaceholders(new PAPIExpansion.Placeholder() {
            @Override
            public String requestPlaceholder(final EclipsePlayer player, final String placeholder) {
                for (final Tag tag : manager.getTags()) {
                    if(placeholder.equalsIgnoreCase("tag_" + tag.getName()))
                        return PlainTextComponentSerializer.plainText().serialize(tag.getDisplay());
                }
                return null;
            }
        });
        eclipseCore.getPlaceholderAPIExpansion().registerPlaceholders(new PAPIExpansion.Placeholder() {
            @Override
            public String requestPlaceholder(final EclipsePlayer player, final String placeholder) {
                if(placeholder.equalsIgnoreCase("%player_tag%")) {
                    Tag tag = manager.getPlayerTag(player);
                    if(tag == null){
                        return "";
                    }else {
                        return PlainTextComponentSerializer.plainText().serialize(tag.getDisplay());
                    }
                }
                return null;
            }
        });
        super.onEnable();
    }

    private void registerCommands() {
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .handler(c -> {
                    EclipsePlayer player = EclipsePlayer.getFromSender(c.getSender());
                    player.openMenu(new TagMenu(this.getManager()));
                }));
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .argument(StringArgument.of("tag"))
                .handler(c -> {
                    EclipsePlayer player = EclipsePlayer.getFromSender(c.getSender());
                    Tag tag = this.getManager().getTag(c.get("tag"));
                    if(tag == null) {
                        player.sendMessage(Component.text("Invalid tag name").color(NamedTextColor.RED));
                    }else {
                        if(player.getBukkitPlayer().hasPermission(tag.getPermission())) {
                            this.getManager().setPlayerTag(player, tag);
                        }else {
                            player.sendMessage(Component.text("You dont have permission to equip this tag").color(NamedTextColor.RED));
                        }
                    }
                }));
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .literal("create").argument(StringArgument.of("name"))
                .permission("eclipsecore.tags.create")
                .handler(c -> {
                    this.manager.createTag(c.get("name"));
                    c.getSender().sendMessage(MiniMessage.get().parse("<green>Created tag " + c.get("name")));
                }));
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .literal("set").literal("name").argument(StringArgument.of("name"))
                        .argument(StringArgument.of("newname"))
                .permission("eclipsecore.tags.edit")
                .handler(c -> {
                    this.manager.getTag(c.get("name")).setName(c.get("newname"));
                    c.getSender().sendMessage(MiniMessage.get().parse("<green>Set " + c.get("name") + "'s name to " + c.get(
                            "newname")));
                }));
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .literal("set").literal("display", "displayname").argument(StringArgument.of("name"))
                .argument(StringArgument.of("newdisplayname"))
                .permission("eclipsecore.tags.edit")
                .handler(c -> {
                    this.manager.getTag(c.get("name")).setDisplay(MiniMessage.get().parse(c.get("newdisplayname")));
                    c.getSender().sendMessage(MiniMessage.get().parse("<green>Set " + c.get("name") + "'s display name to " + c.get(
                            "displayname")));
                }));
        this.registerCommand(this.getCommandBuilder("tag", "tags")
                .literal("delete").argument(StringArgument.of("name"))
                .permission("eclipsecore.tags.delete")
                .handler(c -> {
                    this.manager.deleteTag((String) c.get("name"));
                    c.getSender().sendMessage(MiniMessage.get().parse("<green>Created tag " + c.get("name")));
                }));
    }


    public TagManager getManager() {
        return manager;
    }

}
