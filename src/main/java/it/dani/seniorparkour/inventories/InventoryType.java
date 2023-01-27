package it.dani.seniorparkour.inventories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InventoryType {
    INFO("info"),
    INFO_CHECKPOINTS("info.checkpoints"),
    TOP("top"),
    STATS("stats");


    private final String name;
}
