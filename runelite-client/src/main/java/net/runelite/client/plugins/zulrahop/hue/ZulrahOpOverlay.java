package net.runelite.client.plugins.zulrahop.hue;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

class ZulrahOpOverlay extends Overlay
{
	private final Client client;
	private final ZulrahOpPlugin plugin;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private static final Set<Integer> ZULRAH_REGIONS = ImmutableSet.of(9007, 9008);

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
		if (client.isInInstancedRegion() && ZULRAH_REGIONS.contains(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID())) {
			drawCurrentTile(graphics);
			drawNextTile(graphics);
		}
		return null;
	}

	private java.util.List<ZulrahPhase> getCurrentRotation() {
		java.util.List<ZulrahPhase> rotation = plugin.getRotations().get(0); // default

		if(plugin.getRotation() != 0) {
			rotation = plugin.getRotations().get(plugin.getRotation()-1);
		}

		return rotation;
	}

	private void drawCurrentTile(Graphics2D graphics){
		ZulrahPhase zulrahPos = getCurrentRotation().get(plugin.getPhase()-1);
		LocalPoint o = client.getScene().getTiles()[client.getPlane()][zulrahPos.getPoint().x][zulrahPos.getPoint().y].getLocalLocation();
		final Polygon poly = Perspective.getCanvasTilePoly(client, o);

		if(poly == null) return;


		Color alphaC = new Color(
				zulrahPos.getType().getColor().getRed(),
				zulrahPos.getType().getColor().getGreen(),
				zulrahPos.getType().getColor().getBlue(),
				180
		);

		OverlayUtil.renderPolygon(graphics, poly, zulrahPos.getType().getColor(), new Color(0, 0, 0, 50), zulrahPos.getType().getStroke());
	}

	private void drawNextTile(Graphics2D graphics){
		if(getCurrentRotation().size() <= plugin.getPhase()) {
			return;
		}

		ZulrahPhase zulrahPos = getCurrentRotation().get(plugin.getPhase());
		LocalPoint o = client.getScene().getTiles()[client.getPlane()][zulrahPos.getPoint().x][zulrahPos.getPoint().y].getLocalLocation();
		final Polygon poly = Perspective.getCanvasTilePoly(client, o);

		if(poly == null) return;

		Color alphaC = new Color(
				zulrahPos.getType().getColor().getRed(),
				zulrahPos.getType().getColor().getGreen(),
				zulrahPos.getType().getColor().getBlue(),
				20
		);

		OverlayUtil.renderPolygon(graphics, poly, Color.DARK_GRAY, alphaC, zulrahPos.getType().getStroke());
	}

}
