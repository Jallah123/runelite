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

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("hueshit")
public interface HueConfig extends Config
{

	@ConfigItem(
			keyName = "enabled",
			name = "Enables shit",
			description = "Enables shit.",
			position = 0
	)
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "brightness",
			name = "Brightness",
			description = "Configures the brightness",
			position = 1,
			section = roomSection
	)
	@Range(
			max = 10
	)
	default int brightness()
	{
		return 6;
	}

	String roomSection = "roomSection";

	@Alpha
	@ConfigItem(
			keyName = "defaultColor",
			name = "Default color",
			description = "Configures the default color",
			position = 2,
			section = roomSection
	)
	default Color highlightDestinationColor()
	{
		return Color.BLUE;
	}

	@ConfigItem(
			keyName = "room",
			name = "Room",
			description = "Your room to enable hue shit for",
			position = 3,
			section = roomSection
	)
	default String room()
	{
		return "";
	}
}
