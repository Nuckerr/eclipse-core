package gg.eclipsemc.eclipsecore.module.essentials;

import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.PAPIExpansion;
import gg.eclipsemc.eclipsecore.manager.PlayerDataManager;
import gg.eclipsemc.eclipsecore.module.essentials.command.CraftAnvilCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.FeedRepairHealCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.FlyCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.GamemodeCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.GiveCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.NickCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.PluginsCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.SettingsCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.SocialsCommand;
import gg.eclipsemc.eclipsecore.module.essentials.command.TimeCommand;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Nucker
 */
public class EssentialsModule extends EclipseModule {


    public EssentialsModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "EssentialsModule";
    }

    @Override
    public String getConfigName() {
        return "essentials.yml";
    }

    @Override
    protected void onEnable() {
        registerCommand(new FlyCommand());
        registerCommand(new PluginsCommand());
        registerCommand(new CraftAnvilCommand());
        registerCommand(new FeedRepairHealCommand());
        registerCommand(new GamemodeCommand());
        registerCommand(new TimeCommand());
        registerCommand(new SettingsCommand(eclipseCore));
        registerCommand(new SocialsCommand(this));
        registerCommand(new GiveCommand());
        registerCommand(new NickCommand());

        eclipseCore.getPlaceholderAPIExpansion().registerPlaceholders(new PAPIExpansion.Placeholder() {
            @Override
            public String requestPlaceholder(final EclipsePlayer player, final String placeholder) {
                switch (placeholder) {
                    case "is_flying":
                        return String.valueOf(player.getBukkitPlayer().isFlying());
                    case "player_nick":
                        if(!player.getPlayerData().getString("nick").equals("")) {
                            return "~" + player.getPlayerData().getString("nick");
                        }
                        return player.getBukkitPlayer().getDisplayName();
                    case "player_color":
                        if(!player.getPlayerData().getString("color").equals("")) {
                            return player.getPlayerData().getString("color");
                        }
                        String suffix =
                                eclipseCore.getLuckPerms().getPlayerAdapter(Player.class).getMetaData(player.getBukkitPlayer()).getSuffix();
                        return Objects.requireNonNullElse(suffix, "&7");
                }
                return null;
            }
        });
        super.onEnable();

        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("privateMessages") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return true;
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<String>("nick") {
            @Override
            public String parseData(final UUID uuid) {
                return "";
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("privateMessagesPing") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return true;
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<Boolean>("autoBroadcasts") {
            @Override
            public Boolean parseData(final UUID uuid) {
                return true;
            }
        });
        eclipseCore.getPlayerDataManager().addDefault(new PlayerDataManager.DefaultData<String>("color") {
            @Override
            public String parseData(final UUID uuid) {
                return "";
            }
        });
    }

}
