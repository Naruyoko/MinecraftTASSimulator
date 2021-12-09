package com.github.naruyoko.minecrafttaseditor;

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

@Mod(modid=MinecraftTASEditorMod.MODID,name=MinecraftTASEditorMod.NAME,version=MinecraftTASEditorMod.VERSION)
public class MinecraftTASEditorMod {
    public static final String MODID="minecrafttaseditor";
    public static final String NAME="Minecraft TAS Editor";
    public static final String VERSION="0.0.3";
    private static Minecraft mc=Minecraft.func_71410_x();
    public static Logger logger=null;
    public static MinecraftTASEditorGui gui;
    public static MinecraftTASEditorRenderer renderer;
    public static MinecraftTASEditorKeybinds keybinds;
    public static MinecraftTASEditorEventHandler eventHandler;
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger=event.getModLog();
    }
    @EventHandler
    public void init(FMLServerStartingEvent event)
    {
        MinecraftTASEditorEditor.init();
        gui=new MinecraftTASEditorGui(mc);
        renderer=new MinecraftTASEditorRenderer(mc);
        keybinds=new MinecraftTASEditorKeybinds();
        eventHandler=new MinecraftTASEditorEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        event.registerServerCommand(new MinecraftTASEditorCommand());
    }
    public static void onWorldUnload(WorldEvent.Unload event) {
        MinecraftForge.EVENT_BUS.unregister(eventHandler);
    }
    public static void outputErrorToChatAndLog(String message) {
        mc.field_71439_g.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+message));
        logger.error(message);
    }
}
