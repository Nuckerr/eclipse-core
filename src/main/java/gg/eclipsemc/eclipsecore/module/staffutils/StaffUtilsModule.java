package gg.eclipsemc.eclipsecore.module.staffutils;

import cloud.commandframework.arguments.standard.StringArgument;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.event.PrivateMessageEvent;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.staffutils.listener.ConnectionListeners;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.AdminChatPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerLoadUpPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerOfflinePacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.ServerOnlinePacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffChatPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffJoinPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffQuitPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.vanish.VanishManager;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

/**
 * @author Nucker
 */
public class StaffUtilsModule extends EclipseModule {

    public StaffUtilsModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
        this.sendPacket(new ServerLoadUpPacket(this));
    }

    @Override
    public String getName() {
        return "StaffUtils";
    }

    private VanishManager vanishManager;

    @Override
    protected void onEnable() {
        this.registerDefaults();
        this.registerPackets();
        this.registerCommands();
        this.registerListener(new ConnectionListeners(this));
        this.vanishManager = new VanishManager(this);

        this.schedule(() -> this.sendPacket(new ServerOnlinePacket(this)), 0L); // First tick doesn't happen till after the
        // server is finished loading. Thus, this is called when the server is finished loading
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("isJoinAlert") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return false;
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<String>("lastServer") {
            @Override
            public String parseData(final UUID uuid) {
                return "null";
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("socialSpy") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return false;
            }
        });

        this.registerListener(new Listener() {
            @EventHandler
            public void onChat(PrivateMessageEvent event) {
                for (final Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                    EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(bukkitPlayer);
                    if(!player.equals(event.getSender())) {
                    if(player.getPlayerData().getBool("socialSpy")) {
                        Component componentFrom =
                                MiniMessage.get().parse("<gold>[<yellow>Social Spy<gold>] ").append(Component.text("(").color(
                                                NamedTextColor.GOLD)
                                        .append(Component
                                                .text(event.getSender().getBukkitPlayer().getName() + " -> " + event
                                                        .getReceiver()
                                                        .getBukkitPlayer()
                                                        .getName())
                                                .color(NamedTextColor.YELLOW))
                                        .append(Component.text(")").color(NamedTextColor.GOLD))
                                        .append(Component.text(": ").color(NamedTextColor.GRAY))
                                        .append(event.getMessage().colorIfAbsent(NamedTextColor.YELLOW)));
                            player.sendMessage(componentFrom);
                        }
                    }
                }
            }

            @EventHandler
            public void onCommand(PlayerCommandPreprocessEvent event) {
                for (final Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                    EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(bukkitPlayer);
                    if(player.getPlayerData().getBool("socialSpy")) {
                        Component componentFrom =
                                MiniMessage.get().parse("<gold>[<yellow>Command Spy<gold>] ").append(Component.text("(").color(NamedTextColor.GOLD)
                                        .append(Component.text(event.getPlayer().getName())
                                                .color(NamedTextColor.YELLOW))
                                        .append(Component.text(")").color(NamedTextColor.GOLD))
                                        .append(Component.text(": ").color(NamedTextColor.GRAY))
                                        .append(Component.text(event.getMessage()).colorIfAbsent(NamedTextColor.YELLOW)));
                        player.sendMessage(componentFrom);
                    }
                }
            }
        });

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
        this.registerPacket(new StaffQuitPacket());
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
        this.registerCommand(this.getCommandBuilder("staffchat", "staffc", "schat", "sc")
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .permission("eclipsecore.staffutils.staffchat")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer sender)
                        this.sendPacket(new StaffChatPacket(sender, MiniMessage.get().parse(c.get("message")),
                                StaffUtilsModule.this));
                    else {
                        c.getSender().sendMessage(Component.text("You must be ingame to run this command").color(NamedTextColor.RED));
                    }
                }));
        this.registerCommand(this.getCommandBuilder("adminchat", "adminc", "achat", "ac")
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .permission("eclipsecore.staffutils.adminchat")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer sender)
                        this.sendPacket(new AdminChatPacket(sender, MiniMessage.get().parse(c.get("message")), StaffUtilsModule.this));
                    else {
                        c.getSender().sendMessage(Component.text("You must be ingame to run this command").color(NamedTextColor.RED));
                    }
                }));
        this.registerCommand(this.getCommandBuilder("socialspy", "sspy", "commandspy", "cmdspy", "cspy")
                .permission("eclipsecore.staffutils.socialspy")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer player) {
                        boolean newStatus = !player.getPlayerData().getBool("socialSpy");
                        player.getPlayerData().set("socialSpy", newStatus);
                        player.sendMessage(Component.text("Social spy has been toggled " + (newStatus ? "on" : "off"))
                                .color(newStatus ? NamedTextColor.GREEN : NamedTextColor.RED));
                    }
                }));
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public String getServerName() {
        return this.getConfig().getString("server-name");
    }
}
