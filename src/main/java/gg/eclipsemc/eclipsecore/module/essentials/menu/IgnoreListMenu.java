package gg.eclipsemc.eclipsecore.module.essentials.menu;

import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import gg.eclipsemc.eclipsecore.util.PlayerInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import wtf.nucker.simplemenus.adventure.Button;
import wtf.nucker.simplemenus.adventure.PaginatedMenu;
import wtf.nucker.simplemenus.adventure.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Nucker
 */
public class IgnoreListMenu extends PaginatedMenu {

    public IgnoreListMenu(EclipsePlayer player) {
        super(5, Component.text("Ignore list").color(NamedTextColor.BLACK), new ArrayList<>(), 18);
        List<ItemStack> ignoreList = new ArrayList<>(player.getPlayerData().getListOf("ignoreList", Object.class).size());

        List<UUID> uuids = player.getPlayerData().getListOf("ignoreList", UUID.class);
        for (final UUID uuid : uuids) {
            OfflineEclipsePlayer target = OfflineEclipsePlayer.getPlayerByUUID(uuid);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(target.getBukkitPlayer());
            meta.displayName(Component.text(target.getBukkitPlayer().getName()).color(target.isOnline() ? NamedTextColor.GREEN
                    : NamedTextColor.RED));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("You have blocked " + target.getBukkitPlayer().getName()).color(NamedTextColor.RED));
            lore.add(Component.empty());
            lore.add(Component.text("CLICK ").color(NamedTextColor.RED)
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text("Unblock player").color(NamedTextColor.GRAY)));
            lore.add(Component.empty());
            meta.lore(lore);

            meta.setLocalizedName(uuid.toString());

            skull.setItemMeta(meta);
            ignoreList.add(skull);
        }

        if(ignoreList.size() <= 0) {
            ignoreList.add(new ItemBuilder().setType(Material.RED_STAINED_GLASS_PANE).setName(Component.text("You are not " +
                    "ignoring anyone").color(NamedTextColor.RED))
                    .setLore(ChatColor.translateAlternateColorCodes('&', "&cYou are not ignoring anyone")).build());
        }

        this.setItems(ignoreList);
    }

    @Override
    public void onClick(final InventoryClickEvent e) {
        OfflineEclipsePlayer target = OfflineEclipsePlayer.getPlayerByUUID(UUID.fromString(e.getCurrentItem().getItemMeta().getLocalizedName()));
        EclipsePlayer player = EclipsePlayer.getPlayerByUUID(e.getWhoClicked().getUniqueId());
        List<UUID> ignoreList = player.getPlayerData().getListOf("ignoreList", UUID.class);
        if(!ignoreList.contains(target.getBukkitPlayer().getUniqueId())) {
            player.getBukkitPlayer().closeInventory();
            player.sendMessage(Component.text(target.getBukkitPlayer().getName() + " was already taken off your ignore list").color(NamedTextColor.RED));
            return;
        }
        ignoreList.remove(target.getBukkitPlayer().getUniqueId());
        player.getPlayerData().set("ignoreList", ignoreList);
        player.sendMessage(Component.text("You have removed " + target.getBukkitPlayer().getName() + " from your ignore list").color(NamedTextColor.GREEN));
        player.getBukkitPlayer().closeInventory();
    }

    @Override
    public void addFiller(
            final Button previousPageButton,
            final Button nextPageButton,
            final Button closeInvButton,
            final ItemStack filler
    ) {
        this.setButton(previousPageButton, 36);
        this.setButton(closeInvButton, 40);
        this.setButton(nextPageButton, 44);

        this.fillMenu(filler, slot -> {
            if(Arrays.asList(17, 18, 26, 27).contains(slot)) return true;
            return slot <= 9 || slot >=35;
        });

        ItemStack item =
                new ItemBuilder().setType(Material.LIME_STAINED_GLASS_PANE).setName(Component.text("Block player").color(NamedTextColor.GREEN)).build();
        item.editMeta(meta -> meta.lore(Arrays.asList(Component.empty(),
                Component.text("Click to block another player").color(NamedTextColor.GRAY), Component.empty())));
        this.setButton(new Button(item) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                new PlayerInput(EclipsePlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId()),
                        (sender, target, event1) -> {
                    sender.getPlayerData().addToList("ignoreList", target.getUniqueId());
                    event1.getWhoClicked().closeInventory();
                    new IgnoreListMenu(sender).open(sender.getBukkitPlayer());
                    sender.sendMessage(Component.text("Added " + target.getBukkitPlayer().getName() + " to your block list").color(NamedTextColor.GREEN));
                });
            }
        }, 4);
    }

}
