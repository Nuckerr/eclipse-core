// Decompiled with: FernFlower
// Class Version: 16
package gg.eclipsemc.eclipsecore;

import cloud.commandframework.Command;
import com.mongodb.client.MongoCollection;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.ReloadSettings;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EclipseModule implements Listener {
    protected EclipseCore eclipseCore;
    protected final Set<Listener> listeners = new HashSet();
    protected final Set<BukkitTask> tasks = new HashSet();
    private boolean isEnabled;
    private boolean enableOnStartup;
    private Yaml config;
    private MongoCollection<Document> collection;

    public EclipseModule(EclipseCore eclipseCore) {
        this.eclipseCore = eclipseCore;
        this.setupConfig();
        this.setupMongo();
    }

    public String getName() {
        return "NotOverwrittenNagSomeone";
    }

    public String getConfigName() {
        return this.getName().toLowerCase() + ".yml";
    }

    protected void setupConfig() {
        File configFile = null;

        try {
            File dir = this.eclipseCore.getDataFolder();
            if (!dir.exists()) {
                dir.mkdir();
            }

            configFile = new File(dir, this.getConfigName());
            if (!configFile.exists()) {
                if (this.eclipseCore.getResource(this.getConfigName()) == null) {
                    configFile.createNewFile();
                } else {
                    this.eclipseCore.saveResource(this.getConfigName(), false);
                }
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        this.config = new Yaml(configFile);
        this.config.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS);
        this.config.setReloadSettings(ReloadSettings.MANUALLY);
        this.enableOnStartup = this.config.getBoolean("enableonstartup");
    }

    protected void setupMongo() {
        this.collection = this.eclipseCore.createCollection(this.getName());
    }

    public void enable() {
        if (!this.isEnabled) {
            this.onEnable();
        }
    }

    protected void onEnable() {
        this.registerListener(this);
        this.isEnabled = true;
    }

    public void disable() {
        if (this.isEnabled) {
            this.onDisable();
        }
    }

    protected void onDisable() {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.tasks.forEach(BukkitTask::cancel);
        this.listeners.clear();
        this.tasks.clear();
        this.isEnabled = false;
    }

    public void reload() {
        if (this.isEnabled) {
            this.onReload();
        }
    }

    protected void onReload() {
        this.getConfig().forceReload();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void registerListener(Listener listener) {
        this.listeners.add(listener);
        this.eclipseCore.getServer().getPluginManager().registerEvents(listener, this.eclipseCore);
    }

    public <T> void registerCommand(@NonNull T instance) {
        this.eclipseCore.getAnnotationParser().parse(instance);
    }

    public void runAsync(Runnable runnable) {
        this.eclipseCore.getServer().getScheduler().runTaskAsynchronously(this.eclipseCore, runnable);
    }

    public void schedule(Runnable runnable, long delay) {
        BukkitTask task = this.eclipseCore.getServer().getScheduler().runTaskLater(this.eclipseCore, runnable, delay);
        this.tasks.add(task);
    }

    public void scheduleRepeating(Runnable runnable, long delay, long interval) {
        BukkitTask task = this.eclipseCore.getServer().getScheduler().runTaskTimer(this.eclipseCore, runnable, delay, interval);
        this.tasks.add(task);
    }

    public void scheduleAsync(Runnable runnable, long delay) {
        BukkitTask task = this.eclipseCore.getServer().getScheduler().runTaskLaterAsynchronously(this.eclipseCore, runnable, delay);
        this.tasks.add(task);
    }

    public void scheduleRepeatingAsync(Runnable runnable, long delay, long interval) {
        BukkitTask task = this.eclipseCore.getServer().getScheduler().runTaskTimerAsynchronously(this.eclipseCore, runnable, delay, interval);
        this.tasks.add(task);
    }

    public Yaml getConfig() {
        return this.config;
    }

    public boolean shouldEnableOnStartup() {
        return this.enableOnStartup;
    }

    public Command.Builder<EclipseSender> getCommandBuilder(String name, String... aliases) {
        return this.eclipseCore.paperCommandManager.commandBuilder(name, aliases);
    }

    public void registerCommand(Command.Builder<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    public void registerCommand(Command<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    public void saveDocument(Bson filter, Document document) {
        Bukkit.getScheduler().runTaskAsynchronously(this.eclipseCore, () -> {
            Document res = (Document)this.collection.find(filter).first();
            if (res == null) {
                this.collection.insertOne(document);
            } else {
                this.collection.findOneAndUpdate(filter, (Bson)document);
            }

        });
    }

    public void saveDocument(Bson filter, String key, String newValue) {
        Bukkit.getScheduler().runTaskAsynchronously(this.eclipseCore, () -> {
            Document document = new Document(key, newValue);
            Document res = (Document)this.collection.find(filter).first();
            if (res == null) {
                this.collection.insertOne(document);
            } else {
                this.collection.findOneAndUpdate(filter, (Bson)document);
            }

        });
    }

    public Document getDocument(Bson filter) {
        return (Document)this.collection.find(filter).first();
    }

    public void sendPacket(RedisPacket packet) {
        this.eclipseCore.getRedisManager().sendPacket(packet);
    }

    public void registerPacket(RedisPacket packet) {
        Bukkit.getLogger().info("Registering packet " + packet.getIdentifier());
        this.eclipseCore.getRedisManager().registerPacket(packet);
    }

    public void deleteDocument(Bson filter) {
        this.collection.findOneAndDelete(filter);
    }
}
