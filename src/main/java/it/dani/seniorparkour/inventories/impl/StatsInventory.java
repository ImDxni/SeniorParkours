package it.dani.seniorparkour.inventories.impl;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import it.dani.seniorparkour.utils.ConfigItem;
import it.dani.seniorparkour.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatsInventory extends InventoryBuilder {
    private final Map<RPlayer,Integer> stats;
    public StatsInventory(ConfigManager manager, Map<RPlayer,Integer> stats) {
        super(manager, InventoryType.STATS);
        this.stats = stats;
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = getInventorySection().getConfigurationSection("items.stats");
        if(itemSection == null)
            return;


        ClickableItem[] items = new ClickableItem[stats.size()];

        int i = 0;
        for (Map.Entry<RPlayer, Integer> entry : stats.entrySet()) {
            ConfigItem item = ConfigItem.fromConfig(itemSection);
            RPlayer entity = entry.getKey();
            item.addPlaceHolder("%parkour%",entity.parkour());

            item.addPlaceHolder("%time%", Utils.convertMillis(entity.time()));
            item.addPlaceHolder("%position%",entry.getValue());

            items[i++] = ClickableItem.empty(item.build());
        }

        createPagination(items,contents);
    }
}
