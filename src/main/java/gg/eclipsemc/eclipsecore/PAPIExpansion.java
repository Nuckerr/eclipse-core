package gg.eclipsemc.eclipsecore;

import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nucker
 */
public class PAPIExpansion extends PlaceholderExpansion {

    private final EclipseCore core;
    private final List<Placeholder> placeholders;


    public PAPIExpansion(EclipseCore core) {
        this.core = core;
        this.placeholders = new ArrayList<>();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "EclipseCore";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join("," ,core.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return core.getDescription().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return "KitPvPPlus";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String res = null;
        EclipsePlayer eclipsePlayer = EclipsePlayer.getPlayerFromBukkit(player);
        for (final Placeholder placeholder : this.placeholders) {
            res = placeholder.requestPlaceholder(eclipsePlayer, params);
        }

        return res;
    }


    public abstract static class Placeholder {

        /**
         * @return null if there is no placeholder
         */
        public abstract String requestPlaceholder(EclipsePlayer player, String placeholder);
    }

    public void registerPlaceholders(Placeholder... placeholders) {
        this.placeholders.addAll(Arrays.asList(placeholders));
    }
}
