package net.minecraftforge.common;

/**
 * Small compatibility implementation used while the Fabric configuration
 * backend is being migrated. Values are immutable defaults for now; the
 * public API mirrors the Forge config types used by the core code so callers
 * remain source compatible.
 */
public final class ForgeConfigSpec {
    public static final class Builder {
        public Builder push(String name) { return this; }
        public Builder pop() { return this; }
        public Builder comment(String comment) { return this; }
        public BooleanValue define(String name, boolean value) { return new BooleanValue(value); }
        public IntValue defineInRange(String name, int value, int min, int max) { return new IntValue(value); }
        public ForgeConfigSpec build() { return new ForgeConfigSpec(); }
    }

    public static class BooleanValue {
        private final boolean value;
        public BooleanValue(boolean value) { this.value = value; }
        public boolean get() { return value; }
    }

    public static class IntValue {
        private final int value;
        public IntValue(int value) { this.value = value; }
        public int get() { return value; }
    }
}
