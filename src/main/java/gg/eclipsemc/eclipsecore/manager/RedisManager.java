package gg.eclipsemc.eclipsecore.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.object.RedisPacket;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Nucker
 */
public class RedisManager {

    private final EclipseCore core;
    private JedisPool pool;
    private Jedis pub;
    private Jedis sub;
    private Map<String, RedisPacket> packets;

    public RedisManager(EclipseCore core) {
        this.core = core;

        CompletableFuture.runAsync(() -> {
            Bukkit.getLogger().info("Connecting to redis on thread " + Thread.currentThread().getName());
            /*
            jedis = new Jedis(core.getConfig().getString("database.redis.host"), core.getConfig().getInt("database.redis.port"));
            if(!core.getConfig().getString("database.redis.password").equals("")) {
                jedis.auth(core.getConfig().getString("database.redis.password"));
            }
            jedis.select(0);
            jedis.connect();
             */
            core.getLogger().info("Connecting redis pool");
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            this.pool = new JedisPool(poolConfig, core.getConfig().getString("database.redis.host"),
                    core.getConfig().getInt("database.redis.port"));
            core.getLogger().info("Connected pool to redis");

            this.pub = pool.getResource();
            if(!core.getConfig().getString("database.redis.password").equals("")) {
                pub.auth(core.getConfig().getString("database.redis.password"));
            }
            core.getLogger().info(pub.isConnected() ? "Publisher is now connected" : "Publisher failed to connect");

            this.sub = pool.getResource();
            if(!core.getConfig().getString("database.redis.password").equals("")) {
                sub.auth(core.getConfig().getString("database.redis.password"));
            }
            core.getLogger().info(sub.isConnected() ? "Subscriber is now connected" : "Subscriber failed to connect");

            packets = new HashMap<>();
            sub.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(final String channel, final String message) {
                    if(channel.equals("EclipseCore")) {
                        JsonElement element = new Gson().fromJson(message, JsonElement.class);
                        Bukkit.getLogger().info("Attempting to handle packet " + element.getAsJsonObject().get("identifier").getAsString());
                        RedisPacket packet = packets.get(element.getAsJsonObject().get("identifier").getAsString());
                        if(packet == null) {
                            Bukkit.getLogger().log(Level.SEVERE, "Unable to handle redis packet " + element.getAsJsonObject().get(
                                    "identifier").getAsString() + ".");
                            return;
                        }

                        packet.handlePacket(element.getAsJsonObject().get("data").getAsJsonObject());
                    }
                }
            }, "EclipseCore");
            Bukkit.getLogger().info("Subscribed redis packets on channel \"EclipseCore\"");
        });
    }

    public void closeConnection() {
        this.pool.returnResource(sub);
        this.pool.returnResource(pub);
    }

    public void sendPacket(RedisPacket packet) {
        JsonObject object = new JsonObject();
        object.addProperty("identifier", packet.getIdentifier());
        object.add("data", packet.getPacketData());
        Bukkit.getLogger().info("we got this far");

        Bukkit.getScheduler().runTaskAsynchronously(core, () -> pub.publish("EclipseCore", new Gson().toJson(object)));
    }

    public void registerPacket(RedisPacket packet) {
        this.packets.put(packet.getIdentifier(), packet);
    }

    public EclipseCore getCore() {
        return core;
    }

    public Jedis getPub() {
        return pub;
    }
}
