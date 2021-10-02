package gg.eclipsemc.eclipsecore.object;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gg.eclipsemc.eclipsecore.EclipseCore;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Nucker
 */
public abstract class RedisPacket {

    private final String channel;

    public RedisPacket(String channel) {
        this.channel = channel;
        JavaPlugin.getPlugin(EclipseCore.class).getJedis().subscribe(this.getPubSub(), this.getChannel());
    }

    public abstract void receivePacket(JsonElement message);

    public abstract JsonElement sendPacket();

    public String getChannel() {
        return channel;
    }

    private JedisPubSub getPubSub() {
        return new JedisPubSub() {
            @Override
            public void onMessage(final String channel, final String message) {
                if(channel.equals(RedisPacket.this.getChannel())) {
                    RedisPacket.this.receivePacket(new Gson().fromJson(message, JsonElement.class));

                }
            }
        };
    }

}
