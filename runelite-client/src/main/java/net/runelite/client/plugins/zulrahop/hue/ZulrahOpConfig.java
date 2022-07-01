package net.runelite.client.plugins.zulrahop.hue;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("zulrahop")
public interface ZulrahOpConfig extends Config
{
	@ConfigSection(
		name = "On",
		description = "On",
		position = 1
	)
	String on = "on";


}
