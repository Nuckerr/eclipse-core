package gg.eclipsemc.eclipsecore.module.chat;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.module.chat.listener.ChatListener;

public class ChatModule extends EclipseModule {

    public ChatModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "ChatModule";
    }

    @Override
    public void enable() {
        registerListener(new ChatListener());
        reload();
        super.enable();
    }

    @Override
    public void reload() {
        ChatRenderer.nameFormat = eclipseCore.getConfig().getString("chat.nameformat");
        ChatRenderer.nameHover = eclipseCore.getConfig().getString("chat.namehover");
        super.reload();
    }

}
