package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;

public class CraftAnvilCommand {
    @CommandMethod("anvil")
    @CommandDescription("opens an anvil menu!")
    @CommandPermission("eclipsecore.essentials.anvil")
    public void onAnvil(EclipsePlayer p){
        p.getBukkitPlayer().openAnvil(p.getBukkitPlayer().getLocation(), true); //force: true, since else, if there is no anvil on the given
        // location, it wont open.
    }

    @CommandMethod("craftingtable")
    @CommandDescription("opens an crafting table!")
    @CommandPermission("eclipsecore.essentials.craftingtable")
    public void onCraftingtable(EclipsePlayer p){
        p.getBukkitPlayer().openWorkbench(p.getBukkitPlayer().getLocation(), true); //force: true, since else, if there is no crafting table on the given location,
    }
}
