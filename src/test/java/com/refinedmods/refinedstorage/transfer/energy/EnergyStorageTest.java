package com.refinedmods.refinedstorage.transfer.energy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyStorageTest {
    @Test
    void respectsCapacityRateLimitsAndSimulation() {
        EnergyStorage storage = new EnergyStorage(100, 30, 20);

        assertEquals(30, storage.receiveEnergy(80, false));
        assertEquals(30, storage.receiveEnergy(80, true));
        assertEquals(30, storage.getEnergyStored());
        assertEquals(20, storage.extractEnergy(80, false));
        assertEquals(10, storage.getEnergyStored());
    }

    @Test
    void disablesDirectionsWhenRateIsZero() {
        EnergyStorage storage = new EnergyStorage(100, 0, 0);

        assertEquals(0, storage.receiveEnergy(10, false));
        assertEquals(0, storage.extractEnergy(10, false));
    }
}
