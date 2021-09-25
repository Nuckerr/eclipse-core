package gg.eclipsemc.eclipsechat;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import gg.eclipsemc.eclipsechat.chat.ChatListener;
import gg.eclipsemc.eclipsechat.chat.EclipseChatRenderer;
import gg.eclipsemc.eclipsechat.listener.PlayerListListener;
import gg.eclipsemc.eclipsechat.objects.Tab;
import gg.eclipsemc.eclipsechat.tab.LegacyTab;
import gg.eclipsemc.eclipsechat.tab.RGBTab;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.logging.Level;

public final class EclipseChat extends JavaPlugin {

    PaperCommandManager<CommandSender> paperCommandManager;
    private Tab rgbTab;
    private Tab legacyTab;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupPlayerList();
        setupChat();
        try {
            registerCommands();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed registering commands!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() throws Exception {
        paperCommandManager = new PaperCommandManager<>(
                this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        paperCommandManager.registerAsynchronousCompletions();
        paperCommandManager.registerBrigadier();
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsechat")
                        .literal("reload")
                        .permission("eclipsechat.reload")
                        .handler(c -> {
                            c.getSender().sendMessage(MiniMessage.get().parse("<red>Reloading EclipseChat..."));
                            reloadConfig();
                            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                getRgbTab().reloadPlayerList();
                                getLegacyTab().reloadPlayerList();
                            });
                            Bukkit.getScheduler().runTaskAsynchronously(this, this::reloadChat);
                            c.getSender().sendMessage(MiniMessage.get().parse("<green>Reloaded EclipseChat!"));
                        })
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsechat")
                        .literal("about")
                        .permission("eclipsechat.about")
                        .handler(c -> c.getSender().sendMessage(MiniMessage.get().parse("<blue>EclipseChat v" + getDescription().getVersion() + "\n<green>By <rainbow>SimplyMerlin")))
        );
    }

    public void setupPlayerList() {
        if (getConfig().getBoolean("tab.enabled")) {
            legacyTab = new LegacyTab();
            rgbTab = new RGBTab();
            getServer().getPluginManager().registerEvents(new PlayerListListener(this), this);
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList), 0L, 200L);
        }
    }

    public void setupChat() {
        if (getConfig().getBoolean("chat.enabled")) {
            reloadChat();
            Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        }
    }

    public void reloadChat() {
        EclipseChatRenderer.nameFormat = getConfig().getString("chat.nameformat");
        EclipseChatRenderer.nameHover = getConfig().getString("chat.namehover");
    }

    public void refreshPlayerList(Player player) {
        if(player.getProtocolVersion() >= 735 /* 1.16 */) {
            rgbTab.loadTabList(player);
        }else {
            legacyTab.loadTabList(player);
        }
    }

    public void refreshPlayerList() {
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList);
    }

    public Tab getLegacyTab() {
        return legacyTab;
    }

    public Tab getRgbTab() {
        return rgbTab;
    }

}
