package it.dani.seniorparkour.configuration;

import it.dani.seniorparkour.utils.Utils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public enum Messages {
    NO_PERMISSION("messages.no-permission"),
    COMMAND_NOT_FOUND("messages.command-not-found"),
    PARKOUR_NOT_FOUND("messages.parkour-not-found"),
    INVALID_FORMAT("messages.invalid-format"),
    NO_STATS("messages.no-stats"),
    CHECKPOINT_ADDED("messages.checkpoint-added"),
    CHECKPOINT_REMOVED("messages.checkpoint-removed"),
    CHECKPOINT_NOT_FOUND("messages.checkpoint-not-found"),
    CHECKPOINT_PASSED("messages.checkpoint-passed"),
    NO_CHECKPOINT("messages.no-checkpoint"),
    PARKOUR_CREATED("messages.parkour-created"),
    PARKOUR_STARTED("messages.parkour-started"),
    PARKOUR_FINISHED("messages.parkour-finished"),
    PARKOUR_DELETED("messages.parkour-deleted"),
    PARKOUR_END_CREATED("messages.parkour-end-created"),
    NO_TOP("messages.no-top"),

    HELP("messages.help");




    private final String path;

    public String getMessage(ConfigManager manager){
        return Utils.color(manager.getConfig(ConfigType.MESSAGES).getString(path));
    }

    public String getMessageList(ConfigManager manager){
        StringBuilder builder = new StringBuilder();
        List<String> messages = Utils.color(manager.getConfig(ConfigType.MESSAGES).getStringList(path));

        for (String message : messages) {
            builder.append(message).append("\n");
        }

        return builder.toString();
    }
}
