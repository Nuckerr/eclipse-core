package gg.eclipsemc.eclipsecore.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCommandContextKeys;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author Nucker
 */
public final class EclipsePlayerParser<C> implements ArgumentParser<C, EclipsePlayer> {

    @Override
    public @NonNull ArgumentParseResult<EclipsePlayer> parse(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull Queue<@NonNull String> inputQueue
    ) {
        final String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    PlayerArgument.PlayerParser.class,
                    commandContext
            ));
        }
        inputQueue.remove();

        Player player = Bukkit.getPlayer(input);

        if (player == null) {
            return ArgumentParseResult.failure(new PlayerArgument.PlayerParseException(input, commandContext));
        }

        return ArgumentParseResult.success(EclipsePlayer.getPlayerFromBukkit(player));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull String input
    ) {
        List<String> output = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            final CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
            if (bukkit instanceof Player && !((Player) bukkit).canSee(player)) {
                continue;
            }
            output.add(player.getName());
        }

        return output;
    }

}
