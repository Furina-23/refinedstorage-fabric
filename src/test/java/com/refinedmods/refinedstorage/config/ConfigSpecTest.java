package com.refinedmods.refinedstorage.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigSpecTest {
    @Test
    void loadsNestedValuesAndPersistsThem() throws Exception {
        ConfigSpec.Builder builder = new ConfigSpec.Builder();
        builder.push("storage");
        ConfigSpec.IntValue capacity = builder.defineInRange("capacity", 10, 0, 100);
        ConfigSpec.BooleanValue enabled = builder.define("enabled", true);
        builder.pop();
        ConfigSpec spec = builder.build();
        Path file = Files.createTempFile("refined-storage-config", ".json");
        Files.writeString(file, "{\"storage\":{\"capacity\":42,\"enabled\":false}}");

        spec.load(file);

        assertEquals(42, capacity.get());
        assertFalse(enabled.get());
        String persisted = Files.readString(file);
        assertTrue(persisted.contains("\"capacity\": 42"));
        assertTrue(persisted.contains("\"enabled\": false"));

        enabled.set(true);
        spec.save();

        assertTrue(Files.readString(file).contains("\"enabled\": true"));
        Files.deleteIfExists(file);
    }

    @Test
    void fallsBackToDefaultsForOutOfRangeValues() throws Exception {
        ConfigSpec.Builder builder = new ConfigSpec.Builder();
        builder.push("storage");
        ConfigSpec.IntValue capacity = builder.defineInRange("capacity", 10, 0, 100);
        builder.pop();
        ConfigSpec spec = builder.build();
        Path file = Files.createTempFile("refined-storage-config", ".json");
        Files.writeString(file, "{\"storage\":{\"capacity\":101}}");

        spec.load(file);

        assertEquals(10, capacity.get());
        Files.deleteIfExists(file);
    }
}
