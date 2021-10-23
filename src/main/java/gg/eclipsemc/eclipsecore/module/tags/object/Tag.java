package gg.eclipsemc.eclipsecore.module.tags.object;

import net.kyori.adventure.text.Component;

/**
 * @author Nucker
 */
public interface Tag {

    String getName();

    Component getDisplay();

    void setDisplay(Component display);

    void setName(String name);

    default String getPermission() {
        return "eclipsecore.tags.tag." + this.getName();
    }
}
