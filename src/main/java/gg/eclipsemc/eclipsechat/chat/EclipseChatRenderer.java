package gg.eclipsemc.eclipsechat.chat;

import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EclipseChatRenderer implements ChatRenderer {

    public static String nameFormat;
    public static String nameHover;

    Component message;

    @Override
    public @NotNull Component render(
            @NotNull final Player source,
            @NotNull final Component sourceDisplayName,
            @NotNull final Component message,
            @NotNull final Audience viewer
    ) {
        if (this.message == null) {
            this.message = Component.text()
                    .append(Component.text(PlaceholderAPI.setPlaceholders(source, nameFormat))
                            .clickEvent(ClickEvent.suggestCommand("/msg " + source.getName() + " "))
                            .hoverEvent(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(source, nameHover)))
                    )
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(message)
                    .build();
        }
        return this.message;
    }

}
