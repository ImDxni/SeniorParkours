package it.dani.seniorparkour.inventories.impl;

import fr.minuskube.inv.content.InventoryContents;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import org.bukkit.entity.Player;

public class StatsInventory extends InventoryBuilder {
    public StatsInventory(ConfigManager manager) {
        super(manager, InventoryType.STATS);
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {

    }
}
