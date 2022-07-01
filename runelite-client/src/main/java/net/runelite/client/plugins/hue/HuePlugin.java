package net.runelite.client.plugins.hue;

import com.google.inject.Provides;
import eu.openvalue.huev2.HueV2;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.*;
import java.util.List;

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

    private HueV2 hue;
    List<String> gradientLights;
    List<String> normalLights;

    long timeAlertEnabled = Integer.MAX_VALUE;
    long timeAlertMs = 2000;

    @Provides
    HueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HueConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        clientThread.invokeLater(() ->
        {
            try {


                hue = new HueV2(BRIDGE_IP, KEY);
                refreshLights();
                setDefaultState();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        handleDisableAlert();
    }

    private void handleDisableAlert(){
        if(timeAlertEnabled == Integer.MAX_VALUE) return;

        long disableAlertAt = timeAlertMs + timeAlertEnabled;

        if(disableAlertAt < System.currentTimeMillis()){
            setDefaultState();
            timeAlertEnabled = Integer.MAX_VALUE;
        }
    }

    private void refreshLights() {
        String roomName = config.room();
        gradientLights = hue.getGradientLightsForRoom(roomName);
        normalLights = hue.getNormalLightsForRoom(roomName);

    }

    @Override
    protected void shutDown() throws Exception {
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        refreshLights();

        if (config.enabled()) {
            setDefaultState();

        } else {
            gradientLights.forEach(l -> hue.turnOff(l));
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        int itemPriceThreshold = config.alertThreshold();

        int price = itemManager.getItemPrice(itemSpawned.getItem().getId());

        if(price > itemPriceThreshold){
            setAlertState();
        }
    }

    private void setDefaultState() {
        gradientLights.forEach(l -> hue.turnOn(l));
        gradientLights.forEach(l -> hue.setGradientColor(l, config.defaultColor(), Color.WHITE));

        normalLights.forEach(l -> hue.turnOn(l));
        normalLights.forEach(l -> hue.setColor(l, config.defaultColor()));
    }

    private void setAlertState() {
        timeAlertEnabled = System.currentTimeMillis();

        gradientLights.forEach(l -> hue.turnOn(l));
        gradientLights.forEach(l -> hue.setGradientColor(l, config.alarmColor()));

        normalLights.forEach(l -> hue.turnOn(l));
        gradientLights.forEach(l -> hue.setGradientColor(l, config.alarmColor()));

    }
}
