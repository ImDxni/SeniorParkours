package it.dani.seniorparkour.inventories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.utils.ConfigItem;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Getter
public abstract class InventoryBuilder implements InventoryProvider {
    private final ConfigManager manager;
    private final InventoryType type;

    private final ConfigurationSection inventorySection;

    public InventoryBuilder(ConfigManager manager, InventoryType type) {
        this.manager = manager;
        this.type = type;

        inventorySection = manager.getConfig(ConfigType.INVENTORIES).getConfigurationSection(type.getName());
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection emptyItems = inventorySection.getConfigurationSection("static");
        if (emptyItems != null) {
            for (String key : emptyItems.getKeys(false)) {
                ConfigurationSection itemSection = emptyItems.getConfigurationSection(key);

                SlotPos pos = SlotPos.of(itemSection.getInt("slot.x"),itemSection.getInt("slot.y"));
                contents.set(pos, ClickableItem.empty(ConfigItem.fromConfig(itemSection).build()));
            }
        }

        setupItems(player,contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {}

    protected abstract void setupItems(Player player, InventoryContents contents);


    public SmartInventory getInventory(){
        return SmartInventory.builder()
                .title(inventorySection.getString("title"))
                .size(inventorySection.getInt("size"),9)
                .provider(this)
                .build();
    }
}
