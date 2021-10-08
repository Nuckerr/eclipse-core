package gg.eclipsemc.eclipsecore.module.staffutils;

import cloud.commandframework.arguments.standard.StringArgument;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.AdminChatPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerLoadUpPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerOfflinePacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerOnlinePacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffChatPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffJoinPacket;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.OfflineEclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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

        this.sendPacket(new ServerLoadUpPacket(this));

        this.schedule(() -> this.sendPacket(new ServerOnlinePacket(this)), 0L); // First tick doesn't happen till after the
        // server is finished loading. Thus, this is called when the server is finished loading

        super.onEnable();
    }

    @Override
    protected void onDisable() {
        this.sendPacket(new ServerOfflinePacket(this));
        super.onDisable();
    }

    private void registerPackets() {
        this.registerPacket(new StaffJoinPacket());
        this.registerPacket(new ServerOfflinePacket());
        this.registerPacket(new ServerOnlinePacket());
        this.registerPacket(new ServerLoadUpPacket());
        this.registerPacket(new StaffChatPacket());
        this.registerPacket(new AdminChatPacket());
    }

    private void registerDefaults() {
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("isOnline") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return Bukkit.getOfflinePlayer(uuid).isOnline();
            }
        });
    }


    private void registerCommands() {
        //TODO: fix these commands not working (something to do with the packets)
        this.registerCommand(this.getCommandBuilder("staffchat", "staffc", "schat", "sc")
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .permission("eclipsecore.staffutils.staffchat")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer sender)
                        new StaffChatPacket(sender, MiniMessage.get().parse(c.get("message")), StaffUtilsModule.this);
                    else {
                        c.getSender().sendMessage(Component.text("You must be ingame to run this command").color(NamedTextColor.RED));
                    }
                }));
        this.registerCommand(this.getCommandBuilder("adminchat", "adminc", "achat", "ac")
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .permission("eclipsecore.staffutils.adminchat")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer sender)
                        new AdminChatPacket(sender, MiniMessage.get().parse(c.get("message")), StaffUtilsModule.this);
                    else {
                        c.getSender().sendMessage(Component.text("You must be ingame to run this command").color(NamedTextColor.RED));
                    }
                }));
    }

    private void registerListeners() {
        this.registerListener(new Listener() {

            @EventHandler
            public void onPlayerLogin(PlayerLoginEvent event) {
                EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(event.getPlayer());

                if(!player.getPlayerData().getBool("isOnline")) {
                    StaffUtilsModule.this.sendPacket(new StaffJoinPacket(player, StaffUtilsModule.this));
                }else {

                }

                player.getPlayerData().set("isOnline", true);
            }
        });
    }

    public String getServerName() {
        return this.getConfig().getString("server-name");
    }
}
