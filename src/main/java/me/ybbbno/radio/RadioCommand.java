package me.ybbbno.radio;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.deadybbb.ybmj.LegacyTextHandler;
import me.ybbbno.radio.item.RadioItemManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class RadioCommand implements BasicCommand {
    private final RadioItemManager item;

    public RadioCommand(RadioItemManager item) {
        this.item = item;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player s = (Player) source.getSender();

        if (!args[0].equalsIgnoreCase("give")) {
            LegacyTextHandler.sendFormattedMessage(s, "<red>Используйте /radio give");
            return;
        }

        s.getInventory().addItem(item.getItem());
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of("give");
    }

    @Override
    public boolean canUse(CommandSender sender) {
        final String permission = this.permission();
        return sender.hasPermission(permission) &&
                sender instanceof Player;
    }

    @Override
    public @Nullable String permission() {
        return "radio.use";
    }
}
