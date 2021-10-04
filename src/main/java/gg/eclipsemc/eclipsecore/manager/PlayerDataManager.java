package gg.eclipsemc.eclipsecore.manager;

import com.mongodb.client.MongoCollection;
import gg.eclipsemc.eclipsecore.EclipseCore;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Nucker
 */
public class PlayerDataManager {

    private final MongoCollection<Document> collection;
    private final List<DefaultData<?>> defaults;
    private final Map<UUID, Document> cache;

    public PlayerDataManager(EclipseCore core) {
        this.collection = core.getMongoClient().getDatabase("EclipseCore").getCollection("playerdata");
        this.defaults = new ArrayList<>();
        this.cache = new HashMap<>();
        this.addDefaults();
    }

    private void addDefaults() {
        this.addDefault(new DefaultData<String>("name") {
            @Override
            public String parseData(final UUID uuid) {
                return Bukkit.getOfflinePlayer(uuid).getName();
            }
        });
        this.addDefault(new DefaultData<Long>("firstjoin") {
            @Override
            public Long parseData(final UUID uuid) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                if(player.hasPlayedBefore()) {
                    return player.getFirstPlayed();
                }
                return null;
            }
        });
        // TODO: Add more defaults
    }


    public Document getOrCreate(UUID uuid) {
        if(cache.containsKey(uuid))
            return this.checkForDefaults(cache.get(uuid), uuid);

        Bson filter = new Document("_id", uuid);
        if(collection.find(filter).first() == null) {
            Document res = new Document("_id", uuid);
            for (final DefaultData<?> def: defaults) {
                res.put(def.getKey(), def.parseData(uuid) == null ? "null" : def.parseData(uuid));
            }
            return res;
        } else return this.checkForDefaults(collection.find(filter).first(), uuid);
    }

    public Document findByFilter(Bson filter) {
        return collection.find(filter).first();
    }

    public void updateDoc(UUID uuid, Document document) {
        collection.findOneAndReplace(new Document("_id", uuid), document);
    }

    public void addDefault(DefaultData<?> data) {
        this.defaults.add(data);
    }

    public List<DefaultData<?>> getDefaults() {
        return defaults;
    }

    public abstract static class DefaultData<T> {

        private final String key;

        public DefaultData(String key) {
           this.key = key;
        }

        public abstract T parseData(UUID uuid);

        public String getKey() {
            return key;
        }
    }

    public void updateCache() {
        cache.forEach((uuid, doc) -> this.updateCache(uuid));
    }

    public void updateCache(UUID uuid) {
        cache.remove(uuid);
        cache.put(uuid, this.getOrCreate(uuid));
    }

    private Document checkForDefaults(Document document, UUID uuid) {
        for (final DefaultData<?> def : this.getDefaults()) {
            if(!document.containsKey(def.getKey())) {
                document.append(def.getKey(), def.parseData(uuid));
            }
        }

        return document;
    }
}
