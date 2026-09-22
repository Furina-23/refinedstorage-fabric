package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class BaseBlockEntity extends BlockEntity {
    private final BlockEntitySynchronizationManager dataManager;
    private boolean unloaded;

    protected BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec syncSpec) {
        super(type, pos, state);
        this.dataManager = new BlockEntitySynchronizationManager(this, syncSpec);
    }

    public BlockEntitySynchronizationManager getDataManager() {
        return dataManager;
    }

    public CompoundTag writeUpdate(CompoundTag tag) {
        return tag;
    }

    public void readUpdate(CompoundTag tag) {
    }

    public void onLoad() {
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        unloaded = false;
    }

    @Override
    public final CompoundTag getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        readUpdate(packet.getTag());
    }

    public void handleUpdateTag(CompoundTag tag) {
        readUpdate(tag);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        // Fabric's server chunk unload callback marks the block entity before Minecraft clears it.
        // Network data must only be deleted when the block itself is removed.
        if (!unloaded) {
            onRemovedNotDueToChunkUnload();
        }
    }

    protected void onRemovedNotDueToChunkUnload() {
        // NO OP
    }

    public void onChunkUnloaded() {
        unloaded = true;
    }

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void setChanged() {
        if (level != null) {
            level.blockEntityChanged(worldPosition);
        }
    }
}


