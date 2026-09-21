package net.minecraftforge.event;

import net.minecraft.server.level.ServerLevel;

public class TickEvent {
    public enum Phase { START, END }
    public static class LevelTickEvent extends TickEvent {
        public final ServerLevel level;
        public final Phase phase;
        public LevelTickEvent(ServerLevel level, Phase phase) { this.level = level; this.phase = phase; }
    }
}
