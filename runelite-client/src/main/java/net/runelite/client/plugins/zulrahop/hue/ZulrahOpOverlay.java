package net.runelite.client.plugins.zulrahop.hue;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

class ZulrahOpOverlay extends Overlay
{
	private final Client client;
	private final ZulrahOpPlugin plugin;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private ZulrahOpOverlay(Client client, ZulrahOpPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
//		WorldPoint worldPoint = new WorldPoint(3258, 6108, 0);
//		final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
		java.util.List<ZulrahPos> rotation = plugin.getRotations().get(0); // default

		if(plugin.getRotation() != 0) {
			rotation = plugin.getRotations().get(plugin.getRotation()-1);
		}

		ZulrahPos zulrahPos = rotation.get(plugin.getPhase()-1);

		LocalPoint o = client.getScene().getTiles()[client.getPlane()][zulrahPos.getPoint().x][zulrahPos.getPoint().y].getLocalLocation();
		final Polygon poly = Perspective.getCanvasTilePoly(client, o);

		if(poly == null) return null;

		OverlayUtil.renderPolygon(graphics, poly, zulrahPos.getColor(), new Color(0, 0, 0, 50), new BasicStroke((float) 2));

		return null;
	}

}
