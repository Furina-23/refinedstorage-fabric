package net.minecraftforge.items.wrapper;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;

public class PlayerMainInvWrapper extends InvWrapper {
    public PlayerMainInvWrapper(Player player) { super(player.getInventory()); }
    public PlayerMainInvWrapper(Inventory inventory) { super(inventory); }
}
