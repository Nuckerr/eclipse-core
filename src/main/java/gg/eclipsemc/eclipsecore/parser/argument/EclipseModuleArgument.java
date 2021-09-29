package gg.eclipsemc.eclipsecore.parser.argument;

import cloud.commandframework.arguments.CommandArgument;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.parser.EclipseModuleParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EclipseModuleArgument<C> extends CommandArgument<C, EclipseModule> {

    public EclipseModuleArgument(
            final boolean required,
            @NonNull final String name,
            EclipseCore eclipseCore
    ) {
        super(required, name, new EclipseModuleParser<>(eclipseCore), EclipseModule.class);
    }

}
