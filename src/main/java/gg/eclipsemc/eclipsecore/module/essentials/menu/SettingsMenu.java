package gg.eclipsemc.eclipsecore.module.essentials.menu;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
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
        //TODO: One more setting field to fill the menu

        this.fillMenu();
        this.addListener(event -> {
            EclipsePlayer p = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
            p.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 100f, 50f);
        });
    }

    private void addPrivateMessages() {
        boolean privateMessages = player.getPlayerData().getBool("privateMessages");

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Toggle private messages").color(NamedTextColor.GRAY));
        lore.add(Component.text("off to reduce chat spam").color(NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                .append(privateMessages ? Component.text("Enabled").color(NamedTextColor.GREEN)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(privateMessages ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EMERALD).build();
        item.editMeta(meta -> meta.lore(lore));
        item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        if(privateMessages)
            item.editMeta(meta -> meta.addEnchant(Enchantment.DURABILITY, 1, true));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                boolean privateMessages = player.getPlayerData().getBool("privateMessages"); // Redo variable to refresh value
                // from database
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("privateMessages", !privateMessages);
                player.sendMessage(Component.text("Private messages have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!privateMessages ? "on" : "off").color(NamedTextColor.GREEN)));
                player.getBukkitPlayer().closeInventory();
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
                .append(privateMessagesPing ? Component.text("Enabled").color(NamedTextColor.GREEN)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(privateMessagesPing ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.NOTE_BLOCK).build();
        item.editMeta(meta -> meta.lore(lore));
        item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        if(privateMessagesPing)
            item.editMeta(meta -> meta.addEnchant(Enchantment.DURABILITY, 1, true));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                boolean privateMessagesPing = player.getPlayerData().getBool("privateMessagesPing");
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("privateMessagesPing", !privateMessagesPing);
                player.sendMessage(Component.text("Private message pings have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!privateMessagesPing ? "on" : "off").color(NamedTextColor.GREEN)));
                player.getBukkitPlayer().closeInventory();
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
                .append(autoBroadcasts ? Component.text("Enabled").color(NamedTextColor.GREEN)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Private messages").color(autoBroadcasts ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.OAK_SIGN).build();
        item.editMeta(meta -> meta.lore(lore));
        item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        if(autoBroadcasts)
            item.editMeta(meta -> meta.addEnchant(Enchantment.DURABILITY, 1, true));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                boolean autoBroadcasts = player.getPlayerData().getBool("autoBroadcasts");
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("autoBroadcasts", !autoBroadcasts);
                player.sendMessage(Component.text("Auto broadcasts have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!autoBroadcasts ? "on" : "off").color(NamedTextColor.GREEN)));
                player.getBukkitPlayer().closeInventory();
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
                .append(chatToggle ? Component.text("Enabled").color(NamedTextColor.GREEN)
                        : Component.text("Disabled").color(NamedTextColor.RED)));

        ItemStack item = new ItemBuilder().setName(Component.text("Toggle chat").color(chatToggle ?
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.PAPER).build();
        item.editMeta(meta -> meta.lore(lore));
        item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        if(chatToggle)
            item.editMeta(meta -> meta.addEnchant(Enchantment.DURABILITY, 1, true));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                boolean chatToggle = player.getPlayerData().getBool("chatToggled");
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("chatToggled", !chatToggle);
                player.sendMessage(Component.text("Chat toggled have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!chatToggle ? "on" : "off").color(NamedTextColor.GREEN)));
                player.getBukkitPlayer().closeInventory();
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
                NamedTextColor.GREEN : NamedTextColor.RED)).setType(Material.EXPERIENCE_BOTTLE).build();
        item.editMeta(meta -> meta.lore(lore));
        item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        if(mentionPings)
            item.editMeta(meta -> meta.addEnchant(Enchantment.DURABILITY, 1, true));

        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                boolean mentionPings = player.getPlayerData().getBool("mentionPings");
                EclipsePlayer player = EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                player.getPlayerData().set("mentionPings", !mentionPings);
                player.sendMessage(Component.text("Chat mentions have been toggled ").color(NamedTextColor.GREEN)
                        .append(Component.text(!mentionPings ? "on" : "off").color(NamedTextColor.GREEN)));
                player.getBukkitPlayer().closeInventory();
            }
        }, 34);
    }
}
