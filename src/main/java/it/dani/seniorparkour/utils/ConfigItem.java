package it.dani.seniorparkour.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static it.dani.seniorparkour.utils.Utils.color;

public class ConfigItem {
    private Material material;
    private String name;
    private List<String> lore;
    private int amount;


    public ConfigItem setMaterial(Material material) {
        this.material = material;
        return this;
    }


    public ConfigItem setName(String name) {
        this.name = color(name);
        return this;
    }


    public ConfigItem setLore(List<String> lore) {
        this.lore = color(lore);
        return this;
    }

    public ConfigItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ConfigItem addPlaceHolder(String placeholder, Object value) {
        name = name.replaceAll(placeholder, value.toString());

        List<String> newLore = new ArrayList<>();

        if(value instanceof List){
            int i;
            for (i = 0; i < lore.size(); i++) {
                if(lore.get(i).contains(placeholder)){
                    lore.set(i,lore.get(i).replace(placeholder,""));
                    lore.addAll(i, (Collection<? extends String>) value);
                    break;
                }
            }
        }else {
            lore.forEach((s) -> newLore.add(s.replace(placeholder, value.toString())));
            lore = newLore;
        }

        return this;
    }

    public static ConfigItem fromConfig(ConfigurationSection section){
        return new ConfigItem().setMaterial(Material.valueOf(section.getString("material")))
                .setAmount(section.contains("amount") ? section.getInt("amount") : 1)
                .setName(section.getString("name"))
                .setLore(section.getStringList("lore"));
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (name != null)
            itemMeta.setDisplayName(name);

        if (lore != null)
            itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
