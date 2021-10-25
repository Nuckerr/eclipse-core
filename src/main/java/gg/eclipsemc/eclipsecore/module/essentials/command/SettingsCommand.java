package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.module.essentials.menu.SettingsMenu;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;

/**
 * @author Nucker
 */
public class SettingsCommand {

    private final EclipseCore core;
    public SettingsCommand(EclipseCore core) {
        this.core = core;
    }

    @CommandMethod("settings|setting|options|option")
    @CommandDescription("Toggle your settings")
    public void onSettings(EclipsePlayer player) {
        player.openMenu(new SettingsMenu(player, core));
    }
}
