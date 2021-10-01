package gg.eclipsemc.eclipsecore.module.essentials;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.PAPIExpansion;
import gg.eclipsemc.eclipsecore.module.essentials.command.FlyCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.PluginsCommand;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;

/**
 * @author Nucker
 */
public class EssentialsModule extends EclipseModule {


    public EssentialsModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "EssentialsModule";
    }

    @Override
    public String getConfigName() {
        return "essentials.yml";
    }

    @Override
    protected void onEnable() {
        registerCommand(new FlyCommand());
        registerCommand(new PluginsCommand());
        eclipseCore.getPlaceholderAPIExpansion().registerPlaceholders(new PAPIExpansion.Placeholder() {
            @Override
            public String requestPlaceholder(final EclipsePlayer player, final String placeholder) {
                if(placeholder.equalsIgnoreCase("IsFlying"))
                    return String.valueOf(player.getBukkitPlayer().isFlying());
                return null;
            }
        });
        super.onEnable();
    }

}
