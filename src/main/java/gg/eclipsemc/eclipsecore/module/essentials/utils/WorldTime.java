package gg.eclipsemc.eclipsecore.module.essentials.utils;

/**
 * @author Nucker
 */
public enum WorldTime {

    MORNING(23000),
    DAY(1000),
    SUNSET(12000),
    NIGHT(18000);

    private final long time;

    WorldTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
