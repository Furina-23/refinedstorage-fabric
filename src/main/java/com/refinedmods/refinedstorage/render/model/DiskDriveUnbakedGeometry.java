package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskDriveBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskDriveUnbakedGeometry implements UnbakedModel {
    private static final ResourceLocation BASE_MODEL = new ResourceLocation(RS.ID, "block/disk_drive_base");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    private static final Collection<ResourceLocation> DEPENDENCIES = List.of(
        BASE_MODEL, DISK_MODEL, DISK_DISCONNECTED_MODEL, DISK_FULL_MODEL, DISK_NEAR_CAPACITY_MODEL
    );

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter) {
        DEPENDENCIES.forEach(id -> modelGetter.apply(id).resolveParents(modelGetter));
    }

    @Override
    public BakedModel bake(final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ResourceLocation modelLocation) {
        return new DiskDriveBakedModel(
            Objects.requireNonNull(baker.bake(BASE_MODEL, modelState)),
            getBaseModelBaker(modelState, baker),
            getDiskModelBaker(DISK_MODEL, modelState, baker),
            getDiskModelBaker(DISK_NEAR_CAPACITY_MODEL, modelState, baker),
            getDiskModelBaker(DISK_FULL_MODEL, modelState, baker),
            getDiskModelBaker(DISK_DISCONNECTED_MODEL, modelState, baker)
        );
    }

    private Function<Direction, BakedModel> getBaseModelBaker(final ModelState state,
                                                              final ModelBaker baker) {
        return direction -> baker.bake(BASE_MODEL, state);
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBaker(final ResourceLocation id,
                                                                          final ModelState state,
                                                                          final ModelBaker baker) {
        return (direction, trans) -> {
            final Transformation translation = new Transformation(trans, null, null, null);
            return baker.bake(id, transformedState(state, translation));
        };
    }

    private ModelState transformedState(ModelState state, Transformation transformation) {
        return new ModelState() {
            @Override
            public Transformation getRotation() {
                return state.getRotation().compose(transformation);
            }

            @Override
            public boolean isUvLocked() {
                return state.isUvLocked();
            }
        };
    }
}


