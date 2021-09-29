package gg.eclipsemc.eclipsecore;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.module.tab.TabModule;
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
        enableModules();
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
            if (module.isEnabled()) {
                module.reload();
            }
        }
    }

    public void enableModules() {
        for (final EclipseModule module : modules) {
            if (!module.isEnabled()) {
                module.enable();
            }
        }
    }

    public void disableModules() {
        for (final EclipseModule module : modules) {
            if (module.isEnabled())
                module.disable();
        }
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
                        .handler(c -> c.getSender().sendMessage(MiniMessage.get().parse("<blue>EclipseCore v" + getDescription().getVersion() + "\n<green>By <rainbow>SimplyMerlin")))
        );
    }

    public Set<EclipseModule> getModules() {
        return modules;
    }

}
