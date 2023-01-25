package it.dani.seniorparkour.utils;


import org.bukkit.ChatColor;

import java.util.List;
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

}
