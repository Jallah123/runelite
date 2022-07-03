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

    public List<List<ZulrahPhase>> getRotations() {
        return rotations;
    }

    public void setRotations(List<List<ZulrahPhase>> rotations) {
        this.rotations = rotations;
    }

    private List<List<ZulrahPhase>> rotations = new ArrayList<>();

    private Point BOTTOM_LEFT = new Point(56, 62);
    private Point TOP_LEFT = new Point(57, 55);

    private Point TOP_MIDDLE = new Point(52, 54);
    private Point TOP_RIGHT = new Point(47, 56);
    private Point BOTTOM_RIGHT = new Point(56, 54);


    @Override
    protected void startUp() {
        overlayManager.add(zulrahOpOverlay);

        List<ZulrahPhase> rotation1 = List.of(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_LEFT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(BOTTOM_RIGHT),
                ZulrahPhase.asMelee(BOTTOM_RIGHT)
        );

        List<ZulrahPhase> rotation2 = List.of(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_LEFT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(BOTTOM_RIGHT),
                ZulrahPhase.asMelee(BOTTOM_RIGHT)
        );

        List<ZulrahPhase> rotation3 = List.of(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_MIDDLE),
                ZulrahPhase.asMage(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT)
        );

        List<ZulrahPhase> rotation4 = List.of(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT)
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
                determineRotation(event);
            }
        }
    }

    private void determineRotation(NpcChanged event) {
        System.out.println("Still looking for rotation..");
        System.out.println("Zulrah ID is " + event.getNpc().getId());
        if (phase == 2) {

            System.out.println("PHASE 2: Zulrah ID is " + event.getNpc().getId());
            if (event.getNpc().getId() == ZULRAH) {
                setRotation(3);
            }
            if (event.getNpc().getId() == ZULRAH_2044) { //IDK?
                setRotation(4);
            }
        }
        if (phase == 4 && event.getNpc().getId() == ZULRAH) {
            if (event.getNpc().getOrientation() == 1023) {
                setRotation(1);
            } else {
                setRotation(2);
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
