package gg.eclipsemc.eclipsecore;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.viaversion.viaversion.api.Via;
import gg.eclipsemc.eclipsecore.chat.ChatListener;
import gg.eclipsemc.eclipsecore.chat.EclipseChatRenderer;
import gg.eclipsemc.eclipsecore.listener.PlayerListListener;
import gg.eclipsemc.eclipsecore.objects.Tab;
import gg.eclipsemc.eclipsecore.tab.LegacyTab;
import gg.eclipsemc.eclipsecore.tab.RGBTab;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Level;

public final class EclipseCore extends JavaPlugin {

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
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("reload")
                        .permission("eclipsecore.reload")
                        .handler(c -> {
                            c.getSender().sendMessage(MiniMessage.get().parse("<red>Reloading EclipseChat..."));
                            reloadConfig();
                            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                getRgbTab().reloadPlayerList();
                                getLegacyTab().reloadPlayerList();
                                refreshPlayerList();
                            });
                            Bukkit.getScheduler().runTaskAsynchronously(this, this::reloadChat);
                            c.getSender().sendMessage(MiniMessage.get().parse("<green>Reloaded EclipseChat!"));
                        })
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("about")
                        .permission("eclipsecore.about")
                        .handler(c -> c.getSender().sendMessage(MiniMessage.get().parse("<blue>EclipseChat v" + getDescription().getVersion() + "\n<green>By <rainbow>SimplyMerlin")))
        );
    }

    public void setupPlayerList() {
        if (getConfig().getBoolean("tab.enabled")) {
            legacyTab = new LegacyTab(this);
            rgbTab = new RGBTab(this);
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
        if(Via.getAPI().getPlayerVersion(player.getUniqueId()) >= 735 /* 1.16 */) {
            rgbTab.refreshTabList(player);
        }else {
            legacyTab.refreshTabList(player);
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
