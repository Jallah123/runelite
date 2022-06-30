package net.runelite.client.plugins.hue;

import com.google.inject.Provides;
import io.github.zeroone3010.yahueapi.*;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PluginDescriptor(
        name = "Hue",
        description = "Hue shit",
        tags = {"hue", "shit", "lights"}
)
@Slf4j
public class HuePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private HueConfig config;

    private static final String BRIDGE_IP = "172.16.1.180";
    private static final String APP_NAME = "MyFirstHueApp"; // Fill in the name of your application
    private static final String KEY = "nromEyldXQdCew5h7tHi6XTvu0C4IKO6yPOsGVfF";
    private Hue hue;
    private Optional<Room> room;

    private State defaultState = null;

    @Provides
    HueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HueConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        clientThread.invokeLater(() ->
        {
            try {
                hue = new Hue(BRIDGE_IP, KEY);
                findRoom();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void findRoom() {
        String roomName = config.room();
        room = hue.getRoomByName(roomName);
    }

    @Override
    protected void shutDown() throws Exception {
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        findRoom();
        updateDefaultState();
        if (room.isEmpty()) return;

        if (config.enabled()) {
            room.get().turnOn();
            setState(defaultState);
            applyBrightness();
        } else {
            room.get().turnOff();
        }
    }


    private void updateDefaultState() {
        StateBuilderSteps.InitialStep builder = State.builder();
        builder.effect(EffectType.NONE);
        builder.alert(AlertType.NONE);
        State state = builder.color(getColorPickerValue()).on();
        defaultState = state;
    }

    private void setState(State state) {
        if (room.isEmpty()) return;
        room.get().getLights().forEach(l -> l.setState(state));
    }

    private Color getColorPickerValue() {
        return Color.of(config.highlightDestinationColor());
    }

    private void applyBrightness() {
        if (room.isEmpty()) return;
        int brightness = (int)((config.brightness()/10f) * 254);

        room.get().setBrightness(brightness);
    }

//
//	@Subscribe
//	public void onGameTick(GameTick event) {
//
//	}
}
