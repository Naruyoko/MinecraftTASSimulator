package com.github.naruyoko.minecrafttaseditor;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MinecraftTASEditorEventHandler {
    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        MinecraftTASEditorMod.keybinds.onKeyPress(event);
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        MinecraftTASEditorEditor.onWorldUnload(event);
        MinecraftTASEditorMod.onWorldUnload(event);
    }
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        MinecraftTASEditorMod.gui.render(event);
    }
    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        MinecraftTASEditorMod.renderer.render(event);
    }
    @SubscribeEvent
    public void applyInputs(TickEvent.ClientTickEvent event) {
        if (MinecraftTASEditorEditor.isSimulationRunning()) {
            MinecraftTASEditorEditor.getSimulator().applyInputs(event);
        }
        if (MinecraftTASEditorEditor.isPredictionRunning()) {
            MinecraftTASEditorEditor.getPredictor().simulateNextTick(event);
        }
    }
}
