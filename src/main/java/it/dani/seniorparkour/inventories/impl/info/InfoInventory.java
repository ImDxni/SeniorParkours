package it.dani.seniorparkour.inventories.impl.info;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.inventories.InventoryBuilder;
import it.dani.seniorparkour.inventories.InventoryType;
import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.utils.ConfigItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InfoInventory extends InventoryBuilder {
    private final Parkour parkour;
    public InfoInventory(ConfigManager manager, Parkour parkour) {
        super(manager, InventoryType.INFO);
        this.parkour = parkour;
    }

    @Override
    protected void setupItems(Player player, InventoryContents contents) {
        ConfigurationSection startSection = getInventorySection().getConfigurationSection("items.start");
        if(startSection != null){
            SlotPos pos = getPos(startSection);

            ConfigItem item = ConfigItem.fromConfig(startSection);

            contents.set(pos, ClickableItem.of(item.build(), (e) -> {
                player.closeInventory();
                player.teleport(parkour.getStart());
            }));
        }


        ConfigurationSection endSection = getInventorySection().getConfigurationSection("items.end");

        if(endSection != null){
            SlotPos pos = getPos(endSection);

            ConfigItem item = ConfigItem.fromConfig(endSection);

            contents.set(pos, ClickableItem.of(item.build(), (e) -> {
                player.closeInventory();
                player.teleport(parkour.getEnd());
            }));
        }

        ConfigurationSection checkPointSection = getInventorySection().getConfigurationSection("items.checkpoint");

        if(checkPointSection != null){
            SlotPos pos = getPos(checkPointSection);

            ConfigItem item = ConfigItem.fromConfig(checkPointSection);

            contents.set(pos, ClickableItem.of(item.build(), (e) -> {
                new CheckpointsInventory(getManager(),parkour).getInventory().open(player);
            }));
        }

    }
}
