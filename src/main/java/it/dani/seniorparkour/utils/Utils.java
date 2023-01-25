package it.dani.seniorparkour.utils;


import java.util.concurrent.TimeUnit;

public class Utils {

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
