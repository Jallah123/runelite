package net.runelite.client.plugins.hue;

import io.github.zeroone3010.yahueapi.Hue;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
	name = "Hue",
	description = "Hue shit"
)
public class HuePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	private static final String BRIDGE_IP = "";
	private static final String APP_NAME = "MyFirstHueApp"; // Fill in the name of your application
	private static final String KEY = "";
	private Hue hue;

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invokeLater(() ->
		{
			try
			{
				hue = new Hue(BRIDGE_IP, KEY);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{

	}
}
