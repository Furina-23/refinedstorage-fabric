package com.refinedmods.refinedstorage.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;

/**
 * Quantity-bearing adapter around Fabric Transfer's FluidVariant.
 */
public final class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

    private final FluidVariant variant;
    private int amount;

    public FluidStack(Fluid fluid, int amount) {
        this(FluidVariant.of(fluid), amount);
    }

    public FluidStack(FluidVariant variant, int amount) {
        this.variant = variant;
        this.amount = Math.max(0, amount);
    }

    public static FluidStack loadFluidStackFromNBT(CompoundTag tag) {
        if (tag.contains("id")) {
            return new FluidStack(FluidVariant.fromNbt(tag), tag.getInt("Amount"));
        }

        if (tag.contains("FluidName")) {
            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(tag.getString("FluidName")));
            CompoundTag fluidTag = tag.contains("Tag") ? tag.getCompound("Tag") : null;
            return new FluidStack(fluidTag == null ? FluidVariant.of(fluid) : FluidVariant.of(fluid, fluidTag), tag.getInt("Amount"));
        }

        return EMPTY;
    }

    public static FluidStack readFromPacket(FriendlyByteBuf buf) {
        return new FluidStack(FluidVariant.fromPacket(buf), buf.readVarInt());
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        tag.merge(variant.toNbt());
        tag.putInt("Amount", amount);
        return tag;
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        variant.toPacket(buf);
        buf.writeVarInt(amount);
    }

    public Fluid getFluid() {
        return variant.getFluid();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    public void grow(int amount) {
        setAmount(this.amount + amount);
    }

    public void shrink(int amount) {
        setAmount(this.amount - amount);
    }

    public FluidStack split(int amount) {
        int splitAmount = Math.min(this.amount, Math.max(0, amount));
        this.amount -= splitAmount;
        return new FluidStack(variant, splitAmount);
    }

    public FluidStack copy() {
        return new FluidStack(variant, amount);
    }

    public boolean isEmpty() {
        return variant.isBlank() || amount <= 0;
    }

    public boolean isFluidEqual(@Nonnull FluidStack other) {
        return variant.isOf(other.getFluid()) && variant.nbtMatches(other.variant.getNbt());
    }

    public static boolean areFluidStackTagsEqual(@Nonnull FluidStack left, @Nonnull FluidStack right) {
        return left.variant.nbtMatches(right.variant.getNbt());
    }

    public CompoundTag getTag() {
        return variant.hasNbt() ? variant.copyNbt() : null;
    }

    public boolean hasTag() {
        return variant.hasNbt();
    }

    public Component getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    public String getTranslationKey() {
        return getFluid().getDescriptionId();
    }

    @Override
    public String toString() {
        return "FluidStack[" + BuiltInRegistries.FLUID.getKey(getFluid()) + " x " + amount + "]";
    }
}
