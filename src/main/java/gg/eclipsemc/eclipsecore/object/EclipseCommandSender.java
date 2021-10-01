package gg.eclipsemc.eclipsecore.object;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class EclipseCommandSender implements EclipseSender{

    CommandSender sender;

    public EclipseCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public void sendMessage(Component component) {
        sender.sendMessage(component);
    }

    public void sendMiniMessage(String string) {

    }

    public CommandSender getSender() {
        return sender;
    }

}
