package gg.eclipsemc.eclipsechat.chat;

import io.papermc.paper.chat.ChatRenderer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.plugin.AquaCoreAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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
            @NotNull Component message,
            @NotNull final Audience viewer
    ) {
        if (this.message == null) {
            String messageString = PlainTextComponentSerializer.plainText().serialize(message);
            if (source.hasPermission("eclipsechat.markdown")) {
                message = MiniMessage.markdown().parse(messageString);
            }
            if (source.hasPermission("eclipsechat.minimessagechat")) {
                message = MiniMessage.get().parse(messageString);
            }
            for (final Player player : Bukkit.getOnlinePlayers()) {
                PlayerData data = AquaCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
                if(!data.getMessageSystem().isChatMention())
                    continue;
                Component hoverComponent = Component.text(" " + player.getName() + " ")
                        .color(NamedTextColor.YELLOW)
                        .hoverEvent(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(source, nameHover)));
                message = message.replaceText(TextReplacementConfig.builder()
                        .match(" " + player.getName() + " ")
                        .replacement(hoverComponent)
                        .build());
                if (message.contains(hoverComponent))
                    player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 2f), Sound.Emitter.self());
            }
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
