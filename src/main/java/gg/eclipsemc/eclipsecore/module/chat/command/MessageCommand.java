package gg.eclipsemc.eclipsecore.module.chat.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gg.eclipsemc.eclipsecore.event.PrivateMessageEvent;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Nucker
 */
public class MessageCommand {

    private final Cache<UUID, UUID> replies;

    public MessageCommand() {
        this.replies = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    }

    @CommandMethod("msg|dm|directmessage|pm|privatemessage|w|whisper|tell|message <target> <message>")
    @CommandDescription("Message other players in private")
    public void onMessageCommand(EclipsePlayer sender,
                                 @Argument("target") EclipsePlayer target,
                                 @Argument("message") @Greedy String message) {
        MessageStatus status = this.sendDM(sender, target, message);
        if(sender.equals(target)) {
            sender.sendMessage(Component.text("You can't message your self, silly").color(NamedTextColor.RED));
            return;
        }

        switch (status) {
            case IGNORED -> sender.sendMessage(Component.text(target.getBukkitPlayer().getName() + " is ignoring private messages").color(NamedTextColor.RED));

            case FAILED_UNKNOWN -> sender.sendMessage(Component.text("Unexpected error occurred while executing the command").color(NamedTextColor.RED));

            case FAILED -> sender.sendMessage(Component.text("Unknown error occurred").color(NamedTextColor.RED));

            // Ignore success as that is all handled in the method
        }
    }

    @CommandMethod("reply|r|respond <message> ")
    @CommandDescription("Reply to the last person you spoke to in the last 5 minutes")
    public void onReply(EclipsePlayer sender, @Argument("message") @Greedy String message) {
        MessageStatus status = this.sendDM(sender, message);

        switch (status) {
            case IGNORED -> sender.sendMessage(Component.text("This player is ignoring private messages").color(NamedTextColor.RED));

            case FAILED -> sender.sendMessage(Component.text("You have not messaged anyone in the last 5 minutes").color(NamedTextColor.RED));

            case FAILED_UNKNOWN -> sender.sendMessage(Component.text("Unexpected error occurred while executing the command").color(NamedTextColor.RED));

            // Ignore success as that is all handled in the method
        }
    }


    private MessageStatus sendDM(EclipsePlayer from, EclipsePlayer to, String message) {
        try {
            if(to.getPlayerData().getListOf("ignoreList", UUID.class).contains(from.getUniqueId())) return MessageStatus.IGNORED;
            if(!to.getPlayerData().getBool("privateMessages")) return MessageStatus.IGNORED;

            Component playerMessage = Component.text(message);
            if (from.getBukkitPlayer().hasPermission("eclipsecore.chat.markdown")) {
                playerMessage = MiniMessage.markdown().parse(message);
            }
            if (from.getBukkitPlayer().hasPermission("eclipsecore.chat.minimessagechat")) {
                playerMessage = MiniMessage.get().parse(message);
            }

            Component componentTo = Component.text("(").color(NamedTextColor.GOLD)
                    .append(Component.text("From " + from.getBukkitPlayer().getName()).color(NamedTextColor.YELLOW))
                    .append(Component.text(")").color(NamedTextColor.GOLD))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(playerMessage.colorIfAbsent(NamedTextColor.YELLOW));

            Component componentFrom = Component.text("(").color(NamedTextColor.GOLD)
                    .append(Component.text("To " + to.getBukkitPlayer().getName()).color(NamedTextColor.YELLOW))
                    .append(Component.text(")").color(NamedTextColor.GOLD))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(playerMessage.colorIfAbsent(NamedTextColor.YELLOW));

            PrivateMessageEvent event = new PrivateMessageEvent(from, to, playerMessage);
            Bukkit.getPluginManager().callEvent(event);


            if(event.isCancelled()) return MessageStatus.FAILED_UNKNOWN;
            to.sendMessage(componentTo);
            if(to.getPlayerData().getBool("privateMessagesPing")) {
                to.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f);
            }
            from.sendMessage(componentFrom);
            this.updateReplies(from, to);
            return MessageStatus.SUCCESS;
        }catch (Exception e) {
            e.printStackTrace();
            return MessageStatus.FAILED_UNKNOWN;
        }
    }

    private MessageStatus sendDM(EclipsePlayer from, String message) {
        try {
            if(this.replies.getIfPresent(from.getUniqueId()) == null) return MessageStatus.FAILED;
            EclipsePlayer to = EclipsePlayer.getPlayerByUUID(replies.getIfPresent(from.getUniqueId()));
            return this.sendDM(from, to, message);
        }catch (Exception e) {
            e.printStackTrace();
            return MessageStatus.FAILED_UNKNOWN;
        }
    }

    private void updateReplies(EclipsePlayer from, EclipsePlayer to) {
        if (replies.getIfPresent(from.getUniqueId()) != null) {
            replies.invalidate(from.getUniqueId());
        }
        replies.put(from.getUniqueId(), to.getUniqueId());
    }

    private enum MessageStatus {
        SUCCESS,
        IGNORED,
        FAILED,
        FAILED_UNKNOWN
    }
}
