package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.PortableGridBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PortableGridUnbakedGeometry implements UnbakedModel {
    private static final ResourceLocation BASE_CONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_connected");
    private static final ResourceLocation BASE_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_disconnected");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_near_capacity");
    private static final Collection<ResourceLocation> DEPENDENCIES = List.of(
        BASE_CONNECTED_MODEL,
        BASE_DISCONNECTED_MODEL,
        DISK_MODEL,
        DISK_DISCONNECTED_MODEL,
        DISK_FULL_MODEL,
        DISK_NEAR_CAPACITY_MODEL
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
        Function<Direction, BakedModel> baseConnectedModels = bakeModels(BASE_CONNECTED_MODEL, modelState, baker);
        return new PortableGridBakedModel(
            Objects.requireNonNull(baseConnectedModels.apply(Direction.NORTH)),
            baseConnectedModels,
            bakeModels(BASE_DISCONNECTED_MODEL, modelState, baker),
            bakeModels(DISK_MODEL, modelState, baker),
            bakeModels(DISK_NEAR_CAPACITY_MODEL, modelState, baker),
            bakeModels(DISK_FULL_MODEL, modelState, baker),
            bakeModels(DISK_DISCONNECTED_MODEL, modelState, baker)
        );
    }

    private Function<Direction, BakedModel> bakeModels(final ResourceLocation id,
                                                       final ModelState state,
                                                       final ModelBaker baker) {
        Map<Direction, BakedModel> models = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new ModelState() {
                @Override
                public Transformation getRotation() {
                    return rotation.compose(state.getRotation());
                }

                @Override
                public boolean isUvLocked() {
                    return state.isUvLocked();
                }
            };
            models.put(direction, Objects.requireNonNull(baker.bake(id, wrappedState)));
        }
        return models::get;
    }
}


