package com.refinedmods.refinedstorage.transfer.item.wrapper;

import net.minecraft.world.entity.player.Player;

public class PlayerInvWrapper extends InvWrapper {
    public PlayerInvWrapper(Player player) { super(player.getInventory()); }
}


