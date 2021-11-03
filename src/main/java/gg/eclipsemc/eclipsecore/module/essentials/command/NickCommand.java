package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nucker
 */
public class NickCommand {

    @Suggestions("players")
    public List<String> suggestPlayers(final CommandContext<EclipseSender> context, final String input) {
        List<String> res = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> res.add(player.getName()));
        return res;
    }


    @CommandMethod("nick|nickname [playerornick] [nick]")
    @CommandPermission("eclipsecore.essentials.nick")
    public void onCommand(EclipseSender player,
                          @Argument(value = "playerornick", suggestions = "players") String target,
                          @Argument("nick") String nick) {
        if(target == null) {
            if(player instanceof EclipsePlayer p) {
                p.getPlayerData().set("nick", "");
                p.sendMessage(Component.text("You have reset your nickname").color(NamedTextColor.GREEN));
                return;
            }
        }
        Player bukkitTarget = Bukkit.getPlayer(target);
        if(bukkitTarget != null) { // Setting nick for other player
            boolean hasPermission = false;
            if(player instanceof EclipsePlayer sender) hasPermission = sender.getBukkitPlayer().hasPermission("eclipsecore" +
                    ".essentials.nick.other"); else {hasPermission = true;}

            if(!hasPermission) {
                player.sendMessage(Component.text("You dont have permission to set other people's nickname").color(NamedTextColor.RED));
                return;
            }
            EclipsePlayer p = EclipsePlayer.getPlayerFromBukkit(bukkitTarget);
            if(nick == null) {
                p.getPlayerData().set("nick", "");
                player.sendMessage(Component.text("You have reset " + p.getBukkitPlayer().getName() + "'s nickname").color(NamedTextColor.GREEN));
                return;
            }
            if(p.getBukkitPlayer().hasPermission("eclipsecore.essentials.nick.translate")) {
                nick = ChatColor.translateAlternateColorCodes('&', nick);
            }
            p.getPlayerData().set("nick", nick);
            player.sendMessage(Component.text(p.getBukkitPlayer().getName() + "'s nickname has been set too " + nick).color(NamedTextColor.GREEN));
            return;
        }
        // Setting nick for themselves
        if(nick != null) {
            player.sendMessage(Component.text(target + " is not online").color(NamedTextColor.RED));
            return;
        }
        if(player instanceof EclipsePlayer sender) {
            if(target == null) {
                sender.getPlayerData().set("nick", "");
                sender.sendMessage(Component.text("You have reset your nickname").color(NamedTextColor.GREEN));
                return;
            }
            if(sender.getBukkitPlayer().hasPermission("eclipsecore.essentials.nick.translate")) {
                target = ChatColor.translateAlternateColorCodes('&', target);
            }
            sender.getPlayerData().set("nick", target);
            sender.sendMessage(Component.text("Your nickname has been set too " + target).color(NamedTextColor.GREEN));
        }else {
            player.sendMessage(Component.text("You must be in game to run this command").color(NamedTextColor.RED));
        }
    }
}
