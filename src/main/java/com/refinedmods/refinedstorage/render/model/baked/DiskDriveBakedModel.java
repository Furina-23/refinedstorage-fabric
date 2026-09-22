package com.refinedmods.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.blockentity.DiskDriveBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DiskDriveBakedModel extends ForwardingBakedModel<BakedModel> implements FabricBakedModel {
    private final Function<Direction, BakedModel> baseModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery;

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.getValue(RSBlocks.DISK_DRIVE.get().getDirection().getProperty());

            List<BakedQuad> quads = new ArrayList<>();
            if (key.side != null) {
                quads = baseModelBakery.apply(facing).getQuads(key.state, key.side, key.random);
                return quads;
            }

            int x = 0;
            int y = 0;
            for (int i = 0; i < 8; ++i) {
                if (key.diskState[i] != DiskState.NONE) {
                    BakedModel diskModel = getDiskModelBakery(key.diskState[i]).apply(facing, getDiskTranslation(x, y));
                    quads.addAll(diskModel.getQuads(key.state, key.side, key.random));
                }

                x++;
                if ((i + 1) % 2 == 0) {
                    y++;
                    x = 0;
                }
            }

            return quads;
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
            Vector3f translation = new Vector3f();
            translation.add(((2F / 16F) + ((float) x * 7F) / 16F) * -1, 0, 0); // Add to X
            translation.add(0, -((2F / 16F) + ((float) y * 3F) / 16F), 0); // Remove from Y
            return translation;
        }
    });

    public DiskDriveBakedModel(BakedModel base, Function<Direction, BakedModel> baseModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery) {
        super(base);
        this.baseModelBakery = baseModelBakery;
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
        Direction facing = state.getValue(RSBlocks.DISK_DRIVE.get().getDirection().getProperty());
        emit(baseModelBakery.apply(facing), blockView, state, pos, randomSupplier, context);

        if (!(blockView.getBlockEntity(pos) instanceof DiskDriveBlockEntity diskDrive)) {
            return;
        }

        DiskState[] diskStates = diskDrive.getDiskState();
        int x = 0;
        int y = 0;
        for (int i = 0; i < diskStates.length; ++i) {
            if (diskStates[i] != DiskState.NONE) {
                BakedModel diskModel = getDiskModelBakery(diskStates[i]).apply(facing, getDiskTranslation(x, y));
                emit(diskModel, blockView, state, pos, randomSupplier, context);
            }
            if (++x == 2) {
                x = 0;
                y++;
            }
        }
    }

    private void emit(BakedModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                      Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) model).emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) baseModelBakery.apply(Direction.NORTH)).emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return super.getQuads(state, side, rand);
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
            -((2F + y * 3F) / 16F),
            0
        );
    }

    private static class CacheKey {
        private final BlockState state;
        private final Direction side;
        private final DiskState[] diskState;
        private final RandomSource random;

        CacheKey(BlockState state, @Nullable Direction side, DiskState[] diskState, RandomSource random) {
            this.state = state;
            this.side = side;
            this.diskState = Arrays.copyOf(diskState, diskState.length);
            this.random = random;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            if (!state.equals(cacheKey.state)) {
                return false;
            }

            if (side != cacheKey.side) {
                return false;
            }

            return Arrays.equals(diskState, cacheKey.diskState);
        }

        @Override
        public int hashCode() {
            int result = state.hashCode();
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(diskState);
            return result;
        }
    }
}


