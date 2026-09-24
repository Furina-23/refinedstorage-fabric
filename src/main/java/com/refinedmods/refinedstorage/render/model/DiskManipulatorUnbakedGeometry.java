package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskManipulatorBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskManipulatorUnbakedGeometry implements UnbakedModel {
    private static final ResourceLocation BASE_MODEL_DISCONNECTED = new ResourceLocation(RS.ID, "block/disk_manipulator/disconnected");

    private final Map<DyeColor, ResourceLocation> BASE_MODEL_CONNECTED = new HashMap<>();
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    public DiskManipulatorUnbakedGeometry() {
        for (DyeColor value : DyeColor.values()) {
            BASE_MODEL_CONNECTED.put(value, new ResourceLocation(RS.ID, "block/disk_manipulator/" + value.getName()));
        }
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        var dependencies = new java.util.ArrayList<>(List.of(
            BASE_MODEL_DISCONNECTED, DISK_MODEL, DISK_DISCONNECTED_MODEL, DISK_FULL_MODEL, DISK_NEAR_CAPACITY_MODEL
        ));
        dependencies.addAll(BASE_MODEL_CONNECTED.values());
        return dependencies;
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter) {
        getDependencies().forEach(id -> modelGetter.apply(id).resolveParents(modelGetter));
    }

    @Override
    public BakedModel bake(final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ResourceLocation modelLocation) {
        return new DiskManipulatorBakedModel(
            Objects.requireNonNull(baker.bake(BASE_MODEL_DISCONNECTED, modelState)),
            getBaseModelBakerConnected(modelState, baker),
            getBaseModelBaker(modelState, baker),
            getDiskModelBaker(DISK_MODEL, modelState, baker),
            getDiskModelBaker(DISK_NEAR_CAPACITY_MODEL, modelState, baker),
            getDiskModelBaker(DISK_FULL_MODEL, modelState, baker),
            getDiskModelBaker(DISK_DISCONNECTED_MODEL, modelState, baker)
        );
    }

    private Function<Direction, BakedModel> getBaseModelBaker(final ModelState state,
                                                              final ModelBaker baker) {
        Map<Direction, BakedModel> models = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            models.put(direction, baker.bake(BASE_MODEL_DISCONNECTED, transformedState(state, rotation)));
        }
        return models::get;
    }

    private BiFunction<Direction, DyeColor, BakedModel> getBaseModelBakerConnected(final ModelState state,
                                                                                   final ModelBaker baker) {
        Map<Direction, Map<DyeColor, BakedModel>> models = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Map<DyeColor, BakedModel> modelsByColor = new EnumMap<>(DyeColor.class);
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            for (DyeColor color : DyeColor.values()) {
                modelsByColor.put(color, baker.bake(BASE_MODEL_CONNECTED.get(color), transformedState(state, rotation)));
            }
            models.put(direction, modelsByColor);
        }
        return (direction, color) -> models.get(direction).get(color);
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBaker(final ResourceLocation id,
                                                                          final ModelState state,
                                                                          final ModelBaker baker) {
        return (direction, trans) -> {
            final Transformation translation = new Transformation(trans, null, null, null);
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            return baker.bake(id, transformedState(state, rotation.compose(translation)));
        };
    }

    private ModelState transformedState(ModelState state, Transformation transformation) {
        return new ModelState() {
            @Override
            public Transformation getRotation() {
                return transformation.compose(state.getRotation());
            }

            @Override
            public boolean isUvLocked() {
                return state.isUvLocked();
            }
        };
    }
}


