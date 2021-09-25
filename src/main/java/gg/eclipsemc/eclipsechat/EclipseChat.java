package gg.eclipsemc.eclipsechat;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import gg.eclipsemc.eclipsechat.chat.ChatListener;
import gg.eclipsemc.eclipsechat.chat.EclipseChatRenderer;
import gg.eclipsemc.eclipsechat.listener.PlayerListListener;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Level;

public final class EclipseChat extends JavaPlugin {

    private String playerListHeader;
    private String playerListFooter;
    private String tabName;
    PaperCommandManager<CommandSender> paperCommandManager;

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
                            Bukkit.getScheduler().runTaskAsynchronously(this, this::reloadPlayerList);
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
            reloadPlayerList();
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

    public void reloadPlayerList() {
        String configHeader = getConfig().getString("tab.header");
        String configFooter = getConfig().getString("tab.footer");
        getLogger().log(Level.INFO, "Got tab header " + configHeader);
        getLogger().log(Level.INFO, "Got tab footer " + configFooter);
        if (configHeader == null || configFooter == null) {
            playerListHeader = "";
            playerListFooter = "";
            return;
        }
        playerListHeader = configHeader;
        playerListFooter = configFooter;
        tabName = getConfig().getString("tab.playername");
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(getPlayerListHeader(player), getPlayerListFooter(player));
            if (tabName == null) return;
            player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, tabName)));
        }
    }

    public void refreshPlayerList(Player player) {
        player.sendPlayerListHeaderAndFooter(getPlayerListHeader(player), getPlayerListFooter(player));
        player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, tabName)));
    }

    public void refreshPlayerList() {
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList);
    }

    public Component getPlayerListHeader(Player player) {
        return MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, playerListHeader));
    }

    public Component getPlayerListFooter(Player player) {
        return MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, playerListFooter));
    }

    public String getTabName() {
        return tabName;
    }

}
