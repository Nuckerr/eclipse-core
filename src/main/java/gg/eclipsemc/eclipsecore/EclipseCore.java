package gg.eclipsemc.eclipsecore;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.manager.RedisManager;
import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.module.essentials.EssentialsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.tab.TabModule;
import gg.eclipsemc.eclipsecore.module.tags.TagModule;
import gg.eclipsemc.eclipsecore.object.EclipseCommandSender;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import gg.eclipsemc.eclipsecore.parser.EclipseModuleParser;
import gg.eclipsemc.eclipsecore.parser.argument.EclipseModuleArgument;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public final class EclipseCore extends JavaPlugin {

    PaperCommandManager<EclipseSender> paperCommandManager;
    AnnotationParser<EclipseSender> annotationParser;
    PlayerDataManager playerDataManager;
    MongoClient mongoClient;
    PAPIExpansion expansion;
    RedisManager redisManager;
    public final Set<EclipseModule> modules = new HashSet<>();

    /**
     * Used for setting up databases before modules are loaded
     */
    @Override
    public void onLoad() {
        String host = "database.mongo.host", username = "database.mongo.authentication.username", password = "database.mongo" +
                ".authentication.password", port = "database.mongo.port", auth = "database.mongo.authentication.enabled";
        String uri;
        if (getConfig().getBoolean(auth)) {
            uri = "mongodb://[username]:[password]@[host]:[port]"
                    .replace("[username]", getConfig().getString(username))
                    .replace("[password]", getConfig().getString(password))
                    .replace("[host]", getConfig().getString(host))
                    .replace("[port]", String.valueOf(getConfig().getInt(port)));
        }else {
            uri = "mongodb://[host]:[port]".replace("[host]", getConfig().getString(host))
                    .replace("[port]", String.valueOf(getConfig().getInt(port)));
        }
        mongoClient = new MongoClient(new MongoClientURI(uri));
        redisManager = new RedisManager(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        modules.add(new TabModule(this));
        modules.add(new ChatModule(this));
        modules.add(new EssentialsModule(this));
        modules.add(new TagModule(this));
        modules.add(new StaffUtilsModule(this));
        try {
            registerCommands();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed registering commands!", e);
            getServer().getPluginManager().disablePlugin(this);
        }

        playerDataManager = new PlayerDataManager(this);

        expansion = new PAPIExpansion(this); // Register placeholder api expansion
        expansion.register();
        enableStartupModules();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabling EclipseCore");
        this.disableModules();
        redisManager.closeConnection();
        mongoClient.close();
        getLogger().info("&aEclipseCore is now disabled");
    }

    /**
     * Reloads every module
     */
    public void reloadModules() {
        for (final EclipseModule module : modules) {
            module.reload();
        }
    }

    /**
     * @deprecated Will enable all modules. Even ones set not to start up on start. Use
     * {@link EclipseCore#enableStartupModules()}
     *
     */
    public void enableModules() {
        for (final EclipseModule module : modules) {
            module.enable();
        }
    }

    /**
     * Enables all modules set to start at launch
     */
    public void enableStartupModules() {
        for (final EclipseModule module : modules) {
            if (module.shouldEnableOnStartup())
                module.enable();
        }
    }

    /**
     * Disables every module
     */
    public void disableModules() {
        for (final EclipseModule module : modules) {
            module.disable();
        }
    }

    private void registerCommands() throws Exception {

        Function<CommandSender, EclipseSender> commandSenderMapper;
        Function<EclipseSender, CommandSender> backwardsCommandSenderMapper;

        commandSenderMapper = commandSender -> {
            if (commandSender instanceof Player) {
                return EclipsePlayer.getPlayerFromBukkit(((Player) commandSender).getPlayer());
            } else {
                return new EclipseCommandSender(commandSender);
            }
        };

        backwardsCommandSenderMapper = eclipseSender -> {
            if (eclipseSender instanceof EclipsePlayer) {
                return ((EclipsePlayer) eclipseSender).getBukkitPlayer();
            } else {
                return ((EclipseCommandSender) eclipseSender).getSender();
            }
        };

        paperCommandManager = new PaperCommandManager<>(
                this, CommandExecutionCoordinator.simpleCoordinator(), commandSenderMapper, backwardsCommandSenderMapper);
        paperCommandManager.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, true);
        paperCommandManager.setSetting(CommandManager.ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);
        paperCommandManager.registerAsynchronousCompletions();
        paperCommandManager.registerBrigadier();
        paperCommandManager.getParserRegistry().registerParserSupplier(
                TypeToken.get(EclipseModule.class),
                options -> new EclipseModuleParser<>(this)
        );
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        annotationParser = new AnnotationParser<>(
                paperCommandManager,
                EclipseSender.class,
                commandMetaFunction
        );
        final CaptionRegistry<EclipseSender> registry = paperCommandManager.getCaptionRegistry();
        if (registry instanceof final FactoryDelegatingCaptionRegistry<EclipseSender> factoryRegistry) {
            factoryRegistry.registerMessageFactory(
                    Caption.of("argument.parse.failure.module"),
                    (context, key) -> "'{input}' is not a valid module"
            );
        }
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("reload")
                        .permission("eclipsecore.reload")
                        .handler(c -> {
                            c.getSender().sendMessage(MiniMessage.get().parse("<red>Reloading EclipseCore..."));
                            reloadConfig();
                            Bukkit.getScheduler().runTaskAsynchronously(this, this::reloadModules);
                            c.getSender().sendMessage(MiniMessage.get().parse("<green>Reloaded EclipseCore!"));
                        })
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("about")
                        .permission("eclipsecore.about")
                        .handler(c -> c
                                .getSender()
                                .sendMessage(MiniMessage
                                        .get()
                                        .parse("<blue>EclipseCore v" + getDescription()
                                                .getVersion() + "\n<green>By <rainbow>SimplyMerlin")))
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("module")
                        .literal("list")
                        .permission("eclipsecore.module.list")
                        .handler(c -> {
                            TextComponent.Builder componentBuilder = Component.text()
                                    .append(Component.text("All modules:").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                            for (final EclipseModule module : modules) {
                                componentBuilder.append(Component.newline()
                                        .append(Component.text(module.getName() + ": ").color(NamedTextColor.GRAY))
                                        .append((module.isEnabled() ? Component.text("Enabled").color(NamedTextColor.GREEN) :
                                                Component.text("Disabled").color(NamedTextColor.RED))
                                                .clickEvent(ClickEvent.suggestCommand("/eclipsecore module " + (!module.isEnabled()
                                                        ? "enable"
                                                        : "disable") + " " + module
                                                        .getName()))
                                                .hoverEvent(Component.text()
                                                        .append(Component.text("Click to ").color(NamedTextColor.GRAY))
                                                        .append(!module.isEnabled() ?
                                                                Component.text("enable").color(NamedTextColor.GREEN) :
                                                                Component.text("disable").color(NamedTextColor.RED))
                                                        .append(Component
                                                                .text(" " + module.getName() + "!")
                                                                .color(NamedTextColor.GRAY))
                                                        .append(Component.newline())
                                                        .append(Component.newline())
                                                        .append(Component.text("/eclipsecore module " + (!module.isEnabled()
                                                                ? "enable"
                                                                : "disable") + " " + module
                                                                .getName()).color(NamedTextColor.GOLD))
                                                        .build())));
                            }
                            c.getSender().sendMessage(componentBuilder.build());
                        })
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("module")
                        .literal("enable")
                        .argument(new EclipseModuleArgument<>(true, "module", this))
                        .permission("eclipsecore.module.enable")
                        .handler(c -> ((EclipseModule) c.get("module")).enable())
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("module")
                        .literal("disable")
                        .argument(new EclipseModuleArgument<>(true, "module", this))
                        .permission("eclipsecore.module.disable")
                        .handler(c -> ((EclipseModule) c.get("module")).disable())
        );
        paperCommandManager.command(
                paperCommandManager.commandBuilder("eclipsecore")
                        .literal("module")
                        .literal("reload")
                        .argument(new EclipseModuleArgument<>(true, "module", this))
                        .permission("eclipsecore.module.reload")
                        .handler(c -> ((EclipseModule) c.get("module")).reload())
        );
    }

    public MongoCollection<Document> createCollection(String name) {
        try {
            mongoClient.getDatabase("EclipseCore").createCollection(name);
            return mongoClient.getDatabase("EclipseCore").getCollection(name);
        }catch (MongoCommandException e) {
            if(e.getErrorCode() != 48) e.printStackTrace();
            return mongoClient.getDatabase("EclipseCore").getCollection(name);
        }
    }

    public Set<EclipseModule> getModules() {
        return modules;
    }

    public AnnotationParser<EclipseSender> getAnnotationParser() {
        return annotationParser;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PAPIExpansion getPlaceholderAPIExpansion() {
        return expansion;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

}
