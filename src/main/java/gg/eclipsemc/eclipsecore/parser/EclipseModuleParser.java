package gg.eclipsemc.eclipsecore.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class EclipseModuleParser<C> implements ArgumentParser<C, EclipseModule> {

    EclipseCore eclipseCore;

    public EclipseModuleParser(EclipseCore eclipseCore) {
        this.eclipseCore = eclipseCore;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull EclipseModule> parse(
            @NonNull final CommandContext<@NonNull C> commandContext,
            @NonNull final Queue<@NonNull String> inputQueue
    ) {
        final String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    EclipseModuleParser.class,
                    commandContext
            ));
        }
        for (final EclipseModule module : eclipseCore.getModules()) {
            if (module.getName().equalsIgnoreCase(input)) {
                inputQueue.remove();
                return ArgumentParseResult.success(module);
            }
        }
        return ArgumentParseResult.failure(new EclipseModuleParseException(input, commandContext));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(
            @NonNull final CommandContext<C> commandContext,
            @NonNull final String input
    ) {
        final List<String> completions = new ArrayList<>();
        for (final EclipseModule module : eclipseCore.modules) {
            completions.add(module.getName());
        }
        return completions;
    }

    public static final class EclipseModuleParseException extends ParserException {

        private final String input;

        /**
         * Construct a new UUID parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public EclipseModuleParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    EclipseModuleParser.class,
                    context,
                    Caption.of("argument.parse.failure.module"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public String getInput() {
            return this.input;
        }

    }

}
