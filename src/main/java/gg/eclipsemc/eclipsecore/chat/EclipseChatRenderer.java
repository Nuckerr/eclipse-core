package gg.eclipsemc.eclipsecore.chat;

import io.papermc.paper.chat.ChatRenderer;
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

import java.util.regex.Pattern;

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
            if (source.hasPermission("eclipsecore.chat.markdown")) {
                message = MiniMessage.markdown().parse(messageString);
            }
            if (source.hasPermission("eclipsecore.chat.minimessagechat")) {
                message = MiniMessage.get().parse(messageString);
            }
            /*
            for (final Player player : Bukkit.getOnlinePlayers()) {
                Pattern pattern = Pattern.compile("\\b(?=\\w)" + player.getName() + "\\b(?<=\\w)", Pattern.MULTILINE);
                if(!AquaCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId()).isVanished()) {
                    Component hoverComponent = Component.text(player.getName())
                            .color(NamedTextColor.YELLOW)
                            .hoverEvent(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, nameHover)));
                    message = message.replaceText(TextReplacementConfig.builder()
                            .match(pattern)
                            .replacement(hoverComponent)
                            .build());
                    if (pattern.matcher(PlainTextComponentSerializer.plainText().serialize(message)).find()
                            && AquaCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId()).getMessageSystem().isChatMention())
                        player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 2f), Sound.Emitter.self());
                }
            }
             */
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
