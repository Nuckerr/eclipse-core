package gg.eclipsemc.eclipsecore.module.essentials;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;

import java.util.List;

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


        super.onEnable();
    }

}
