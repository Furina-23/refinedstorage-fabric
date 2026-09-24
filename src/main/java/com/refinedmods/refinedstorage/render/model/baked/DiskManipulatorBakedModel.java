package com.refinedmods.refinedstorage.render.model.baked;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.block.DiskManipulatorBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DiskManipulatorBakedModel extends ForwardingBakedModel<BakedModel> implements FabricBakedModel {
    private final BiFunction<Direction, DyeColor, BakedModel> baseConnectedModelBakery;
    private final Function<Direction, BakedModel> baseDisconnectedModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery;

    public DiskManipulatorBakedModel(BakedModel originalModel, BiFunction<Direction, DyeColor, BakedModel> baseConnectedModelBakery, Function<Direction, BakedModel> baseDisconnectedModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery) {
        super(originalModel);
        this.baseConnectedModelBakery = baseConnectedModelBakery;
        this.baseDisconnectedModelBakery = baseDisconnectedModelBakery;
        this.diskModelBakery = diskModelBakery;
        this.diskNearCapacityModelBakery = diskNearCapacityModelBakery;
        this.diskFullModelBakery = diskFullModelBakery;
        this.diskDisconnectedModelBakery = diskDisconnectedModelBakery;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier, RenderContext context) {
        Direction facing = state.getValue(RSBlocks.DISK_MANIPULATOR.get(ColorMap.DEFAULT_COLOR).get().getDirection().getProperty());
        DyeColor color = RSBlocks.DISK_MANIPULATOR.getColorFromObject((DiskManipulatorBlock) state.getBlock());
        BakedModel base = state.getValue(NetworkNodeBlock.CONNECTED)
            ? baseConnectedModelBakery.apply(facing, color)
            : baseDisconnectedModelBakery.apply(facing);
        emit(base, blockView, state, pos, randomSupplier, context);

        if (!(blockView.getBlockEntity(pos) instanceof DiskManipulatorBlockEntity diskManipulator)) {
            return;
        }

        DiskState[] diskStates = diskManipulator.getDiskState();
        int x = 0;
        int y = 0;
        for (int i = 0; i < diskStates.length; ++i) {
            if (diskStates[i] != DiskState.NONE) {
                BakedModel diskModel = getDiskModelBakery(diskStates[i]).apply(facing, getDiskTranslation(x, y));
                emit(diskModel, blockView, state, pos, randomSupplier, context);
            }
            if (++y == 3) {
                y = 0;
                x++;
            }
        }
    }

    private void emit(BakedModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                      Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) model).emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) originalModel).emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (state == null) {
            return super.getQuads(null, side, rand);
        }

        Direction facing = state.getValue(RSBlocks.DISK_MANIPULATOR.get(ColorMap.DEFAULT_COLOR).get().getDirection().getProperty());
        BakedModel base;
        if (state.getValue(NetworkNodeBlock.CONNECTED)) {
            DyeColor color = RSBlocks.DISK_MANIPULATOR.getColorFromObject((DiskManipulatorBlock) state.getBlock());
            base = baseConnectedModelBakery.apply(facing, color);
        } else {
            base = baseDisconnectedModelBakery.apply(facing);
        }
        return base.getQuads(state, side, rand);
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBakery(DiskState diskState) {
        return switch (diskState) {
            case DISCONNECTED -> diskDisconnectedModelBakery;
            case NEAR_CAPACITY -> diskNearCapacityModelBakery;
            case FULL -> diskFullModelBakery;
            default -> diskModelBakery;
        };
    }

    private Vector3f getDiskTranslation(int x, int y) {
        return new Vector3f(
            -((2F + x * 7F) / 16F),
            -((6F + y * 3F) / 16F),
            0
        );
    }

}


