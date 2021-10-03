package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class CraftAnvilCommand {
    @CommandMethod("anvil")
    @CommandDescription("opens an anvil menu!")
    @CommandPermission("eclipsecore.essentials.anvil")
    public void onAnvil(Player p){
        p.openAnvil(p.getLocation(), true); //force: true, since else, if there is no anvil on the given location, it wont open.
    }

    @CommandMethod("craftingtable")
    @CommandDescription("opens an crafting table!")
    @CommandPermission("eclipsecore.essentials.craftingtable")
    public void onCraftingtable(Player p){
        p.openWorkbench(p.getLocation(), true); //force: true, since else, if there is no crafting table on the given location,
    }
}
