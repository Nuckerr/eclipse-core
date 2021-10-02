package gg.eclipsemc.eclipsecore;

import cloud.commandframework.Command;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import redis.clients.jedis.JedisPubSub;

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
        setupMongo();
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

    /**
     * Used to enable the module **DO NOT OVERRIDE**
     */
    public void enable() {
        if (isEnabled)
            return;
        onEnable();
    }

    /**
     * Override this method to execute code when the module starts
     * **YOU MUST SUPER THIS METHOD AT THE BOTTOM OF YOUR METHOD**
     */
    protected void onEnable() {
        registerListener(this);
        isEnabled = true;
    }

    /**
     * Used to disable the module **DO NOT OVERRIDE**
     */
    public void disable() {
        if (!isEnabled)
            return;
        onDisable();
    }

    /**
     * Override this method to execute code when the module stops
     * **YOU MUST SUPER THIS METHOD AT THE BOTTOM OF YOUR METHOD**
     */
    protected void onDisable() {
        listeners.forEach(HandlerList::unregisterAll);
        tasks.forEach(BukkitTask::cancel);
        listeners.clear();
        tasks.clear();
        isEnabled = false;
    }

    /**
     * Used to reload a module. **DO NOT OVERRIDE**
     */
    public void reload() {
        if (!isEnabled)
            return;
        onReload();
    }

    /**
     * Execute code when your module reloads.
     * **MAKE SURE TO SUPER THIS METHOD)
     */
    protected void onReload() {
        this.getConfig().forceReload(); // I swear this is safe
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Register listener to the plugin
     * @param listener The listener you are registering
     */
    public void registerListener(Listener listener) {
        listeners.add(listener);
        eclipseCore.getServer().getPluginManager().registerEvents(listener, eclipseCore);
    }

    /**
     * Register a command
     * @param instance Your command
     * @param <T> The class type of your command
     */
    public <T> void registerCommand(final @NonNull T instance) {
        eclipseCore.getAnnotationParser().parse(instance);
    }

    /**
     * Run a runnable async
     * @param runnable the runnable you are running
     */
    public void runAsync(Runnable runnable) {
        eclipseCore.getServer().getScheduler().runTaskAsynchronously(eclipseCore, runnable);
    }

    /**
     * Schedule a runnable to run after a delay
     * @param runnable the runnable you are running
     * @param delay the delay before running it
     * @see EclipseModule#scheduleAsync(Runnable, long) to schedule a runnable async
     */
    public void schedule(Runnable runnable, long delay) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskLater(eclipseCore, runnable, delay);
        tasks.add(task);
    }

    /**
     * Run a runnable repeatably
     * @param runnable the runnable you are running
     * @param delay the delay before it starts running
     * @param interval the interval between each time the runnable runs
     */
    public void scheduleRepeating(Runnable runnable, long delay, long interval) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskTimer(eclipseCore, runnable, delay, interval);
        tasks.add(task);
    }

    /**
     * Same as {@link EclipseModule#schedule(Runnable, long)} except async
     */
    public void scheduleAsync(Runnable runnable, long delay) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskLaterAsynchronously(eclipseCore, runnable, delay);
        tasks.add(task);
    }

    /**
     * Same as {@link EclipseModule#scheduleRepeating(Runnable, long, long)} except async
     */
    public void scheduleRepeatingAsync(Runnable runnable, long delay, long interval) {
        BukkitTask task = eclipseCore.getServer().getScheduler().runTaskTimerAsynchronously(eclipseCore, runnable, delay, interval);
        tasks.add(task);
    }

    public Yaml getConfig() {
        return config;
    }

    public boolean shouldEnableOnStartup() {
        return enableOnStartup;
    }

    /**
     * Method to access cloud's command builder
     */
    public Command.Builder<EclipseSender> getCommandBuilder(String name, String... aliases) {
        return this.eclipseCore.paperCommandManager.commandBuilder(name, aliases);
    }

    /**
     * Method to register cloud commands
     */
    public void registerCommand(Command.Builder<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    /**
     * Method to register cloud commands
     */
    public void registerCommand(Command<EclipseSender> command) {
        this.eclipseCore.paperCommandManager.command(command);
    }

    /**
     * @return this module's specific collection
     */
    public MongoCollection<Document> getCollection() {
        return collection;
    }

    /**
     * Will append a document (or create if it is missing) in the module's collection
     */
    public void saveDocument(Bson filter, Document document) {
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
    public void saveDocument(Bson filter, String key, String newValue) {
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

    public Document getDocument(Bson filter) {
        return collection.find(filter).first();
    }

    /**
     * Send a redis packet
     * @param packet the packet you are sending
     */
    public void sendPacket(RedisPacket packet) {
        eclipseCore.getJedis().publish(packet.getChannel(), new Gson().fromJson(packet.sendPacket(), String.class));
    }

    /**
     * Delete a document from the modules collection
     * @param filter the filter to find the document
     */
    public void deleteDocument(Bson filter) {
        collection.findOneAndDelete(filter);
    }
}
