package it.dani.seniorparkour.inventories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InventoryType {
    INFO("info"),
    TOP("top"),
    STATS("stats");


    private final String name;
}
