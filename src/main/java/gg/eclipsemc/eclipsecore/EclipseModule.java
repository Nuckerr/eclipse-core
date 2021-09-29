package gg.eclipsemc.eclipsecore;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class EclipseModule implements Listener {

    protected EclipseCore eclipseCore;
    protected final Set<Listener> listeners = new HashSet<>();
    protected final Set<BukkitTask> tasks = new HashSet<>();
    private boolean isEnabled;

    public EclipseModule(EclipseCore eclipseCore) {
        this.eclipseCore = eclipseCore;
    }

    public void enable() {
        registerListener(this);
        isEnabled = true;
    }

    public void disable() {
        listeners.forEach(HandlerList::unregisterAll);
        tasks.forEach(BukkitTask::cancel);
        listeners.clear();
        tasks.clear();
        isEnabled = false;
    }

    public void reload() {

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    protected void registerListener(Listener listener) {
        listeners.add(listener);
        eclipseCore.getServer().getPluginManager().registerEvents(listener, eclipseCore);
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

}
