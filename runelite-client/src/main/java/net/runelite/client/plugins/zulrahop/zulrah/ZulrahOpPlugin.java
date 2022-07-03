package net.runelite.client.plugins.zulrahop.zulrah;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.Point;
import java.util.*;

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

    public int findRotation() {
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
    private Point BOTTOM_RIGHT = new Point(48, 62);

    private static final int ZULRAH_PHASE_CHANGE_ANIMATION = 5073;
    private static final Set<Integer> ZULRAH_REGIONS = ImmutableSet.of(9007, 9008);
    private ZulrahPhase currentZulrahPhase = null;
    private ZulrahPhase nextZulrahPhase = null;


    @Override
    protected void startUp() {
        overlayManager.add(zulrahOpOverlay);

        List<ZulrahPhase> rotation1 = Arrays.asList(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asMage(TOP_LEFT),
                ZulrahPhase.asRangedJad(TOP_RIGHT),
                ZulrahPhase.asMelee(BOTTOM_LEFT)
        );

        List<ZulrahPhase> rotation2 = Arrays.asList(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asMage(TOP_LEFT),
                ZulrahPhase.asRangedJad(TOP_RIGHT),
                ZulrahPhase.asMelee(BOTTOM_LEFT)
        );

        List<ZulrahPhase> rotation3 = Arrays.asList(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMelee(BOTTOM_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_MIDDLE),
                ZulrahPhase.asMage(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asMageJad(BOTTOM_LEFT)
        );

        List<ZulrahPhase> rotation4 = Arrays.asList(
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMage(BOTTOM_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asMelee(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_LEFT),
                ZulrahPhase.asRanged(TOP_RIGHT),
                ZulrahPhase.asMage(TOP_RIGHT),
                ZulrahPhase.asRanged(BOTTOM_LEFT),
                ZulrahPhase.asMageJad(BOTTOM_LEFT)
        );

        rotations = Arrays.asList(
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
            onNewZulrahPhase(event.getNpc());
        }
    }

    private void resetRotation() {
        phase = 0;
        rotation = 0;
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (isZulrah(npcDespawned.getNpc())) {
            System.out.println("Zulrah dead, resetting");
            resetRotation();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() instanceof NPC && isZulrah((NPC) event.getActor())) {
            if (event.getActor().getAnimation() == ZULRAH_PHASE_CHANGE_ANIMATION) {
                System.out.println("Zulrah changed");
                onNewZulrahPhase((NPC) event.getActor());
            }
        }
    }

    @Subscribe
    void onNpcChanged(NpcChanged event) {
//        if (isZulrah(event.getNpc())) {
//            System.out.println("Zulrah changed");
//            onNewZulrahPhase(event.getNpc());
//        }
    }

    public boolean inZulrahInstance() {
        return client.isInInstancedRegion() &&
                ZULRAH_REGIONS.contains(
                        WorldPoint.fromLocalInstance(
                                client,
                                client.getLocalPlayer().getLocalLocation()
                        ).getRegionID()
                );
    }

    private void onNewZulrahPhase(NPC zulrah) {
        phase += 1;
        List<ZulrahPhase> currentRotation = getRotation(zulrah);
        if (currentRotation.size() < phase) {
            System.out.println("Rotation done and has been reset");
            resetRotation();
            phase += 1;
            currentRotation = getRotation(zulrah);
        }

        System.out.println("phase " + phase);


        currentZulrahPhase = currentRotation.get(phase - 1);
        if (currentRotation.size() > phase) {
            nextZulrahPhase = currentRotation.get(phase);
        } else {
            // We'll just say that the next phase is the first phase of the default rotation
            nextZulrahPhase = getRotations().get(0).get(0);
        }
    }

    private List<ZulrahPhase> getRotation(NPC zulrah) {
        if (rotation != 0) return rotations.get(rotation - 1);

        int determinedRotation = determineRotation(zulrah);

        if (determinedRotation != 0) {
            setRotation(determinedRotation);
            return rotations.get(rotation - 1);
        } else {
            return rotations.get(0); // default
        }
    }

    private int determineRotation(NPC zulrah) {
        int zulrahId = zulrah.getId();
        System.out.println("Still looking for rotation..");
        System.out.println("Zulrah ID is " + zulrah.getId());
        if (phase == 2) {

            System.out.println("PHASE 2: Zulrah ID is " + zulrahId);
            if (zulrahId == ZULRAH) {
                return 3;
            }
            if (zulrahId == ZULRAH_2044) { //IDK?
                return 4;
            }
        }
        if (phase == 4 && zulrahId == ZULRAH) {
            if (zulrah.getOrientation() == 1023) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;//we dont know yet
    }

    private boolean isZulrah(NPC npc) {
        return npc.getId() == ZULRAH || npc.getId() == ZULRAH_2043 || npc.getId() == ZULRAH_2044;
    }

    public void setRotation(int rotation) {
        System.out.println("Rotation found! Rotation is " + rotation);
        this.rotation = rotation;
    }

    public ZulrahPhase getCurrentZulrahPhase() {
        return currentZulrahPhase;
    }

    public ZulrahPhase getNextZulrahPhase() {
        return nextZulrahPhase;
    }
}
