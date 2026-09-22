package com.refinedmods.refinedstorage.render.model;

import com.refinedmods.refinedstorage.RS;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverItemBakedModel;
import com.refinedmods.refinedstorage.render.model.baked.PatternBakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class FabricModelLoadingPlugin {
    private static final ResourceLocation PORTABLE_GRID = new ResourceLocation(RS.ID, "block/portable_grid");
    private static final ResourceLocation DISK_DRIVE = new ResourceLocation(RS.ID, "block/disk_drive");
    private static final ResourceLocation DISK_MANIPULATOR = new ResourceLocation(RS.ID, "block/disk_manipulator/loader");

    private FabricModelLoadingPlugin() {
    }

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            context.modifyModelBeforeBake().register((model, modelContext) -> {
                ResourceLocation id = modelContext.id();
                if (PORTABLE_GRID.equals(id) || isPortableGridItem(id)) {
                    return new PortableGridUnbakedGeometry();
                }
                if (DISK_DRIVE.equals(id)) {
                    return new DiskDriveUnbakedGeometry();
                }
                if (DISK_MANIPULATOR.equals(id)) {
                    return new DiskManipulatorUnbakedGeometry();
                }
                return model;
            });
            context.modifyModelAfterBake().register((model, modelContext) -> {
                ResourceLocation id = modelContext.id();
                if (isCoverableBlockModel(id)) {
                    return new CableCoverBakedModel(model);
                }
                if (isInventoryModel(id, "cover")) {
                    return new CableCoverItemBakedModel(ItemStack.EMPTY, CoverType.NORMAL);
                }
                if (isInventoryModel(id, "hollow_cover")) {
                    return new CableCoverItemBakedModel(ItemStack.EMPTY, CoverType.HOLLOW);
                }
                if (isInventoryModel(id, "pattern")) {
                    return new PatternBakedModel(model);
                }
                return model;
            });
        });
    }

    private static boolean isInventoryModel(ResourceLocation id, String path) {
        return id instanceof ModelResourceLocation modelId
            && RS.ID.equals(modelId.getNamespace())
            && path.equals(modelId.getPath())
            && "inventory".equals(modelId.getVariant());
    }

    private static boolean isCoverableBlockModel(ResourceLocation id) {
        if (!(id instanceof ModelResourceLocation modelId) || !RS.ID.equals(modelId.getNamespace())
            || "inventory".equals(modelId.getVariant())) {
            return false;
        }
        return switch (modelId.getPath()) {
            case "cable", "constructor", "destructor", "exporter", "external_storage", "importer" -> true;
            default -> false;
        };
    }

    private static boolean isPortableGridItem(ResourceLocation id) {
        return id instanceof ModelResourceLocation modelId
            && RS.ID.equals(modelId.getNamespace())
            && "portable_grid".equals(modelId.getPath())
            && "inventory".equals(modelId.getVariant());
    }
}


