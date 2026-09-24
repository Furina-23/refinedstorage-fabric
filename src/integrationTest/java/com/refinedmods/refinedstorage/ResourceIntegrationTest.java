package com.refinedmods.refinedstorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceIntegrationTest {
    @Test
    void fabricMetadataAndCoreRecipeArePackagedTogether() throws IOException {
        String metadata = read("fabric.mod.json");
        String recipe = read("data/refinedstorage/recipes/1k_storage_disk.json");

        assertTrue(metadata.contains("\"id\": \"refinedstorage\""));
        assertTrue(metadata.contains("com.refinedmods.refinedstorage.fabric.RefinedStorageFabric"));
        assertTrue(metadata.contains("\"minecraft\": \"~1.20.1\""));
        assertTrue(recipe.contains("\"result\""));
        assertTrue(recipe.contains("refinedstorage:1k_storage_disk"));
    }

    private static String read(String resource) throws IOException {
        try (InputStream stream = ResourceIntegrationTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException("Missing packaged resource: " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
