package gg.eclipsemc.eclipsecore;

import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.module.tab.TabModule;
import gg.eclipsemc.eclipsecore.parser.EclipseModuleParser;
import gg.eclipsemc.eclipsecore.parser.argument.EclipseModuleArgument;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public final class EclipseCore extends JavaPlugin {

    PaperCommandManager<CommandSender> paperCommandManager;
    public final Set<EclipseModule> modules = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        modules.add(new TabModule(this));
        modules.add(new ChatModule(this));
        enableStartupModules();
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

    public void reloadModules() {
        for (final EclipseModule module : modules) {
            module.reload();
        }
    }

    public void enableModules() {
        for (final EclipseModule module : modules) {
            module.enable();
        }
    }

    public void enableStartupModules() {
        for (final EclipseModule module : modules) {
            if (module.shouldEnableOnStartup())
                module.enable();
        }
    }

    public void disableModules() {
        for (final EclipseModule module : modules) {
            module.disable();
        }
    }

    private void registerCommands() throws Exception {
        paperCommandManager = new PaperCommandManager<>(
                this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        paperCommandManager.registerAsynchronousCompletions();
        paperCommandManager.registerBrigadier();
        paperCommandManager.getParserRegistry().registerParserSupplier(
                TypeToken.get(EclipseModule.class),
                options -> new EclipseModuleParser<>(this)
        );
        final CaptionRegistry<CommandSender> registry = paperCommandManager.getCaptionRegistry();
        if (registry instanceof final FactoryDelegatingCaptionRegistry<CommandSender> factoryRegistry) {
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
    }

    public Set<EclipseModule> getModules() {
        return modules;
    }

}
