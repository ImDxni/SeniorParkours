package it.dani.seniorparkour.inventories.impl;

import fr.minuskube.inv.content.InventoryContents;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import it.dani.seniorparkour.utils.ConfigItem;
import it.dani.seniorparkour.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TopInventory extends InventoryBuilder {
    private final List<RPlayer> records;
    public TopInventory(ConfigManager manager, List<RPlayer> records) {
        super(manager, InventoryType.TOP);

        this.records = records;
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = getInventorySection().getConfigurationSection("items.top");
        if(itemSection == null)
            return;

        ConfigItem configItem = ConfigItem.fromConfig(itemSection);

        Map<ItemStack, UUID> players = new HashMap<>();

        for (int i = 0; i < records.size(); i++) {
            RPlayer record = records.get(i);

            configItem.addPlaceHolder("%player%",record.username())
                    .addPlaceHolder("%time%", Utils.convertMillis(record.time()))
                    .addPlaceHolder("%position%",i+1);

            players.put(configItem.build(),record.uuid());
        }

        Utils.setPlayerHead(players).thenAccept((items) -> Bukkit.getScheduler().runTask(getManager().getPlugin(), () -> createPagination(items,contents)));


    }
}
