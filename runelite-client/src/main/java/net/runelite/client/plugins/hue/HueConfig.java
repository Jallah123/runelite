/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.hue;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("hueshit")
public interface HueConfig extends Config
{
	@ConfigSection(
		name = "Hue bridge",
		description = "Technical settings for the bridge",
		position = 1
	)
	String bridgeSection = "generalSection";

	@ConfigSection(
		name = "Colors",
		description = "Default",
		position = 2
	)
	String defaultSection = "defaultSection";

	@ConfigSection(
		name = "Alerts",
		description = "Alerts",
		position = 3
	)
	String alertSection = "alertSection";

	@ConfigItem(
		keyName = "enabled",
		name = "Enabled",
		description = "Enables shit.",
		position = 0,
		section = bridgeSection
	)
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "Bridge IP",
		name = "Bridge ip",
		description = "Bridge ip",
		position = 1,
		section = bridgeSection
	)
	default String bridgeIp()
	{
		return "";
	}

	@ConfigItem(
		keyName = "Bridge token",
		name = "Bridge token",
		description = "Bridge token",
		position = 2,
		section = bridgeSection
	)
	default String bridgeToken()
	{
		return "";
	}

	@ConfigItem(
		keyName = "room",
		name = "Room",
		description = "Your room to enable hue shit for",
		position = 3,
		section = bridgeSection
	)
	default String room()
	{
		return "";
	}


	@ConfigItem(
		keyName = "brightness",
		name = "Brightness",
		description = "Configures the brightness",
		position = 1,
		section = defaultSection
	)
	@Range(
		max = 10
	)
	default int brightness()
	{
		return 6;
	}

	@Alpha
	@ConfigItem(
		keyName = "defaultColor",
		name = "Default color",
		description = "Configures the default color",
		position = 2,
		section = defaultSection
	)
	default Color defaultColor()
	{
		return Color.BLUE;
	}


	@ConfigItem(
		keyName = "alertThreshold",
		name = "Alert Item Value Threshold",
		description = "Any item that is more valuable than this threshold will result in an alarm",
		position = 1,
		section = alertSection
	)
	default int alertThreshold()
	{
		return 10000;
	}

	@Alpha
	@ConfigItem(
		keyName = "alarmColor",
		name = "Alarm color",
		description = "Configures the alarm color",
		position = 1,
		section = defaultSection
	)
	default Color alarmColor()
	{
		return Color.RED;
	}

}
