package com.github.naruyoko.minecrafttassimulator;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SimulatorEventHandler {
    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        MinecraftTASSimulatorMod.keybinds.onKeyPress(event);
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        InputEditor.onWorldUnload(event);
        MinecraftTASSimulatorMod.onWorldUnload(event);
    }
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        MinecraftTASSimulatorMod.gui.render(event);
    }
    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        MinecraftTASSimulatorMod.renderer.render(event);
    }
    @SubscribeEvent
    public void applyInputs(TickEvent.ClientTickEvent event) {
        if (InputEditor.isSimulationRunning()) {
            InputEditor.getSimulator().applyInputs(event);
        }
        if (InputEditor.isPredictionRunning()) {
            InputEditor.getPredictor().simulateNextTick(event);
        }
    }
    @SubscribeEvent
    public void unblockKeyBinds(TickEvent.ClientTickEvent event) {
        if (MinecraftTASSimulatorMod.keybinds!=null) MinecraftTASSimulatorMod.keybinds.unblockKeyBinds(event);
    }
}
