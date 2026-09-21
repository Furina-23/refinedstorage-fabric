package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerEvent {
    public static class HarvestCheck extends PlayerEvent {
        private final Player player;
        private final BlockState target;
        private boolean canHarvest;
        public HarvestCheck(Player player, BlockState target, boolean canHarvest) { this.player = player; this.target = target; this.canHarvest = canHarvest; }
        public BlockState getTargetBlock() { return target; }
        public void setCanHarvest(boolean value) { canHarvest = value; }
    }
}
