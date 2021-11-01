package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandMethod;
import gg.eclipsemc.eclipsecore.module.essentials.EssentialsModule;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * @author Nucker
 */
public record SocialsCommand(EssentialsModule module) {

    @CommandMethod("discord")
    public void onDiscord(EclipseSender sender) {
        sender.sendMessage(this.sendMessage("Discord", "socials.discord"));
    }

    @CommandMethod("store")
    public void onStore(EclipseSender sender) {
        sender.sendMessage(this.sendMessage("Store", "socials.store"));
    }

    @CommandMethod("twitter")
    public void onTwitter(EclipseSender sender) {
        sender.sendMessage(this.sendMessage("Twitter", "socials.twitter"));
    }

    private TextComponent sendMessage(String prefix, String pathToLink) {
        return Component.text(prefix + ": ").color(NamedTextColor.GOLD).append(MiniMessage.get().parse(module.getConfig().getString(pathToLink)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,
                        MiniMessage.get().stripTokens(module.getConfig().getString(pathToLink)))); // TODO: fix link clicking not working
    }
}
