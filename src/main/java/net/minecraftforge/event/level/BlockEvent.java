package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEvent {
    private final LevelAccessor level;
    private final BlockPos pos;
    private boolean canceled;
    public BlockEvent(LevelAccessor level, BlockPos pos) { this.level = level; this.pos = pos; }
    public LevelAccessor getLevel() { return level; }
    public BlockPos getPos() { return pos; }
    public void setCanceled(boolean value) { canceled = value; }
    public boolean isCanceled() { return canceled; }
    public static class EntityPlaceEvent extends BlockEvent {
        private final Player entity;
        public EntityPlaceEvent(LevelAccessor level, BlockPos pos, Player entity) { super(level, pos); this.entity = entity; }
        public Player getEntity() { return entity; }
        public BlockSnapshot getBlockSnapshot() { return new BlockSnapshot(getPos()); }
    }
    public static class BreakEvent extends BlockEvent {
        private final Player player;
        public BreakEvent(LevelAccessor level, BlockPos pos, BlockState state, Player player) { super(level, pos); this.player = player; }
        public Player getPlayer() { return player; }
    }
    public static class BlockSnapshot {
        private final BlockPos pos;
        public BlockSnapshot(BlockPos pos) { this.pos = pos; }
        public BlockPos getPos() { return pos; }
    }
}
