package gg.eclipsemc.eclipsecore.module.essentials.menu;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import wtf.nucker.simplemenus.adventure.Button;
import wtf.nucker.simplemenus.adventure.Menu;
import wtf.nucker.simplemenus.adventure.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nucker
 */
public class SettingsMenu extends Menu {

    private final EclipsePlayer player;
    private final EclipseCore core;

    public SettingsMenu(EclipsePlayer player, EclipseCore core) {
        super(5, Component.text("Settings").color(NamedTextColor.BLACK));
        this.player = player;
        this.core = core;

         // 10, 13, 16, 28, 31, 34
        this.addPrivateMessages();
        this.addPrivateMessagesPing();
        this.addAutoBroadcasts();
        this.addChatToggle();
        this.addMentionPings();
    }

    private void addPrivateMessages() {
        boolean privateMessages = player.getPlayerData().getBool("privateMessages");

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle private messages").color(NamedTextColor.GRAY));
        lore.add(Component.text("off to reduce chat spam").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(privateMessages ? Component.text("Enabled").color(NamedTextColor.GRAY)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(privateMessages ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("privateMessages", !privateMessages);
                player.sendMessage(Component.text("Private messages have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!privateMessages ? "off" : "on").color(NamedTextColor.GREEN)));
            }
        }, 10);
    }

    private void addPrivateMessagesPing() {
        boolean privateMessagesPing = player.getPlayerData().getBool("privateMessagesPing");

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle private message's ping").color(NamedTextColor.GRAY));
        lore.add(Component.text("off").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(privateMessagesPing ? Component.text("Enabled").color(NamedTextColor.GRAY)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(privateMessagesPing ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("privateMessagesPing", !privateMessagesPing);
                player.sendMessage(Component.text("Private message pings have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!privateMessagesPing ? "off" : "on").color(NamedTextColor.GREEN)));
            }
        }, 13);
    }

    private void addAutoBroadcasts() {
        boolean autoBroadcasts = player.getPlayerData().getBool("autoBroadcasts");
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle auto broadcasts").color(NamedTextColor.GRAY));
        lore.add(Component.text("off to reduce chat spam").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(autoBroadcasts ? Component.text("Enabled").color(NamedTextColor.GRAY)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(autoBroadcasts ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("autoBroadcasts", !autoBroadcasts);
                player.sendMessage(Component.text("Auto broadcasts have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!autoBroadcasts ? "off" : "on").color(NamedTextColor.GREEN)));
            }
        }, 28);
    }

    private void addChatToggle() {
        ChatModule module = (ChatModule) core.getModule(ChatModule.class);
        if(module == null)
            throw new NullPointerException("Chat module is null");
        if(!module.isEnabled()) return;

        boolean chatToggle = player.getPlayerData().getBool("chatToggled");

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle chat messages").color(NamedTextColor.GRAY));
        lore.add(Component.text("off to reduce chat spam").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(chatToggle ? Component.text("Enabled").color(NamedTextColor.GRAY)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Toggle chat").color(chatToggle ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("chatToggled", !chatToggle);
                player.sendMessage(Component.text("Chat toggled have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!chatToggle ? "off" : "on").color(NamedTextColor.GREEN)));
            }
        }, 31);
    }

    private void addMentionPings() {
        ChatModule module = (ChatModule) core.getModule(ChatModule.class);
        if(module == null)
            throw new NullPointerException("Chat module is null");
        if(!module.isEnabled()) return;

        boolean mentionPings = player.getPlayerData().getBool("mentionPings");
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle mention pings").color(NamedTextColor.GRAY));
        lore.add(Component.text("off").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(mentionPings ? Component.text("Enabled").color(NamedTextColor.GRAY)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(mentionPings ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("mentionPings", !mentionPings);
                player.sendMessage(Component.text("Chat mentions have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!mentionPings ? "off" : "on").color(NamedTextColor.GREEN)));
            }
        }, 34);
    }
}
