package gg.eclipsemc.eclipsecore.object;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

/**
 * @author Nucker
 */
public class PlayerData {

    private final PlayerDataManager manager;
    private final Document document;
    private final UUID id;

    public PlayerData(UUID uuid) {
        this.manager = JavaPlugin.getPlugin(EclipseCore.class).getPlayerDataManager();

        this.id = uuid;
        this.document = manager.getOrCreate(uuid);
    }

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(id).isOnline();
    }

    public boolean hasPlayedBefore() {
        return Bukkit.getOfflinePlayer(id).hasPlayedBefore();
    }

    public String getString(String key) {
        return document.getString(key);
    }

    public int getInt(String key) {
        return document.getInteger(key);
    }

    public boolean getBool(String key) {
        Boolean res = document.getBoolean(key);
        if(res == null) {
            return false;
        }
        return res;
    }

    public <T> List<T> getListOf(String key, Class<T> clazz) {
        return document.getList(key, clazz);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return document.get(key, clazz);
    }

    public void set(String key, Object data) {
        document.append(key, data);
        manager.updateDoc(id, document);
    }


    public Document getDocument() {
        return document;
    }
}
