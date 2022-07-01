package net.runelite.client.plugins.zulrahop.hue;

import java.awt.*;

public class ZulrahPos {

    private Color color;
    private Point point;

    public ZulrahPos(Color color, Point point) {
        this.color = color;
        this.point = point;
    }

    public static ZulrahPos asRanged(Point point) {
        return new ZulrahPos(Color.GREEN, point);
    }

    public static ZulrahPos asMage(Point point) {
        return new ZulrahPos(Color.BLUE, point);
    }

    public static ZulrahPos asMelee(Point point) {
        return new ZulrahPos(Color.RED, point);
    }

    public Color getColor() {
        return color;
    }

    public Point getPoint() {
        return point;
    }
}
