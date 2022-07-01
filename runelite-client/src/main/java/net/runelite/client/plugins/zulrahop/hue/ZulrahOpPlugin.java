package net.runelite.client.plugins.zulrahop.hue;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.api.NpcID.*;

@PluginDescriptor(
        name = "ZulrahOP",
        description = "ZulrahOP",
        tags = {"zulrah"}
)
@Slf4j
public class ZulrahOpPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ZulrahOpOverlay zulrahOpOverlay;

    private int phase = 1;
    private int rotation = 0;

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getRotation() {
        return rotation;
    }

    public List<List<ZulrahPos>> getRotations() {
        return rotations;
    }

    public void setRotations(List<List<ZulrahPos>> rotations) {
        this.rotations = rotations;
    }

    private List<List<ZulrahPos>> rotations = new ArrayList<>();

    private Point BOTTOM_LEFT = new Point(56, 62);
    private Point TOP_LEFT = new Point(57, 55);

    private Point TOP_MIDDLE = new Point(52, 54);
    private Point TOP_RIGHT = new Point(47, 56);
    private Point BOTTOM_RIGHT = new Point(56, 54);


    @Override
    protected void startUp() {
        overlayManager.add(zulrahOpOverlay);

        List<ZulrahPos> rotation1 = List.of(
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asMelee(BOTTOM_LEFT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asMelee(TOP_RIGHT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_LEFT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(BOTTOM_RIGHT),
                ZulrahPos.asMelee(BOTTOM_RIGHT)
        );

        List<ZulrahPos> rotation2 = List.of(
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asMelee(BOTTOM_LEFT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asMelee(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_LEFT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(BOTTOM_RIGHT),
                ZulrahPos.asMelee(BOTTOM_RIGHT)
        );

        List<ZulrahPos> rotation3 = List.of(
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asMelee(BOTTOM_RIGHT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_MIDDLE),
                ZulrahPos.asMage(TOP_LEFT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asMage(BOTTOM_LEFT),
                ZulrahPos.asMage(BOTTOM_LEFT)
        );

        List<ZulrahPos> rotation4 = List.of(
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asMage(BOTTOM_LEFT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asMelee(TOP_LEFT),
                ZulrahPos.asRanged(TOP_LEFT),
                ZulrahPos.asRanged(TOP_RIGHT),
                ZulrahPos.asMage(TOP_RIGHT),
                ZulrahPos.asRanged(BOTTOM_LEFT),
                ZulrahPos.asMage(BOTTOM_LEFT)
                );

        rotations = List.of(
                rotation1,
                rotation2,
                rotation3,
                rotation4
        );
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(zulrahOpOverlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (isZulrah(event.getNpc())) {
            System.out.println("Zulrah spawned, resetting rotation");
            resetRotation();
        }
    }

    private void resetRotation() {
        phase = 1;
        rotation = 0;
    }

    @Subscribe
    void onNpcChanged(NpcChanged event) {

        if (isZulrah(event.getNpc())) {
            phase += 1;
            System.out.println("phase " + phase);

            if (rotation == 0) {
                System.out.println("Still looking for rotation..");
                if (phase == 2) {
                    if (event.getNpc().getId() == ZULRAH) {
                        setRotation(3);
                    }
                    if (event.getNpc().getId() == ZULRAH_2044) { //IDK?
                        setRotation(4);
                    }
                }
                if (phase == 4 && event.getNpc().getId() == ZULRAH) {
                    System.out.println("Orientation " + event.getNpc().getOrientation());
                    if (event.getNpc().getOrientation() == 1023) {
                        setRotation(1);
                    } else {
                        setRotation(2);
                    }
                }
            } else {
                System.out.println("We already know rotation is " + rotation);
            }
        }
    }

    private boolean isZulrah(NPC npc) {
        return npc.getId() == ZULRAH || npc.getId() == ZULRAH_2043 || npc.getId() == ZULRAH_2044;
    }

    public void setRotation(int rotation) {
        System.out.println("Rotation found! Rotation is " + rotation);
        this.rotation = rotation;
    }
}
