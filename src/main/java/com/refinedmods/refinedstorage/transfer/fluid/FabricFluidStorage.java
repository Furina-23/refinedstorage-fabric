package com.refinedmods.refinedstorage.transfer.fluid;

import com.refinedmods.refinedstorage.inventory.fluid.ProxyFluidHandler;
import com.refinedmods.refinedstorage.transfer.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.List;
import java.util.Iterator;

/** Exposes a fluid interface's directional tanks through Fabric Transfer transactions. */
public final class FabricFluidStorage extends SnapshotParticipant<List<FluidStack>> implements Storage<FluidVariant> {
    private static final long SCALE = FluidConstants.BUCKET / 1000;

    private final FluidTank insertTank;
    private final FluidTank extractTank;

    public FabricFluidStorage(ProxyFluidHandler handler) {
        this.insertTank = handler.getInsertHandler();
        this.extractTank = handler.getExtractHandler();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        int droplets = toDroplets(maxAmount);
        if (droplets == 0) {
            return 0;
        }

        FluidStack stack = toStack(resource, droplets);
        int inserted = insertTank.fill(stack, IFluidHandler.FluidAction.SIMULATE);
        if (inserted > 0) {
            updateSnapshots(transaction);
            inserted = insertTank.fill(toStack(resource, inserted), IFluidHandler.FluidAction.EXECUTE);
        }
        return toFabricUnits(inserted);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        int droplets = toDroplets(maxAmount);
        if (droplets == 0) {
            return 0;
        }

        FluidStack requested = toStack(resource, droplets);
        FluidStack simulated = extractTank.drain(requested, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty()) {
            return 0;
        }

        updateSnapshots(transaction);
        return toFabricUnits(extractTank.drain(simulated, IFluidHandler.FluidAction.EXECUTE).getAmount());
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return List.<StorageView<FluidVariant>>of(new TankView(insertTank, false), new TankView(extractTank, true)).iterator();
    }

    @Override
    protected List<FluidStack> createSnapshot() {
        return List.of(insertTank.getFluid().copy(), extractTank.getFluid().copy());
    }

    @Override
    protected void readSnapshot(List<FluidStack> snapshot) {
        insertTank.setFluid(snapshot.get(0));
        extractTank.setFluid(snapshot.get(1));
    }

    private static FluidStack toStack(FluidVariant variant, int amount) {
        return new FluidStack(variant, amount);
    }

    private static long toFabricUnits(int amount) {
        return (long) amount * SCALE;
    }

    private static int toDroplets(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, amount / SCALE);
    }

    private final class TankView implements StorageView<FluidVariant> {
        private final FluidTank tank;
        private final boolean extractable;

        private TankView(FluidTank tank, boolean extractable) {
            this.tank = tank;
            this.extractable = extractable;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!extractable) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);
                return 0;
            }
            return FabricFluidStorage.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return tank.getFluid().isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            FluidStack fluid = tank.getFluid();
            if (fluid.isEmpty()) {
                return FluidVariant.blank();
            }
            return fluid.hasTag() ? FluidVariant.of(fluid.getFluid(), fluid.getTag()) : FluidVariant.of(fluid.getFluid());
        }

        @Override
        public long getAmount() {
            return toFabricUnits(tank.getFluid().getAmount());
        }

        @Override
        public long getCapacity() {
            return toFabricUnits(tank.getTankCapacity(0));
        }
    }
}
