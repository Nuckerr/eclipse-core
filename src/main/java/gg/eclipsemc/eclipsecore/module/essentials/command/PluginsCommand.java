package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class PluginsCommand {

    @ProxiedBy("pl")
    @CommandMethod("plugins")
    @CommandDescription("Shows a list of all plugins with fancy components.")
    @CommandPermission("eclipsecore.essentials.plugins")
    public void onPlugins(CommandSender sender) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        TextComponent.Builder component = Component.text();
        component.append(Component.text("Plugins (" + plugins.length + "): "));
        for (int i = 0; i < plugins.length; i++) {
            Plugin plugin = plugins[i];
            PluginDescriptionFile pluginDescription = plugin.getDescription();
            Component pluginComponent = Component.text(plugin.getName())
                    .color(plugin.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .hoverEvent(Component.text()
                            .append(Component.text(plugin.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.text("Version: ").color(NamedTextColor.GRAY))
                            .append(Component.text(pluginDescription.getVersion()).color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("Author" + (pluginDescription.getAuthors().size() > 1 ? "s" : "") +
                                    ": ").color(NamedTextColor.GRAY))
                            .append(getAuthorComponent(pluginDescription.getAuthors()).color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("Website: ").color(NamedTextColor.GRAY))
                            .append(Component.text(pluginDescription.getWebsite() != null ? pluginDescription.getWebsite() :
                                    "None").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("Naggable: ").color(NamedTextColor.GRAY))
                            .append(Component.text(plugin.isNaggable()).color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("Description: ").color(NamedTextColor.GRAY))
                            .append(Component.text(pluginDescription.getDescription() != null ?
                                    WordUtils.wrap(pluginDescription.getDescription(), 40, "\n", true) : "None.").color(NamedTextColor.DARK_GRAY))
                            .append(Component.newline())
                            .append(Component.newline())
                            .append(Component.text("/about " + plugin.getName()).color(NamedTextColor.YELLOW))
                            .build())
                    .clickEvent(ClickEvent.runCommand("/about " + plugin.getName()));
            component.append(pluginComponent);
            if (i != plugins.length-1)
                component.append(Component.text(", "));
        }
        sender.sendMessage(component.build());
    }

    public Component getAuthorComponent(List<String> authors) {
        TextComponent.Builder component = Component.text();
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).equalsIgnoreCase("SimplyMerlin"))
                //swag tag
                component.append(MiniMessage.get().parse("<rainbow>" + authors.get(i)));
            else
                component.append(Component.text(authors.get(i)));
            if (i != authors.size()-1)
                component.append(Component.text(", "));
        }
        return component.build();
    }

}
