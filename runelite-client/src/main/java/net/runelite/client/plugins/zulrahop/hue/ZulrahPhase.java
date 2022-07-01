package net.runelite.client.plugins.zulrahop.hue;

import java.awt.*;

public class ZulrahPhase {

    private ZulrahPhaseType type;
    private Point point;

    public ZulrahPhase(ZulrahPhaseType type, Point point) {
        this.type = type;
        this.point = point;
    }

    public static ZulrahPhase asRanged(Point point) {
        return new ZulrahPhase(ZulrahPhaseType.RANGE, point);
    }

    public static ZulrahPhase asRangedJad(Point point) {
        return new ZulrahPhase(ZulrahPhaseType.RANGE_JAD, point);
    }

    public static ZulrahPhase asMageJad(Point point) {
        return new ZulrahPhase(ZulrahPhaseType.MAGE_JAD, point);
    }

    public static ZulrahPhase asMage(Point point) {
        return new ZulrahPhase(ZulrahPhaseType.MAGE, point);
    }

    public static ZulrahPhase asMelee(Point point) {
        return new ZulrahPhase(ZulrahPhaseType.MELEE, point);
    }

    public ZulrahPhaseType getType() {
        return type;
    }

    public Point getPoint() {
        return point;
    }
}
