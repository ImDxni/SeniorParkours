package it.dani.seniorparkour.inventories.impl;

import fr.minuskube.inv.content.InventoryContents;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import org.bukkit.entity.Player;

public class TopInventory extends InventoryBuilder {

    public TopInventory(ConfigManager manager) {
        super(manager, InventoryType.TOP);
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {

    }
}
