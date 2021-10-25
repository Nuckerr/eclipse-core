package gg.eclipsemc.eclipsecore.module.chat;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.chat.listener.ChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatModule extends EclipseModule {

    public ChatModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "ChatModule";
    }

    @Override
    public String getConfigName() {
        return "chat.yml";
    }

    @Override
    public void onEnable() {
        registerListener(new ChatListener());
        onReload();
        super.onEnable();
    }

    private void registerDefaults() {
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("chatToggled") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return false;
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<List<UUID>>("ignoreList") {
            @Override
            public List<UUID> parseData(final UUID uuid) {
                return new ArrayList<>();
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("mentionPings") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return true;
            }
        });
    }

    @Override
    public void onReload() {
        ChatRenderer.nameFormat = this.getConfig().getString("nameformat");
        ChatRenderer.nameHover = this.getConfig().getString("namehover");
        super.onReload();
    }

}
