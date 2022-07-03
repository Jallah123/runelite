package net.runelite.client.plugins.zulrahop.zulrah;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

class ZulrahOpOverlay extends Overlay {
    private final Client client;
    private final ZulrahOpPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private ZulrahOpOverlay(Client client, ZulrahOpPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.inZulrahInstance()) {
            drawCurrentTile(graphics);
            drawNextTile(graphics);
        }
        return null;
    }

    private void drawCurrentTile(Graphics2D graphics) {
        if (plugin.getCurrentZulrahPhase() == null) {
            return;
        }

        ZulrahPhase zulrahPos = plugin.getCurrentZulrahPhase();
        LocalPoint o = client.getScene().getTiles()[client.getPlane()][zulrahPos.getPoint().x][zulrahPos.getPoint().y].getLocalLocation();
        final Polygon poly = Perspective.getCanvasTilePoly(client, o);

        if (poly == null) return;

        Color alphaC = new Color(
                zulrahPos.getType().getColor().getRed(),
                zulrahPos.getType().getColor().getGreen(),
                zulrahPos.getType().getColor().getBlue(),
                180
        );
        OverlayUtil.renderPolygon(graphics, poly, alphaC, new Color(0, 0, 0, 50), zulrahPos.getType().getStroke());
    }

    private void drawNextTile(Graphics2D graphics) {
        if (plugin.getNextZulrahPhase() == null) {
            return;
        }

        ZulrahPhase zulrahPos = plugin.getNextZulrahPhase();
        LocalPoint o = client.getScene().getTiles()[client.getPlane()][zulrahPos.getPoint().x][zulrahPos.getPoint().y].getLocalLocation();
        final Polygon poly = Perspective.getCanvasTilePoly(client, o, 5);

        if (poly == null) return;

        Color alphaC = new Color(
                zulrahPos.getType().getColor().getRed(),
                zulrahPos.getType().getColor().getGreen(),
                zulrahPos.getType().getColor().getBlue(),
                180
        );
        int thickness = 3;
        int length = 2;
        int spacing = 1;
        float[] array = { thickness * (length - 1.0f), thickness * (spacing + 1.0f) };

        OverlayUtil.renderPolygon(
                graphics,
                poly,
                alphaC,
                new Color(0, 0, 0, 50),
                new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, array, 1.0f)
        );
    }

}
