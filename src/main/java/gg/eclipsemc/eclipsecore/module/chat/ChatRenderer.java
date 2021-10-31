package gg.eclipsemc.eclipsecore.module.chat;

import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
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

public class ChatRenderer implements io.papermc.paper.chat.ChatRenderer {

    public static String nameFormat;
    public static String nameHover;

    private Component message;


    @Override
    public @NotNull Component render(
            @NotNull final Player source,
            @NotNull final Component sourceDisplayName,
            @NotNull Component message,
            @NotNull final Audience viewer
    ) {
        EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(source);
        if (this.message == null) {
            String messageString = PlainTextComponentSerializer.plainText().serialize(message);
            if (player.getBukkitPlayer().hasPermission("eclipsecore.chat.markdown")) {
                message = MiniMessage.markdown().parse(messageString);
            }
            if (player.getBukkitPlayer().hasPermission("eclipsecore.chat.minimessagechat")) {
                message = MiniMessage.get().parse(messageString);
            }
            for (final Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                EclipsePlayer player1 = EclipsePlayer.getPlayerFromBukkit(bukkitPlayer);
                Pattern pattern = Pattern.compile("\\b(?=\\w)" + player1.getBukkitPlayer().getName() + "\\b(?<=\\w)",
                        Pattern.MULTILINE);
                //if(!AquaCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId()).isVanished()) {
                    Component hoverComponent = Component.text(player.getBukkitPlayer().getName())
                            .color(NamedTextColor.YELLOW)
                            .hoverEvent(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player.getBukkitPlayer(), nameHover)));
                    message = message.replaceText(TextReplacementConfig.builder()
                            .match(pattern)
                            .replacement(hoverComponent)
                            .build());
                    if (pattern.matcher(PlainTextComponentSerializer.plainText().serialize(message)).find()
                            && player.getPlayerData().getBool("mentionPings"))
                        player.getBukkitPlayer().playSound(
                                Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 2f),
                        Sound.Emitter.self());
                //}
            }
            this.message = Component.text()
                    .append(Component.text(PlaceholderAPI.setPlaceholders(player.getBukkitPlayer(), nameFormat))
                            .clickEvent(ClickEvent.suggestCommand("/msg " + player.getBukkitPlayer().getName() + " "))
                            .hoverEvent(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player.getBukkitPlayer(), nameHover)))
                    )
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(message)
                    .build();
            // TODO: dont send message if ignored
            //if(player.getPlayerData().getListOf("ignoreList", UUID.class).contains(target.getUniqueId)) return; // Or whatever
            // TODO: dont send chat is toggled
            //if(player.getPlayerData().getBool("chatToggled") return; // Or whatever
        }
        return this.message;
    }

}
