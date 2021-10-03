package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    @CommandMethod("gamemode creative")
    @ProxiedBy("gmc")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemodeCreative(EclipsePlayer player) {
        setGamemode(player.getBukkitPlayer(), GameMode.CREATIVE);
    }

    @CommandMethod("gamemode adventure")
    @ProxiedBy("gma")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemodeAdventure(EclipsePlayer player) {
        setGamemode(player.getBukkitPlayer(), GameMode.ADVENTURE);
    }

    @CommandMethod("gamemode survival")
    @ProxiedBy("gms")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemodeSurvival(EclipsePlayer player) {
        setGamemode(player.getBukkitPlayer(), GameMode.SURVIVAL);
    }

    @CommandMethod("gamemode spectator")
    @ProxiedBy("gmsp")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemodeSpectator(EclipsePlayer player) {
        setGamemode(player.getBukkitPlayer(), GameMode.SPECTATOR);
    }

    public void setGamemode(Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
        player.sendMessage(Component.text("Your gamemode has been set to " + gameMode.toString().toLowerCase()).color(NamedTextColor.GREEN));
    }

}
