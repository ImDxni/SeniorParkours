package it.dani.seniorparkour.utils;


import fr.minuskube.inv.ClickableItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    public static String color(String s){
        s = translateHexCodes(s);
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
                "m " +
                seconds +
                "s");
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

    private static String translateHexCodes(String textToTranslate) {

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder buffer = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }
}
