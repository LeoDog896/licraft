package com.leodog896.licraft.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

public class GamesCommand extends Command {
    public GamesCommand() {
        super("games");

        setCondition(Conditions::playerOnly);

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, "Games");

            inventory.addViewer(player);
        });
    }
}
