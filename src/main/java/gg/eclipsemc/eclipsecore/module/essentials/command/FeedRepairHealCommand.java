package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FeedRepairHealCommand {
    @CommandMethod("feed")
    @CommandDescription("NO MORE HUNGER FOR YOU!!")
    @CommandPermission("eclipsecore.essentials.saturate")
    public void onFeed(EclipsePlayer p){
        p.getBukkitPlayer().setSaturation(20f);
        p.getBukkitPlayer().sendMessage(Component.text("You have been saturated!").color(NamedTextColor.GREEN));
    }

    @CommandMethod("heal")
    @CommandDescription("Heals you back to full health!")
    @CommandPermission("eclipsecore.essentials.heal")
    public void onHeal(EclipsePlayer p){
        p.getBukkitPlayer().setHealth(p.getBukkitPlayer().getMaxHealth());
        p.getBukkitPlayer().sendMessage(Component.text("You have been healed!").color(NamedTextColor.GREEN));
    }
    @CommandMethod("repair <hand|all|armor>")
    @CommandDescription("repairs all of the specified options")
    @CommandPermission("eclipsecore.essentials.repair")
    public void onRepair(EclipsePlayer p, String type){
        type = type.toLowerCase();
        switch(type){
            case "all":
                for (ItemStack is : p.getBukkitPlayer().getInventory().getStorageContents()){
                    if (is == null || is.getType().isBlock() || is.getDurability() == 0) continue;
                    is.setDurability((short) 0);
                }
                break;
            case "hand":
                ItemStack is = p.getBukkitPlayer().getInventory().getItemInMainHand();
                if (is == null || is.getType().isBlock() || is.getDurability() == 0){
                    p.sendMessage(Component.text("You can not repair that!").color(NamedTextColor.RED));
                    return;
                }
                is.setDurability((short) 0);
                break;
            case "armor":
                for (ItemStack i : p.getBukkitPlayer().getInventory().getArmorContents()){
                    if (i == null || i.getType().isBlock() || i.getDurability() == 0) continue;
                    i.setDurability((short) 0);
                }
                break;
            default:
                p.sendMessage(Component.text("Invalid Option!").color(NamedTextColor.RED));
        }
        p.sendMessage(Component.text(type + " has been repaired!").color(NamedTextColor.GREEN));
    }
}
