package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * @author Nucker (IKEW, nucker created the class)
 */
public class GamemodeCommand {
    @ProxiedBy("gamemode")
    @CommandMethod("gamemode")
    @CommandDescription("Set your gamemode")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGamemode(Player sender, String gm) {
        Component message = Component.text("Your gamemode has been set to ").color(NamedTextColor.GREEN);
        gm = gm.toLowerCase();
        switch (gm){
            case "c":
            case "creative":
                sender.setGameMode(GameMode.CREATIVE);
                message.append(Component.text("creative")).color(NamedTextColor.GREEN);
                break;
            case "s":
            case "survival":
                sender.setGameMode(GameMode.SURVIVAL);
                message.append(Component.text("survival")).color(NamedTextColor.GREEN);
                break;
            case "sp":
            case "spectator":
                sender.setGameMode(GameMode.SPECTATOR);
                message.append(Component.text("spectator")).color(NamedTextColor.GREEN);
                break;
            case "a":
            case "adventure":
                sender.setGameMode(GameMode.ADVENTURE);
                message.append(Component.text("adventure")).color(NamedTextColor.GREEN);
                break;
            default:
                sender.setGameMode(GameMode.CREATIVE);
                message.append(Component.text("creative")).color(NamedTextColor.GREEN);
                break;
        }
        sender.sendMessage(message);
    }

    @ProxiedBy("gmc")
    @CommandMethod("gmc")
    @CommandDescription("Set your gamemode to creative")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGmc(Player sender){
        sender.setGameMode(GameMode.CREATIVE);
        sender.sendMessage(Component.text("Your gamemode has been set to creative").color(NamedTextColor.GREEN));
    }

    @ProxiedBy("gms")
    @CommandMethod("gms")
    @CommandDescription("Set your gamemode to survival")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGms(Player sender){
        sender.setGameMode(GameMode.SURVIVAL);
        sender.sendMessage(Component.text("Your gamemode has been set to survival").color(NamedTextColor.GREEN));
    }

    @ProxiedBy("gmsp")
    @CommandMethod("gmsp")
    @CommandDescription("Set your gamemode to survival")
    @CommandPermission("eclipsecore.essentials.gamemode")
    public void onGmsp(Player sender){
        sender.setGameMode(GameMode.SPECTATOR);
        sender.sendMessage(Component.text("Your gamemode has been set to spectator").color(NamedTextColor.GREEN));
    }
}
