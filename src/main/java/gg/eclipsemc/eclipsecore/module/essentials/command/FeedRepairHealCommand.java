package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FeedRepairHealCommand {
    @CommandMethod("feed")
    @CommandDescription("NO MORE HUNGER FOR YOU!!")
    @CommandPermission("eclipsecore.essentials.saturate")
    public void onFeed(Player p){
        p.setSaturation(20f);
        p.sendMessage(Component.text("You have been saturated!").color(NamedTextColor.GREEN));
    }

    @CommandMethod("heal")
    @CommandDescription("Heals you back to full health!")
    @CommandPermission("eclipsecore.essentials.heal")
    public void onHeal(Player p){
        p.setHealth(p.getMaxHealth());
        p.sendMessage(Component.text("You have been healed!").color(NamedTextColor.GREEN));
    }
    @CommandMethod("repair <hand|all|armor>")
    @CommandDescription("repairs all of the specified options")
    @CommandPermission("eclipsecore.essentials.repair")
    public void onRepair(Player p, String type){
        type = type.toLowerCase();
        switch(type){
            case "all":
                for (ItemStack is : p.getInventory().getStorageContents()){
                    if (is == null || is.getType().isBlock() || is.getDurability() == 0) continue;
                    is.setDurability((short) 0);
                }
                break;
            case "hand":
                ItemStack is = p.getInventory().getItemInMainHand();
                if (is == null || is.getType().isBlock() || is.getDurability() == 0){
                    p.sendMessage(Component.text("You can not repair that!").color(NamedTextColor.RED));
                    return;
                }
                is.setDurability((short) 0);
                break;
            case "armor":
                for (ItemStack i : p.getInventory().getArmorContents()){
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
