package gg.eclipsemc.eclipsecore.module.chat;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.chat.command.MessageCommand;
import gg.eclipsemc.eclipsecore.module.chat.listener.ChatListener;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatModule extends EclipseModule {


    private int slowMode = 0;
    private final List<UUID> slowModeCooldown;

    public ChatModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
        this.slowModeCooldown = new ArrayList<>();
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
        registerListener(new ChatListener(this));
        onReload();
        this.registerDefaults();
        this.registerCommands();
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

    private void registerCommands() {
        this.registerCommand(this.getCommandBuilder("ignore", "addignore", "ignorelist", "block", "blockplayer")
                .argument(PlayerArgument.of("player"))
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer player) {
                        EclipsePlayer target = EclipsePlayer.getFromCraftPlayer(c.get("player"));
                        List<UUID> ignoreList = player.getPlayerData().getListOf("ignoreList", UUID.class);
                        if(ignoreList.contains(target.getUniqueId())) {
                            player.sendMessage(Component.text(target.getBukkitPlayer().getName() + " is already in your ignore " +
                                            "list").color(NamedTextColor.RED)
                                    .hoverEvent(Component.text("Click to remove them from your ignore list").color(NamedTextColor.YELLOW))
                                    .clickEvent(ClickEvent.suggestCommand("/unignore " + target.getBukkitPlayer().getName())));
                            return;
                        }
                        ignoreList.add(target.getUniqueId());
                        player.getPlayerData().set("ignoreList", ignoreList);
                        player.sendMessage(Component.text("You have added " + target.getBukkitPlayer().getName() + " to your " +
                                "ignore list").color(NamedTextColor.GREEN));
                    }else {
                        c.getSender().sendMessage(Component.text("You must be in game to run this command").color(NamedTextColor.RED));
                    }
                }));
        this.registerCommand(this.getCommandBuilder("unignore", "removeignore", "unblock", "blockremove")
                .argument(PlayerArgument.of("player"))
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer player) {
                        EclipsePlayer target = EclipsePlayer.getFromCraftPlayer(c.get("player"));
                        List<UUID> ignoreList = player.getPlayerData().getListOf("ignoreList", UUID.class);
                        if(ignoreList.contains(target.getUniqueId())) {
                            ignoreList.remove(target.getUniqueId());
                            player.getPlayerData().set("ignoreList", ignoreList);
                            player.sendMessage(Component.text(target.getBukkitPlayer().getName() + " has been removed from " +
                                    "your ignore list").color(NamedTextColor.GREEN));
                        }else {
                            player.sendMessage(Component.text(target.getBukkitPlayer().getName() + " is not in your ignore " +
                                    "list").color(NamedTextColor.RED)
                                    .hoverEvent(Component.text("Click to add them to your ignore list").color(NamedTextColor.YELLOW))
                                    .clickEvent(ClickEvent.suggestCommand("/ignore " + target.getBukkitPlayer().getName())));
                        }
                    }else {
                        c.getSender().sendMessage(Component.text("You must be in game to run this command").color(NamedTextColor.RED));
                    }
                }));

        this.registerCommand(this.getCommandBuilder("togglechat", "togglec", "tchat")
                .handler(c -> {
                    if(c.getSender() instanceof EclipsePlayer player) {
                        boolean newValue = !player.getPlayerData().getBool("chatToggled");
                        player.getPlayerData().set("chatToggled", newValue);
                        player.sendMessage(Component.text("Chat has been toggled ").color(NamedTextColor.GREEN)
                                .append(newValue ? Component.text("on").color(NamedTextColor.GREEN)
                                        : Component.text("off").color(NamedTextColor.RED)));
                    }else {
                        c.getSender().sendMessage(Component.text("You must be in game to run this command").color(NamedTextColor.RED));
                    }
                }));

        // There is no mutechat as that is handled in litebans
        this.registerCommand(this.getCommandBuilder("slowmode", "slowm", "smode")
                .permission("eclipsecore.chat.slowmode")
                .argument(IntegerArgument.optional("amount"))
                .handler(c -> {
                    if(!c.getOptional("amount").isPresent() || c.getOrDefault("amount", 0) == 0) {
                        this.setSlowMode(0);
                        Bukkit.broadcast(Component.text("Slow mode has been toggled off").color(NamedTextColor.GREEN));
                    }else {
                        this.setSlowMode(c.get("amount"));
                        Bukkit.broadcast(Component.text("Slow mode has been set to " + c.get("amount") + " seconds").color(NamedTextColor.RED));
                    }
                }));

        this.registerCommand(new MessageCommand());
    }

    @Override
    public void onReload() {
        ChatRenderer.nameFormat = this.getConfig().getString("nameformat");
        ChatRenderer.nameHover = this.getConfig().getString("namehover");
        super.onReload();
    }


    public int getSlowMode() {
        return slowMode;
    }

    public void setSlowMode(final int slowMode) {
        this.slowMode = slowMode;
    }

    public List<UUID> getSlowModeCooldown() {
        return slowModeCooldown;
    }

    public void addToCache(UUID uuid) {
        this.slowModeCooldown.add(uuid);
        this.scheduleAsync(() -> {
            this.slowModeCooldown.remove(uuid);
        }, 20L * this.getSlowMode());
    }
}
