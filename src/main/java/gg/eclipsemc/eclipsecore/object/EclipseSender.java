package gg.eclipsemc.eclipsecore.object;

import net.kyori.adventure.text.Component;

public interface EclipseSender {

    public void sendMessage(Component component);

    public void sendMiniMessage(String string);

}
