package com.github.naruyoko.minecrafttassimulator;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid=MinecraftTASSimulatorMod.MODID,name=MinecraftTASSimulatorMod.NAME,version=MinecraftTASSimulatorMod.VERSION)
public class MinecraftTASSimulatorMod {
    public static final String MODID="minecrafttassimulator";
    public static final String NAME="Minecraft TAS Simulator";
    public static final String VERSION="${version}";
    public static final String MCVERSION="${mcversion}";
    private static Minecraft mc=Minecraft.getMinecraft();
    public static Logger logger=null;
    public static EditorGui gui;
    public static Renderer renderer;
    public static EditorKeybinds keybinds;
    public static SimulatorEventHandler eventHandler;
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger=event.getModLog();
    }
    @EventHandler
    public void init(FMLServerStartingEvent event)
    {
        InputEditor.init();
        gui=new EditorGui(mc);
        renderer=new Renderer(mc);
        keybinds=new EditorKeybinds();
        eventHandler=new SimulatorEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        event.registerServerCommand(new EditorCommand());
    }
    public static void onWorldUnload(WorldEvent.Unload event) {
        MinecraftForge.EVENT_BUS.unregister(eventHandler);
    }
    public static void outputWarningToChatAndLog(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+message));
        logger.warn(message);
    }
    public static void outputErrorToChatAndLog(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+message));
        logger.error(message);
    }
}
