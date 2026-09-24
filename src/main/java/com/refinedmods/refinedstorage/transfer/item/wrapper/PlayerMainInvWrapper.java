package com.refinedmods.refinedstorage.transfer.item.wrapper;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;

public class PlayerMainInvWrapper extends InvWrapper {
    private final Inventory inventory;

    public PlayerMainInvWrapper(Player player) {
        this(player.getInventory());
    }

    public PlayerMainInvWrapper(Inventory inventory) {
        super(inventory);
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.items.size();
    }
}


