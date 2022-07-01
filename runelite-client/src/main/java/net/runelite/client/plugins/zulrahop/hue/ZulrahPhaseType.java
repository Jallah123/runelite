package net.runelite.client.plugins.zulrahop.hue;

import java.awt.*;

public enum ZulrahPhaseType {
    MAGE, MELEE, RANGE, RANGE_JAD, MAGE_JAD;

    public Color getColor() {
        switch (this) {
            case MAGE:
                return Color.CYAN;
            case MELEE:
                return Color.RED;
            case RANGE:
                return Color.GREEN;
            case RANGE_JAD:
                return new Color(30, 255, 20);
            case MAGE_JAD:
                return Color.BLUE;
        }
        return Color.DARK_GRAY;
    }
    public Stroke getStroke() {
        switch (this) {
            case MAGE:
                return new BasicStroke((float) 2);
            case MELEE:
                return new BasicStroke((float) 2);
            case RANGE:
                return new BasicStroke((float) 2);
            case RANGE_JAD:
                return new BasicStroke((float) 2);
            case MAGE_JAD:
                return new BasicStroke((float) 2);
        }
        return new BasicStroke((float) 1);
    }
}
