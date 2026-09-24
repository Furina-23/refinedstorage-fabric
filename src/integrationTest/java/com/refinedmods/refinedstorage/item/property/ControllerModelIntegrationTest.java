package com.refinedmods.refinedstorage.item.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.refinedmods.refinedstorage.block.ControllerBlock.EnergyType;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ControllerModelIntegrationTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    static Stream<String> controllers() {
        return Stream.of("light_blue", "white", "orange", "magenta", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black")
            .flatMap(color -> Stream.of("controller", "creative_controller")
                .map(type -> (color.equals("light_blue") ? "" : color + "_") + type));
    }

    @ParameterizedTest
    @MethodSource("controllers")
    void clampedPropertySelectsEveryEnergyStateAndCorrectColor(String item) throws Exception {
        JsonArray overrides = readModel("item/" + item).getAsJsonArray("overrides");
        String color = item.replace("creative_controller", "").replace("controller", "");
        color = color.isEmpty() ? "light_blue" : color.substring(0, color.length() - 1);

        for (EnergyType state : EnergyType.values()) {
            // Use Minecraft's actual clamping interface, as the client registration does.
            ClampedItemPropertyFunction property = (stack, level, entity, seed) ->
                ControllerItemPropertyGetter.toModelValue(state);
            float value = property.call(ItemStack.EMPTY, null, null, 0);
            String selected = null;
            for (JsonElement entry : overrides) {
                JsonObject override = entry.getAsJsonObject();
                float threshold = override.getAsJsonObject("predicate").get("refinedstorage:energy_type").getAsFloat();
                assertTrue(threshold >= 0 && threshold <= 1, item + " has an unreachable threshold");
                if (value >= threshold) {
                    selected = override.get("model").getAsString();
                }
            }
            String expected = "refinedstorage:block/controller/" + (state == EnergyType.ON ? color : state.getSerializedName());
            assertEquals(expected, selected, item + " / " + state);
            JsonObject model = readModel(selected.substring("refinedstorage:".length()));
            for (JsonElement texture : model.getAsJsonObject("textures").asMap().values()) {
                String path = "assets/refinedstorage/textures/" + texture.getAsString().substring("refinedstorage:".length()) + ".png";
                try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
                    assertNotNull(stream, path);
                }
            }
        }
    }

    @Test
    void freshItemReturnsFullStateWithoutBeingClampedToLowEnergy() {
        ControllerItemPropertyGetter getter = new ControllerItemPropertyGetter();
        ClampedItemPropertyFunction registered = getter::call;
        // The no-tag branch is independent of item type; no mod registry is needed.
        assertEquals(1.0F, registered.call(new ItemStack(Items.STONE), null, null, 0));
    }

    private static JsonObject readModel(String model) throws Exception {
        String path = "assets/refinedstorage/models/" + model + ".json";
        try (InputStream stream = ControllerModelIntegrationTest.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(stream, path);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
