package gg.eclipsemc.eclipsecore.module.staffutils;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffJoinPacket;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * @author Nucker
 */
public class StaffUtilsModule extends EclipseModule {

    public StaffUtilsModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "StaffUtils";
    }

    @Override
    protected void onEnable() {
        this.registerDefaults();
        this.registerPackets();
        this.registerCommands();
        this.registerListeners();
        
        super.onEnable();
    }

    private void registerPackets() {
        this.registerPacket(new StaffJoinPacket());
    }

    private void registerDefaults() {
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("isOnline") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return OfflineEclipsePlayer.getPlayerByUUID(uuid).isOnline();
            }
        });
    }


    private void registerCommands() {

    }

    private void registerListeners() {
        this.registerListener(new Listener() {

            @EventHandler
            public void onPlayerLogin(PlayerLoginEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(event.getPlayer());

                if(!player.getPlayerData().getBool("isOnline")) {
                    StaffUtilsModule.this.sendPacket(new StaffJoinPacket(player, StaffUtilsModule.this));
                }

                player.getPlayerData().set("isOnline", true);
            }
        });
    }

    public String getServerName() {
        return this.getConfig().getString("server-name");
    }
}
