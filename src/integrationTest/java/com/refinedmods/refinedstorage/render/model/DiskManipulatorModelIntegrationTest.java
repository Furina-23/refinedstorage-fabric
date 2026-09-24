package com.refinedmods.refinedstorage.render.model;

import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DiskManipulatorModelIntegrationTest {
    private static final ResourceLocation LOADER = new ResourceLocation("refinedstorage", "block/disk_manipulator/loader");

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void loadingExposesAndResolvesAllGeometryBeforeBaking() {
        Map<ResourceLocation, BlockModel> models = new HashMap<>();
        BlockModel placeholder = load(LOADER, models);
        assertTrue(placeholder.getElements().isEmpty());
        UnbakedModel geometry = FabricModelLoadingPlugin.loadDiskManipulatorModel(placeholder, LOADER);
        assertInstanceOf(DiskManipulatorUnbakedGeometry.class, geometry);
        assertEquals(21, geometry.getDependencies().size()); // 16 colors, disconnected shell, four disk states

        ResourceLocation disconnected = new ResourceLocation("refinedstorage", "block/disk_manipulator/disconnected");
        // Reproduce the previous late-loading failure: this shell inherits ALL its faces.
        assertTrue(load(disconnected, models).getElements().isEmpty());
        geometry.resolveParents(id -> load(id, models));

        for (ResourceLocation id : geometry.getDependencies()) {
            BlockModel model = load(id, models);
            assertFalse(model.getElements().isEmpty(), id + " must have geometry after parent resolution");
            model.getElements().forEach(element -> element.faces.values().forEach(face -> {
                var texture = model.getMaterial(face.texture).texture();
                assertNotEquals("missingno", texture.getPath(), id + " has an unresolved texture");
                assertNotNull(getClass().getClassLoader().getResource("assets/" + texture.getNamespace()
                    + "/textures/" + texture.getPath() + ".png"), texture.toString());
            }));
        }
    }

    @Test
    void bakesEveryDirectionAndColorBeforeRendering() {
        List<BakeCall> calls = new ArrayList<>();
        BakedModel emptyModel = new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
                return List.of();
            }

            @Override public boolean useAmbientOcclusion() { return false; }
            @Override public boolean isGui3d() { return true; }
            @Override public boolean usesBlockLight() { return true; }
            @Override public boolean isCustomRenderer() { return false; }
            @Override public TextureAtlasSprite getParticleIcon() { return null; }
            @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
        };
        ModelBaker baker = new ModelBaker() {
            @Override
            public UnbakedModel getModel(ResourceLocation id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public BakedModel bake(ResourceLocation id, ModelState state) {
                calls.add(new BakeCall(id, state));
                return emptyModel;
            }
        };

        new DiskManipulatorUnbakedGeometry().bake(baker, material -> null, BlockModelRotation.X0_Y0, LOADER);

        assertEquals(69, calls.size()); // Original + four disconnected + 16 colors in four directions.
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation id = new ResourceLocation("refinedstorage", "block/disk_manipulator/" + color.getName());
            List<BakeCall> colorCalls = calls.stream().filter(call -> call.id().equals(id)).toList();
            assertEquals(4, colorCalls.size(), id.toString());
            assertEquals(4, new HashSet<>(colorCalls.stream().map(call -> call.state().getRotation()).toList()).size(), id.toString());
        }
    }

    @ParameterizedTest
    @EnumSource(DyeColor.class)
    void allColorsUseDynamicBlockLoaderButKeepStaticItemModel(DyeColor color) throws Exception {
        String name = (color == DyeColor.LIGHT_BLUE ? "" : color.getName() + "_") + "disk_manipulator";
        try (var reader = reader("assets/refinedstorage/blockstates/" + name + ".json")) {
            var root = JsonParser.parseReader(reader).getAsJsonObject();
            String id = root.getAsJsonObject("variants").getAsJsonObject("").get("model").getAsString();
            assertEquals(LOADER, new ResourceLocation(id));
        }
        Map<ResourceLocation, BlockModel> models = new HashMap<>();
        ResourceLocation itemId = new ResourceLocation("refinedstorage", "item/" + name);
        BlockModel item = load(itemId, models);
        assertSame(item, FabricModelLoadingPlugin.loadDiskManipulatorModel(item, itemId));
        item.resolveParents(id -> load(id, models));
        assertFalse(item.getElements().isEmpty());
        assertEquals("block/disk_manipulator/cutouts/" + color.getName(), item.getMaterial("#cutout").texture().getPath());
    }

    private static BlockModel load(ResourceLocation id, Map<ResourceLocation, BlockModel> models) {
        return models.computeIfAbsent(id, key -> {
            try (var reader = reader("assets/" + key.getNamespace() + "/models/" + key.getPath() + ".json")) {
                return BlockModel.fromStream(reader);
            } catch (Exception e) {
                throw new AssertionError("Unable to load " + key, e);
            }
        });
    }

    private static InputStreamReader reader(String path) {
        var stream = DiskManipulatorModelIntegrationTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull(stream, path);
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private record BakeCall(ResourceLocation id, ModelState state) {
    }
}
