package it.dani.seniorparkour.inventories.impl.info;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.utils.ConfigItem;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CheckpointsInventory extends InventoryBuilder {
    private final Parkour parkour;
    public CheckpointsInventory(ConfigManager manager,Parkour parkour) {
        super(manager, InventoryType.INFO_CHECKPOINTS);
        this.parkour = parkour;
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = getInventorySection().getConfigurationSection("items.checkpoint");
        if(itemSection == null)
            return;



        ClickableItem[] items = new ClickableItem[parkour.getCheckPoints().size()];


        for (int i = 0; i < parkour.getCheckPoints().size(); i++) {
            ConfigItem item = ConfigItem.fromConfig(itemSection);
            item.addPlaceHolder("%index%",(i+1));
            Location checkpoint = parkour.getCheckPoints().get(i);

            items[i] = ClickableItem.of(item.build(), (e) -> {
                player.closeInventory();
                player.teleport(checkpoint);
            });
        }

        createPagination(items,contents);
    }
}
