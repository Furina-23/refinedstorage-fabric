package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;

public abstract class BaseBlock extends Block {
    protected BaseBlock(Properties properties) {
        super(properties);
    }

    public BlockDirection getDirection() {
        return BlockDirection.NONE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            Direction newDirection = dir.cycle(state.getValue(dir.getProperty()));

            return state.setValue(dir.getProperty(), newDirection);
        }

        return super.rotate(state, rot);
    }

    protected void onDirectionChanged(Level level, BlockPos pos, Direction newDirection) {
        // NO OP
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !(placer instanceof Player player)) {
            return;
        }

        INetworkNode placed = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(pos));
        if (placed == null) {
            return;
        }
        placed.setOwner(player.getGameProfile().getId());
        for (Direction direction : Direction.values()) {
            INetworkNode neighbor = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(pos.relative(direction)));
            if (neighbor != null && neighbor.getNetwork() != null) {
                neighbor.getNetwork().getNodeGraph().invalidate(
                    Action.PERFORM, neighbor.getNetwork().getLevel(), neighbor.getNetwork().getPosition()
                );
                break;
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        checkIfDirectionHasChanged(state, level, pos, newState);
    }

    protected void checkIfDirectionHasChanged(BlockState state, Level level, BlockPos pos, BlockState newState) {
        if (getDirection() != BlockDirection.NONE &&
            state.getBlock() == newState.getBlock() &&
            state.getValue(getDirection().getProperty()) != newState.getValue(getDirection().getProperty())) {
            onDirectionChanged(level, pos, newState.getValue(getDirection().getProperty()));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            builder.add(dir.getProperty());
        }
    }
}
