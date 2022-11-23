package org.abos.fabricmc.creeperspores;

public enum CreeperGrief {
    VANILLA, CHARGED, NEVER;

    public boolean shouldGrief(boolean charged) {
        return switch (this) {
            case NEVER -> false;
            case CHARGED -> charged;
            case VANILLA -> true;
        };
    }
}