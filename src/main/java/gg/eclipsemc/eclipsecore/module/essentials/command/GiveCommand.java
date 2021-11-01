package gg.eclipsemc.eclipsecore.module.essentials.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.ItemStackArgument;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import gg.eclipsemc.eclipsecore.object.EclipseSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Range;

/**
 * @author Nucker
 */
public class GiveCommand {

    @CommandMethod("give <player> <item> [amount]")
    @CommandDescription("Give items to another player")
    @CommandPermission("eclipsecore.essentials.give.other")
    public void onGiveOther(EclipseSender sender,
                            @NonNull @Argument("player") EclipsePlayer target,
                            @NonNull @Argument("item") Material mat,
                            @Argument("amount") @Range(from = 1, to = 64) Integer amount) {
        ItemStack item = new ItemStack(mat);
        amount = amount == null ? 1 : amount;
        item.setAmount(amount);
        target.getBukkitPlayer().getInventory().addItem(item);
        sender.sendMessage(Component.text("You have given " + target.getBukkitPlayer().getName() + " " + amount + " " + item.getType().name().toLowerCase() + "(s)")
                .color(NamedTextColor.GREEN));
    }
}
