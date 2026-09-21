package net.minecraftforge.fml.event.lifecycle;

public final class FMLCommonSetupEvent {
    public void enqueueWork(Runnable work) { work.run(); }
}
