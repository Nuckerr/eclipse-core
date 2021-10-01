package gg.eclipsemc.eclipsecore.manager;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.Allocation;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import gg.eclipsemc.eclipsecore.EclipseCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.logging.Level;

/**
 * @author Nucker
 */
public class PterodactylManager {

    private static boolean restarting = false;

    private final EclipseCore core;

    private final PteroClient client;
    private final PteroApplication application;
    private ApplicationServer apiServer;
    private final ClientServer clientServer;

    public PterodactylManager(EclipseCore core) {
        this.core = core;

        this.client = PteroBuilder.createClient(core.getConfig().getString("pterodactyl.url"), core.getConfig().getString(
                "pterodactyl.token"));
        this.application = PteroBuilder.createApplication(core.getConfig().getString("pterodactyl.url"), core.getConfig().getString(
                "pterodactyl.client"));
        if(client == null || application == null) {
            core.getLogger().log(Level.SEVERE, "Unable to connect to pterodactyl panel. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(core);
        }
        System.out.println(application);

        int port = Bukkit.getPort();
        for (final ApplicationServer server : application.retrieveServers().execute()) {
            if(server.getAllocations().isPresent()) {
                for (final Allocation allocation : server.getAllocations().get()) {
                    if(Integer.parseInt(allocation.getPort()) == port)
                        this.apiServer = server;
                }
            }
        }

        this.clientServer = client.retrieveServerByIdentifier(apiServer.getIdentifier()).execute();

        if(apiServer == null || clientServer == null) {
            core.getLogger().log(Level.SEVERE, "Unable to find the server this is running on. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(core);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onConnect(AsyncPlayerPreLoginEvent event) {
                if(restarting) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            Component.text("Server is restarting").color(NamedTextColor.RED));
                }
            }
        }, core);
    }


    public ClientServer getClientServer() {
        return clientServer;
    }

    public ApplicationServer getApiServer() {
        return apiServer;
    }

    public EclipseCore getCore() {
        return core;
    }

    public PteroApplication getApplication() {
        return application;
    }

    public PteroClient getClient() {
        return client;
    }

    public void rebootServer() {
        if(restarting) return;
        restarting = true;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.kick(Component.text("Server is restarting").color(NamedTextColor.RED));
        }
        this.clientServer.restart().execute();
    }

}
