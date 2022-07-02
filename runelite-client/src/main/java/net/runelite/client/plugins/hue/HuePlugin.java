package net.runelite.client.plugins.hue;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import eu.openvalue.huev2.HueV2;
import java.awt.Color;
import java.time.Duration;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import static net.runelite.api.NpcID.ZULRAH;
import static net.runelite.api.NpcID.ZULRAH_2043;
import static net.runelite.api.NpcID.ZULRAH_2044;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.plugins.hue.Feature.HUE;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Hue",
	description = "Hue shit",
	tags = {"hue", "shit", "lights"}
)
@Slf4j
public class HuePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private HueConfig config;

	private static final long TIME_ALERT_MS = 2000;
	private static final Color PURPLE = new Color(161, 52, 235);
	private static final Color LIGHT_BLUE = new Color(52, 198, 235);

	private HueV2 hue;
	private List<String> gradientLights;
	private List<String> normalLights;
	private final ExecutorService hueExecutorService = Executors.newFixedThreadPool(1);
	private long timeAlertEnabled = Integer.MAX_VALUE;
	private State currentState = State.IDLE;
	private long lastSkyboxUpdate = System.currentTimeMillis();

	private static final Set<Integer> ZULRAH_REGIONS = ImmutableSet.of(9007, 9008);
	private static final Map<Integer, State> ZULRAH_STATES = new HashMap<Integer, State>()
	{{
		put(ZULRAH, State.ZULRAH_COMBAT_RANGE);
		put(ZULRAH_2043, State.ZULRAH_COMBAT_MELEE);
		put(ZULRAH_2044, State.ZULRAH_COMBAT_MAGE);
	}};

	private final Map<State, Map<Feature, Runnable>> stateConsumerMap = new HashMap<State, Map<Feature, Runnable>>()
	{{
		put(State.IDLE, new HashMap<Feature, Runnable>()
		{{
			put(HUE, () -> hueExecutorService.submit(() -> {
				int skybox = client.getSkyboxColor();
				Color color = new Color((skybox >> 16) & 0xFF, (skybox >> 8) & 0xFF, (skybox) & 0xFF);
				gradientLights.forEach(id -> hue.setGradientColor(id, color));
				normalLights.forEach(id -> hue.setColor(id, color));
			}));
		}});
		put(State.ZULRAH_BEFORE_FIGHT, emptyMap());
		put(State.ZULRAH_COMBAT_RANGE, new HashMap<Feature, Runnable>()
		{{
			put(HUE, () -> hueExecutorService.submit(() -> {
				gradientLights.forEach(id -> hue.setGradientColor(id, Color.GREEN));
				normalLights.forEach(id -> hue.setColor(id, Color.GREEN));
			}));
		}});
		put(State.ZULRAH_COMBAT_MAGE, new HashMap<Feature, Runnable>()
		{{
			put(HUE, () -> hueExecutorService.submit(() -> {
				gradientLights.forEach(id -> hue.setGradientColor(id, Color.CYAN));
				normalLights.forEach(id -> hue.setColor(id, Color.CYAN));
			}));
		}});
		put(State.ZULRAH_COMBAT_MELEE, new HashMap<Feature, Runnable>()
		{{
			put(HUE, () -> hueExecutorService.submit(() -> {
				gradientLights.forEach(id -> hue.setGradientColor(id, Color.RED));
				normalLights.forEach(id -> hue.setColor(id, Color.RED));
			}));
		}});
		put(State.ZULRAH_AFTER_FIGHT, emptyMap());
	}};

	@Provides
	HueConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HueConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invokeLater(() ->
		{
			try
			{
				hue = new HueV2(config.bridgeIp(), config.bridgeToken());
				refreshLights();
				setDefaultState();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void setState(State state)
	{
		currentState = state;
		stateConsumerMap.get(state).values().forEach(Runnable::run);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		handleDisableAlert();
		if (currentState == State.IDLE && System.currentTimeMillis() - lastSkyboxUpdate > 1000L)
		{
			updateSkybox();
			return;
		}
		if (currentState == State.IDLE &&
			client.isInInstancedRegion() &&
			ZULRAH_REGIONS.contains(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID())
		)
		{
			setState(State.ZULRAH_BEFORE_FIGHT);
			return;
		}
		if (currentState.name().toLowerCase().contains("zulrah") && (!client.isInInstancedRegion() || !ZULRAH_REGIONS.contains(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID())))
		{
			setState(State.IDLE);
			return;
		}
	}

	private void updateSkybox()
	{
		lastSkyboxUpdate = System.currentTimeMillis();
		int skybox = client.getSkyboxColor();
		hueExecutorService.submit(() -> {
			Color color = new Color((skybox >> 16) & 0xFF, (skybox >> 8) & 0xFF, (skybox) & 0xFF);
			gradientLights.forEach(id -> hue.setGradientColor(id, color));
			normalLights.forEach(id -> hue.setColor(id, color));
		});
	}

	private void handleDisableAlert()
	{
		if (timeAlertEnabled == Integer.MAX_VALUE)
		{
			return;
		}

		long disableAlertAt = TIME_ALERT_MS + timeAlertEnabled;

		if (disableAlertAt < System.currentTimeMillis())
		{
			setDefaultState();
			timeAlertEnabled = Integer.MAX_VALUE;
		}
	}

	private void refreshLights()
	{
		try
		{
			hue = new HueV2(config.bridgeIp(), config.bridgeToken());
			String roomName = config.room();
			gradientLights = hue.getGradientLightsForRoom(roomName);
			normalLights = hue.getNormalLightsForRoom(roomName);
		}
		catch (Exception e)
		{

		}
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		refreshLights();
		if (config.enabled())
		{
			setDefaultState();
		}
		else
		{
			hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.turnOff(l)));
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		int itemPriceThreshold = config.alertThreshold();
		int price = itemManager.getItemPrice(itemSpawned.getItem().getId());
		if (price > itemPriceThreshold)
		{
			setAlertState();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (ZULRAH == npc.getId())
		{
			setState(State.ZULRAH_COMBAT_RANGE);
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcChanged)
	{
		if (ZULRAH_STATES.containsKey(npcChanged.getNpc().getId()))
		{
			if (npcChanged.getNpc().isDead())
			{
				setState(State.ZULRAH_AFTER_FIGHT);
			}
			else
			{
				setState(ZULRAH_STATES.get(npcChanged.getNpc().getId()));
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) throws InterruptedException
	{
		String message = Text.removeTags(chatMessage.getMessage());
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (message.contains("You have a funny feeling like") || message.contains("You feel something weird sneaking into your backpack"))
			{
				// do something
			}
			else if (message.contains("Oh dear, you are dead!"))
			{
				hueExecutorService.submit(() -> hue.fireWorks(gradientLights, normalLights, Duration.ofSeconds(10)));
			}
			else if (message.contains("Congratulations, you've just advanced"))
			{
				hueExecutorService.submit(() -> hue.fireWorks(gradientLights, normalLights, Duration.ofSeconds(10)));
			}
			else if (message.contains(client.getLocalPlayer().getName()))
			{
				if (message.contains("enhanced"))
				{
					gradientLights.forEach(id -> hue.setGradientColor(id, PURPLE, Duration.ofSeconds(15)));
					normalLights.forEach(id -> hue.setColor(id, PURPLE, Duration.ofSeconds(15)));
				}
				else if (message.contains("armour"))
				{
					gradientLights.forEach(id -> hue.setGradientColor(id, LIGHT_BLUE, Duration.ofSeconds(15)));
					normalLights.forEach(id -> hue.setColor(id, LIGHT_BLUE, Duration.ofSeconds(15)));
				}
			}
		}
		else if (chatMessage.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			if (message.contains("Special loot"))
			{
				hueExecutorService.submit(() -> {
					gradientLights.forEach(id -> hue.setGradientColor(id, PURPLE, Duration.ofSeconds(15)));
					normalLights.forEach(id -> hue.setColor(id, PURPLE, Duration.ofSeconds(15)));
				});
			}
		}
	}

	private void setDefaultState()
	{
		setState(State.IDLE);
		hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.turnOn(l)));
		hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.setGradientColor(l, config.defaultColor(), Color.WHITE)));

		hueExecutorService.submit(() -> normalLights.forEach(l -> hue.turnOn(l)));
		hueExecutorService.submit(() -> normalLights.forEach(l -> hue.setColor(l, config.defaultColor())));
	}

	private void setAlertState()
	{
		timeAlertEnabled = System.currentTimeMillis();

		hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.turnOn(l)));
		hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.setGradientColor(l, config.alarmColor())));

		hueExecutorService.submit(() -> normalLights.forEach(l -> hue.turnOn(l)));
		hueExecutorService.submit(() -> gradientLights.forEach(l -> hue.setGradientColor(l, config.alarmColor())));
	}
}
