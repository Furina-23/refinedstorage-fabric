package com.refinedmods.refinedstorage.render.model;

import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortableGridModelIntegrationTest {
    private static final ResourceLocation MODEL = new ResourceLocation("refinedstorage", "block/portable_grid");

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void replacesBlockAndBothItemModelsDuringLoading() {
        BlockModel placeholder = BlockModel.fromString("{}");

        assertInstanceOf(PortableGridUnbakedGeometry.class,
            FabricModelLoadingPlugin.loadPortableGridModel(placeholder, MODEL));
        assertInstanceOf(PortableGridUnbakedGeometry.class,
            FabricModelLoadingPlugin.loadPortableGridModel(placeholder,
                new ModelResourceLocation("refinedstorage", "portable_grid", "inventory")));
        assertInstanceOf(PortableGridUnbakedGeometry.class,
            FabricModelLoadingPlugin.loadPortableGridModel(placeholder,
                new ModelResourceLocation("refinedstorage", "creative_portable_grid", "inventory")));

        ResourceLocation unrelated = new ResourceLocation("refinedstorage", "block/controller");
        assertSame(placeholder, FabricModelLoadingPlugin.loadPortableGridModel(placeholder, unrelated));
    }

    @Test
    void loadingExposesAndResolvesAllGeometryBeforeBaking() {
        Map<ResourceLocation, BlockModel> models = new HashMap<>();
        BlockModel placeholder = load(MODEL, models);
        UnbakedModel geometry = FabricModelLoadingPlugin.loadPortableGridModel(placeholder, MODEL);

        assertEquals(6, geometry.getDependencies().size());
        ResourceLocation connected = new ResourceLocation("refinedstorage", "block/portable_grid_connected");
        assertTrue(load(connected, models).getElements().isEmpty(),
            "the connected shell inherits its geometry and reproduces the late-loading failure");

        geometry.resolveParents(id -> load(id, models));
        for (ResourceLocation id : geometry.getDependencies()) {
            BlockModel model = load(id, models);
            assertFalse(model.getElements().isEmpty(), id + " must have geometry after parent resolution");
            model.getElements().forEach(element -> element.faces.values().forEach(face -> {
                ResourceLocation texture = model.getMaterial(face.texture).texture();
                assertNotEquals("missingno", texture.getPath(), id + " has an unresolved texture");
                assertNotNull(getClass().getClassLoader().getResource("assets/" + texture.getNamespace()
                    + "/textures/" + texture.getPath() + ".png"), texture.toString());
            }));
        }
    }

    @Test
    void bakesEveryModelAndDirectionBeforeRendering() {
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

        new PortableGridUnbakedGeometry().bake(
            baker, material -> null, BlockModelRotation.X0_Y0, MODEL);

        assertEquals(24, calls.size());
        for (ResourceLocation id : new PortableGridUnbakedGeometry().getDependencies()) {
            List<BakeCall> modelCalls = calls.stream().filter(call -> call.id().equals(id)).toList();
            assertEquals(4, modelCalls.size(), id.toString());
            assertEquals(4, new HashSet<>(modelCalls.stream().map(call -> call.state().getRotation()).toList()).size(),
                id.toString());
        }
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
        var stream = PortableGridModelIntegrationTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull(stream, path);
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private record BakeCall(ResourceLocation id, ModelState state) {
    }
}
