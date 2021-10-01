package gg.eclipsemc.eclipsecore;

import cloud.commandframework.Command;
import com.mongodb.client.MongoCollection;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.ReloadSettings;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    protected final Set<Listener> listeners = new HashSet<>();
    protected final Set<BukkitTask> tasks = new HashSet<>();
    private boolean isEnabled;
    private boolean enableOnStartup;
    private Yaml config;
    private MongoCollection<Document> collection;

    public EclipseModule(EclipseCore eclipseCore) {
        this.eclipseCore = eclipseCore;
        setupConfig();
    }

    public String getName() {
        return "NotOverwrittenNagSomeone";
    }

    /**
     * Override to change the name of the config
     */
    public String getConfigName() {
        return this.getName().toLowerCase() + ".yml";
    }

    protected void setupConfig() {
        File configFile = null;
        try {
            File dir = eclipseCore.getDataFolder();
            if(!dir.exists()) {
                dir.mkdir();
            }
            configFile = new File(dir, this.getConfigName());
            if(!configFile.exists()) {
                if(eclipseCore.getResource(this.getConfigName()) == null) {
                    configFile.createNewFile();
                }else {
                    eclipseCore.saveResource(this.getConfigName(), false);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.config = new Yaml(configFile);
        this.config.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS);
        this.config.setReloadSettings(ReloadSettings.MANUALLY); // Use Yaml#forceReload() (its safe, I swear)
        enableOnStartup = this.config.getBoolean("enableonstartup");
    }

    protected void setupMongo() {
        collection = eclipseCore.getMongoClient().getDatabase("EclipseCore").getCollection(this.getName());
    }

    public void enable() {
        if (isEnabled)
            return;
        onEnable();
    }

    protected void onEnable() {
        registerListener(this);
        isEnabled = true;
    }

    public void disable() {
        if (!isEnabled)
            return;
        onDisable();
    }

    protected void onDisable() {
        listeners.forEach(HandlerList::unregisterAll);
        tasks.forEach(BukkitTask::cancel);
        listeners.clear();
        tasks.clear();
        isEnabled = false;
    }

    public void reload() {
        if (!isEnabled)
            return;
        onReload();
    }

    protected void onReload() {
        this.getConfig().forceReload(); // I swear this is safe
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    protected void registerListener(Listener listener) {
        listeners.add(listener);
        eclipseCore.getServer().getPluginManager().registerEvents(listener, eclipseCore);
    }

    protected <T> void registerCommand(final @NonNull T instance) {
        eclipseCore.getAnnotationParser().parse(instance);
    }

    protected void runAsync(Runnable runnable) {
        eclipseCore.getServer().getScheduler().runTaskAsynchronously(eclipseCore, runnable);
    }

    protected void schedule(Runnable runnable, long delay) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskLater(eclipseCore, runnable, delay);
        tasks.add(task);
    }

    protected void scheduleRepeating(Runnable runnable, long delay, long interval) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskTimer(eclipseCore, runnable, delay, interval);
        tasks.add(task);
    }

    protected void scheduleAsync(Runnable runnable, long delay) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskLaterAsynchronously(eclipseCore, runnable, delay);
        tasks.add(task);
    }

    protected void scheduleRepeatingAsync(Runnable runnable, long delay, long interval) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskTimerAsynchronously(eclipseCore, runnable, delay, interval);
        tasks.add(task);
    }

    public Yaml getConfig() {
        return config;
    }

    public boolean shouldEnableOnStartup() {
        return enableOnStartup;
    }

    protected Command.Builder<EclipseSender> getCommandBuilder(String name, String... aliases) {
        return this.eclipseCore.paperCommandManager.commandBuilder(name, aliases);
    }

    protected void registerCommand(Command.Builder<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    protected void registerCommand(Command<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    /**
     * Will append a document (or create if it is missing) in the module's collection
     */
    protected void saveDocument(Bson filter, Document document) {
        Bukkit.getScheduler().runTaskAsynchronously(eclipseCore, () -> {
            Document res = collection.find(filter).first();
            if(res == null) {
                collection.insertOne(document);
            }else {
                collection.findOneAndUpdate(filter, document);
            }
        });
    }


    /**
     * Will append a document (or create if it is missing) in the module's collection
     */
    protected void saveDocument(Bson filter, String key, String newValue) {
        Bukkit.getScheduler().runTaskAsynchronously(eclipseCore, () -> {
            Document document = new Document(key, newValue);

            Document res = collection.find(filter).first();
            if(res == null) {
                collection.insertOne(document);
            }else {
                collection.findOneAndUpdate(filter, document);
            }
        });
    }

    protected void deleteDocument(Bson filter) {
        collection.findOneAndDelete(filter);
    }
}
