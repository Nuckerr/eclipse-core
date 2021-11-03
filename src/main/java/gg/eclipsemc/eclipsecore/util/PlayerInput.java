package gg.eclipsemc.eclipsecore.util;

import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
public class PlayerInput {

    public PlayerInput(EclipsePlayer player, PlayerInputCompletion completion) {
        List<ItemStack> items = new ArrayList<>();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if(!p.getUniqueId().equals(player.getUniqueId())) {
                ItemStack item =
                        new ItemBuilder().setType(Material.PLAYER_HEAD)
                                .setName(Component.text(p.getName()).color(NamedTextColor.GRAY)).build();
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setLocalizedName(p.getUniqueId().toString());
                meta.setOwningPlayer(p);
                item.setItemMeta(meta);
                items.add(item);
            }
        }

        PaginatedMenu menu = new PaginatedMenu(5, Component.text("Pick player").color(NamedTextColor.BLACK), items, 18) {
            @Override
            public void onClick(final InventoryClickEvent e) {
                if (!Bukkit.getOfflinePlayer(UUID.fromString(e.getCurrentItem().getItemMeta().getLocalizedName())).isOnline()) {
                    player.sendMessage(Component.text("That player is no longer online").color(NamedTextColor.RED));
                    e.getWhoClicked().closeInventory();
                    return;
                }
                EclipsePlayer target = EclipsePlayer.getPlayerByUUID(UUID.fromString(e
                        .getCurrentItem()
                        .getItemMeta()
                        .getLocalizedName()));
                completion.onComplete(player, target, e);
                e.getWhoClicked().closeInventory();
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
                    if (Arrays.asList(17, 18, 26, 27).contains(slot)) return true;
                    return slot <= 9 || slot >= 35;
                });
            }
        };
        menu.open(player.getBukkitPlayer());
    }

    public interface PlayerInputCompletion {
        void onComplete(EclipsePlayer sender, EclipsePlayer pickedPlayer, InventoryClickEvent event);
    }
}
