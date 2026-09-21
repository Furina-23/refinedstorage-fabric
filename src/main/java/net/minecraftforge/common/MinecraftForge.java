package net.minecraftforge.common;

public final class MinecraftForge {
    public static final EventBus EVENT_BUS = new EventBus();
    public static final class EventBus {
        public void register(Object listener) { }
        public void addListener(Object listener) { }
        public boolean post(Object event) { return false; }
    }
}
