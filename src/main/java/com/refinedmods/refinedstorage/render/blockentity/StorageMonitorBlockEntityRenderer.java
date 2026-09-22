package com.refinedmods.refinedstorage.render.blockentity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.StorageMonitorBlock;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import com.refinedmods.refinedstorage.render.FabricFluidRendering;
import com.refinedmods.refinedstorage.transfer.FluidStack;
import org.joml.Vector3f;

public class StorageMonitorBlockEntityRenderer implements BlockEntityRenderer<StorageMonitorBlockEntity> {
    private static org.joml.Quaternionf rotationXYZ(Vector3f rotation, boolean degrees) {
        float factor = degrees ? (float) (Math.PI / 180.0) : 1.0F;
        return new org.joml.Quaternionf().rotationXYZ(
            rotation.x() * factor,
            rotation.y() * factor,
            rotation.z() * factor
        );
    }

    @Override
    public void render(StorageMonitorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int i, int i1) {
        Direction direction = Direction.NORTH;

        BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() instanceof StorageMonitorBlock) {
            direction = state.getValue(RSBlocks.STORAGE_MONITOR.get().getDirection().getProperty());
        }

        final int light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().offset(direction.getNormal()));
        final float rotation = (float) (Math.PI * (360 - direction.getOpposite().get2DDataValue() * 90) / 180d);

        final int type = blockEntity.getStackType();

        final ItemStack itemStack = blockEntity.getItemStack();
        final FluidStack fluidStack = blockEntity.getFluidStack();

        if (type == IType.ITEMS && itemStack != null && !itemStack.isEmpty()) {
            renderItem(poseStack, renderTypeBuffer, direction, rotation, light, itemStack);

            String amount = API.instance().getQuantityFormatter().formatWithUnits(blockEntity.getAmount());

            renderText(poseStack, renderTypeBuffer, direction, rotation, light, amount);
        } else if (type == IType.FLUIDS && fluidStack != null && !fluidStack.isEmpty()) {
            renderFluid(poseStack, renderTypeBuffer, direction, rotation, light, fluidStack);

            String amount = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(blockEntity.getAmount());

            renderText(poseStack, renderTypeBuffer, direction, rotation, light, amount);
        }
    }

    private void renderText(PoseStack poseStack, MultiBufferSource renderTypeBuffer, Direction direction, float rotation, int light, String amount) {
        poseStack.pushPose();

        float stringOffset = -(Minecraft.getInstance().font.width(amount) * 0.01F) / 2F;

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate(
            ((float) direction.getStepX() * 0.501F) + (direction.getStepZ() * stringOffset),
            -0.275,
            ((float) direction.getStepZ() * 0.501F) - (direction.getStepX() * stringOffset)
        );

        poseStack.mulPose(rotationXYZ(new Vector3f(direction.getStepX() * 180, 0, direction.getStepZ() * 180), true));
        poseStack.mulPose(new org.joml.Quaternionf().rotationY(rotation));

        poseStack.scale(0.01F, 0.01F, 0.01F);

        Minecraft.getInstance().font.drawInBatch(
            amount,
            0,
            0,
            -1,
            false,
            poseStack.last().pose(),
            renderTypeBuffer,
            Font.DisplayMode.NORMAL,
            0,
            light
        );

        poseStack.popPose();
    }

    private void renderItem(PoseStack poseStack, MultiBufferSource renderTypeBuffer, Direction direction, float rotation, int light, ItemStack itemStack) {
        poseStack.pushPose();

        // Put it in the middle, outwards, and facing the correct direction
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate((float) direction.getStepX() * 0.501F, 0, (float) direction.getStepZ() * 0.501F);
        poseStack.mulPose(rotationXYZ(new Vector3f(0, rotation, 0), false));

        // Make it look "flat"
        poseStack.scale(0.5F, -0.5F, -0.00005f);

        // Fix rotation after making it look flat
        poseStack.mulPose(rotationXYZ(new Vector3f(0, 0, 180), true));

        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);
        boolean render3D = itemModel.isGui3d();

        if (render3D) {
            Lighting.setupFor3DItems();
        } else {
            Lighting.setupForFlatItems();
        }

        Minecraft.getInstance().getItemRenderer().render(
            itemStack,
            ItemDisplayContext.GUI,
            false,
            poseStack,
            renderTypeBuffer,
            light,
            OverlayTexture.NO_OVERLAY,
            itemModel
        );

        poseStack.popPose();
    }

    private void renderFluid(PoseStack poseStack, MultiBufferSource renderTypeBuffer, Direction direction, float rotation, int light, FluidStack fluidStack) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate((float) direction.getStepX() * 0.51F, 0.5F, (float) direction.getStepZ() * 0.51F);
        poseStack.mulPose(rotationXYZ(new Vector3f(0, rotation, 0), false));

        poseStack.scale(0.5F, 0.5F, 0.5F);

        final Fluid fluid = fluidStack.getFluid();
        final TextureAtlasSprite sprite = FabricFluidRendering.getStillSprite(fluid);
        final int fluidColor = FabricFluidRendering.getColor(fluid);

        final VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.text(sprite.atlasLocation()));

        final int colorRed = fluidColor >> 16 & 0xFF;
        final int colorGreen = fluidColor >> 8 & 0xFF;
        final int colorBlue = fluidColor & 0xFF;
        final int colorAlpha = fluidColor >> 24 & 0xFF;

        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0F)
            .color(colorRed, colorGreen, colorBlue, colorAlpha)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .endVertex();
        buffer.vertex(poseStack.last().pose(), 0.5F, -0.5F, 0F)
            .color(colorRed, colorGreen, colorBlue, colorAlpha)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .endVertex();
        buffer.vertex(poseStack.last().pose(), 0.5F, -1.5F, 0F)
            .color(colorRed, colorGreen, colorBlue, colorAlpha)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5F, -1.5F, 0F)
            .color(colorRed, colorGreen, colorBlue, colorAlpha)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .endVertex();

        poseStack.popPose();
    }
}


