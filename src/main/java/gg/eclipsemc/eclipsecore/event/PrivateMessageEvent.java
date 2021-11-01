package gg.eclipsemc.eclipsecore.event;

import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nucker
 */
public class PrivateMessageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST;
    static {
        HANDLER_LIST = new HandlerList();
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final EclipsePlayer to, from;
    private final Component message;
    private boolean cancelled;

    public PrivateMessageEvent(EclipsePlayer from, EclipsePlayer to, Component message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.cancelled = false;
    }

    public EclipsePlayer getReceiver() {
        return to;
    }

    public EclipsePlayer getSender() {
        return from;
    }

    public Component getMessage() {
        return message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

}
