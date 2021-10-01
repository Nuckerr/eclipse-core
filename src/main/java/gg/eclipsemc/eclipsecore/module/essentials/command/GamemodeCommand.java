package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand {
    
    @ProxiedBy("gamemode")
    @CommandMethod("gamemode")
    @CommandDescription("Set your gamemode")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemode(EclipsePlayer sender, String gm) {
        Component message = Component.text("Your gamemode has been set to ").color(NamedTextColor.GREEN);
        gm = gm.toLowerCase();
        switch (gm){
            case "c":
            case "creative":
                sender.getBukkitPlayer().setGameMode(GameMode.CREATIVE);
                message.append(Component.text("creative")).color(NamedTextColor.GREEN);
                break;
            case "s":
            case "survival":
                sender.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                message.append(Component.text("survival")).color(NamedTextColor.GREEN);
                break;
            case "sp":
            case "spectator":
                sender.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
                message.append(Component.text("spectator")).color(NamedTextColor.GREEN);
                break;
            case "a":
            case "adventure":
                sender.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
                message.append(Component.text("adventure")).color(NamedTextColor.GREEN);
                break;
            default:
                sender.getBukkitPlayer().setGameMode(GameMode.CREATIVE);
                message.append(Component.text("creative")).color(NamedTextColor.GREEN);
                break;
        }
        sender.sendMessage(message);
    }

    @ProxiedBy("gmc")
    @CommandMethod("gmc")
    @CommandDescription("Set your gamemode to creative")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGmc(EclipsePlayer sender){
        sender.getBukkitPlayer().setGameMode(GameMode.CREATIVE);
        sender.sendMessage(Component.text("Your gamemode has been set to creative").color(NamedTextColor.GREEN));
    }

    @ProxiedBy("gms")
    @CommandMethod("gms")
    @CommandDescription("Set your gamemode to survival")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGms(EclipsePlayer sender){
        sender.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
        sender.sendMessage(Component.text("Your gamemode has been set to survival").color(NamedTextColor.GREEN));
    }

    @ProxiedBy("gmsp")
    @CommandMethod("gmsp")
    @CommandDescription("Set your gamemode to survival")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGmsp(EclipsePlayer sender){
        sender.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        sender.sendMessage(Component.text("Your gamemode has been set to spectator").color(NamedTextColor.GREEN));
    }
}
