package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.DestructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.refinedmods.refinedstorage.transfer.FluidStack;
import com.refinedmods.refinedstorage.transfer.FluidType;
import com.refinedmods.refinedstorage.transfer.fluid.IFluidHandler;
import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import com.refinedmods.refinedstorage.transfer.item.IItemHandlerModifiable;

import java.util.List;

public class DestructorNetworkNode extends NetworkNode implements IComparable, IWhitelistBlacklist, IType, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "destructor");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private final BaseItemHandler itemFilters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));
    private final CoverManager coverManager;
    private int compare = IComparer.COMPARE_NBT;
    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.SILK_TOUCH, UpgradeItem.Type.FORTUNE_1, UpgradeItem.Type.FORTUNE_2, UpgradeItem.Type.FORTUNE_3)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> tool = createTool());
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;

    public DestructorNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
        this.coverManager = new CoverManager(this);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getDestructor().getUsage() + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0 && level.isLoaded(pos)) {
            if (type == IType.ITEMS) {
                if (pickupItem) {
                    pickupItems();
                } else {
                    breakBlock();
                }
            } else if (type == IType.FLUIDS) {
                breakFluid();
            }
        }
    }

    private ItemStack tool = createTool();

    private void pickupItems() {
        BlockPos front = pos.relative(getDirection());
        if (!level.isLoaded(front)) {
            return;
        }

        List<ItemEntity> droppedItems = level.getEntitiesOfClass(ItemEntity.class, new AABB(front));

        for (ItemEntity entity : droppedItems) {

            if (entity.isRemoved()) {
                continue;
            }

            ItemStack droppedItem = entity.getItem();

            if (IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, droppedItem) &&
                network.insertItem(droppedItem, droppedItem.getCount(), Action.SIMULATE).isEmpty()) {
                network.insertItemTracked(droppedItem.copy(), droppedItem.getCount());

                entity.remove(Entity.RemovalReason.DISCARDED);

                break;
            }
        }
    }

    private void breakBlock() {
        BlockPos front = pos.relative(getDirection());
        if (!level.isLoaded(front)) {
            return;
        }
        BlockState frontBlockState = level.getBlockState(front);
        Block frontBlock = frontBlockState.getBlock();
        ItemStack frontStack = frontBlock.getCloneItemStack(level, front, frontBlockState);
        BlockEntity frontBlockEntity = level.getBlockEntity(front);
        ServerPlayer fakePlayer = LevelUtils.getFakePlayer((ServerLevel) level, getOwner());

        if (!frontStack.isEmpty() &&
            IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, frontStack) &&
            frontBlockState.getDestroySpeed(level, front) != -1.0 &&
            PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, fakePlayer, front, frontBlockState, frontBlockEntity)) {
            List<ItemStack> drops = Block.getDrops(
                frontBlockState,
                (ServerLevel) level,
                front,
                frontBlockEntity,
                fakePlayer,
                tool
            );

            for (ItemStack drop : drops) {
                if (!network.insertItem(drop, drop.getCount(), Action.SIMULATE).isEmpty()) {
                    return;
                }
            }

            frontBlock.playerWillDestroy(level, front, frontBlockState, fakePlayer);
            level.removeBlock(front, false);
            PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, fakePlayer, front, frontBlockState, frontBlockEntity);

            for (ItemStack drop : drops) {
                // Removing a network node can detach this destructor from its controller.
                if (network == null) {
                    Containers.dropItemStack(level, front.getX(), front.getY(), front.getZ(), drop);
                } else {
                    network.insertItemTracked(drop, drop.getCount());
                }
            }
        }
    }

    private void breakFluid() {
        BlockPos front = pos.relative(getDirection());
        if (!level.isLoaded(front)) {
            return;
        }

        BlockState frontBlockState = level.getBlockState(front);
        Block frontBlock = frontBlockState.getBlock();

        if (frontBlock instanceof LiquidBlock) {
            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
            if (frontBlockState.getValue(LiquidBlock.LEVEL) == 0) {
                Fluid fluid = frontBlockState.getFluidState().getType();

                FluidStack stack = new FluidStack(fluid, FluidType.BUCKET_VOLUME);

                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack) &&
                    network.insertFluid(stack, stack.getAmount(), Action.SIMULATE).isEmpty()) {
                    network.insertFluidTracked(stack, stack.getAmount());

                    level.setBlock(front, Blocks.AIR.defaultBlockState(), 11);
                }
            }
        }
    }

    private ItemStack createTool() {
        ItemStack newTool = new ItemStack(Items.DIAMOND_PICKAXE);

        if (upgrades.hasUpgrade(UpgradeItem.Type.SILK_TOUCH)) {
            newTool.enchant(Enchantments.SILK_TOUCH, 1);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_3)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_2)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 2);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_1)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 1);
        }

        return newTool;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(CoverManager.NBT_COVER_MANAGER)) {
            this.coverManager.readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));
        }

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.coverManager.writeToNbt());

        StackUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        tag.putBoolean(NBT_PICKUP, pickupItem);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        if (tag.contains(NBT_PICKUP)) {
            pickupItem = tag.getBoolean(NBT_PICKUP);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return getUpgrades();
    }

    @Override
    public int getType() {
        return level.isClientSide ? DestructorBlockEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    public boolean isPickupItem() {
        return pickupItem;
    }

    public void setPickupItem(boolean pickupItem) {
        this.pickupItem = pickupItem;
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }


}
