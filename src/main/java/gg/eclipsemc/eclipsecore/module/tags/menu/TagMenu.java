package gg.eclipsemc.eclipsecore.module.tags.menu;

import gg.eclipsemc.eclipsecore.module.tags.TagManager;
import gg.eclipsemc.eclipsecore.module.tags.object.Tag;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import wtf.nucker.simplemenus.adventure.Button;
import wtf.nucker.simplemenus.adventure.PaginatedMenu;

/**
 * @author Nucker
 */
public class TagMenu extends PaginatedMenu {

    private final TagManager manager;

    public TagMenu(TagManager manager) {
        super(4, Component.text("Tag menu").color(NamedTextColor.BLACK), manager.getTagsAsItemStacks(), 14);
        this.manager = manager;
    }

    @Override
    public void addFiller(
            final Button previousPageButton,
            final Button nextPageButton,
            final Button closeInvButton,
            final ItemStack filler
    ) {
        this.setButton(previousPageButton, 27);
        this.setButton(nextPageButton, 35);
        this.setButton(closeInvButton, 31);
        this.fillMenu(Material.BLACK_STAINED_GLASS, i -> !(i >= 10 && i <= 25));
    }

    @Override
    public void onClick(final InventoryClickEvent e) {
        if(e.getCurrentItem().getItemMeta() == null) return;
        Tag tag = manager.getTag(e.getCurrentItem().getItemMeta().getLocalizedName());
        EclipsePlayer player = EclipsePlayer.getPlayerByUUID(e.getWhoClicked().getUniqueId());

        if(player.getBukkitPlayer().hasPermission(tag.getPermission())) {
            manager.setPlayerTag(player, tag);
            player.sendMessage(Component.text("Your tag has been set too " + tag.getName()).color(NamedTextColor.GREEN));
        }else {
            player.sendMessage(Component.text("You dont have permission to equip this tag").color(NamedTextColor.RED));
        }
    }

}
