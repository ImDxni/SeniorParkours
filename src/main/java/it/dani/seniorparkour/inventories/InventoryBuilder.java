package it.dani.seniorparkour.inventories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.utils.ConfigItem;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class InventoryBuilder implements InventoryProvider {
    private final ConfigManager manager;
    private final InventoryType type;

    private final ConfigurationSection inventorySection;

    private final Set<SlotPos> blackList = new HashSet<>();

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

                SlotPos pos = getPos(itemSection);
                contents.set(pos, ClickableItem.empty(ConfigItem.fromConfig(itemSection).build()));

                blackList.add(pos);
            }
        }

        setupItems(player,contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {}

    protected abstract void setupItems(Player player, InventoryContents contents);

    protected void createPagination(ClickableItem[] items, InventoryContents contents){
        ConfigurationSection paginationSection = inventorySection.getConfigurationSection("pagination");
        if(paginationSection == null)
            return;

        Pagination pagination = contents.pagination();
        pagination.setItems(items);


        pagination.setItemsPerPage(paginationSection.getInt("items-per-page",9));

        ConfigurationSection nextPage = paginationSection.getConfigurationSection("page.next");
        ConfigItem nextPageItem = ConfigItem.fromConfig(nextPage);
        SlotPos nextPagePos = getPos(nextPage);

        contents.set(nextPagePos,ClickableItem.of(nextPageItem.build(),
                (e) -> getInventory().open((Player) e.getWhoClicked(),pagination.next().getPage())));

        ConfigurationSection previousPage = paginationSection.getConfigurationSection("page.previous");
        ConfigItem previousPageItem = ConfigItem.fromConfig(previousPage);
        SlotPos previousPagePos = getPos(previousPage);


        contents.set(previousPagePos,ClickableItem.of(previousPageItem.build(),
                (e) -> getInventory().open((Player) e.getWhoClicked(),pagination.previous().getPage())));


        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL,paginationSection.getInt("start.row"), paginationSection.getInt("start.column"));

        iterator.blacklist(nextPagePos);
        iterator.blacklist(previousPagePos);

        for (SlotPos slotPos : blackList) {
            iterator.blacklist(slotPos);
        }

        pagination.addToIterator(iterator);

    }


    public SmartInventory getInventory(){
        return SmartInventory.builder()
                .title(inventorySection.getString("title"))
                .size(inventorySection.getInt("size"),9)
                .provider(this)
                .build();
    }


    protected SlotPos getPos(ConfigurationSection section){
        return SlotPos.of(section.getInt("slot.row"),section.getInt("slot.column"));
    }
}
