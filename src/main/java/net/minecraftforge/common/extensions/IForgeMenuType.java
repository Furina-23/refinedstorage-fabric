package net.minecraftforge.common.extensions;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;

public final class IForgeMenuType {
    private IForgeMenuType() { }
    public static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return new MenuType<>((id, inventory) -> factory.create(id, inventory, new FriendlyByteBuf(Unpooled.buffer())), FeatureFlags.VANILLA_SET);
    }
}
