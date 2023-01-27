package it.dani.seniorparkour.utils;


import fr.minuskube.inv.ClickableItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Utils {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static List<String> color(List<String> list){
        return list.stream()
                .map(Utils::color)
                .collect(Collectors.toList());
    }
    public static String convertMillis(long millis){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return(minutes +
                " m " +
                seconds +
                " s");
    }

    public static CompletableFuture<ClickableItem[]> setPlayerHead(Map<ItemStack,UUID> items){
        return CompletableFuture.supplyAsync(() -> {
            ClickableItem[] result = new ClickableItem[items.size()];
            int i = 0;
            for (Map.Entry<ItemStack, UUID> entry : items.entrySet()) {
                ItemStack item = entry.getKey();
                ItemMeta meta = item.getItemMeta();
                if(meta instanceof SkullMeta skull){
                    skull.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getValue()));
                    item.setItemMeta(skull);
                }

                result[i] = ClickableItem.empty(item);
            }

            return result;
        });
    }
}
