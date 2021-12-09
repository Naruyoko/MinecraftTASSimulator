package com.github.naruyoko.minecrafttaseditor;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

public class MinecraftTASEditorCommand extends CommandBase {
	private static Minecraft mc=Minecraft.func_71410_x();
    @Override
    public void func_71515_b(ICommandSender sender,String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;
        if (!mc.func_71356_B()) return;
        if (args==null||args.length==0) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Too few arguments."));
        } else if (args[0].equals("start")) {
            MinecraftTASEditorEditor.start();
        } else if (args[0].equals("stop")) {
            MinecraftTASEditorEditor.stop();
        } else if (args[0].equals("save")) {
            MinecraftTASEditorEditor.saveFile();
        } else if (args[0].equals("load")) {
            MinecraftTASEditorEditor.loadFile();
        } else if (args[0].equals("reinitsim")) {
            MinecraftTASEditorEditor.instanciateRunners();
        } else if (args[0].equals("setstartpos")) {
            Vec3 startPosition;
            if (args.length==1) {
                EntityPlayerMP player=func_71521_c(sender);
                startPosition=MinecraftTASEditorUtil.getPositionVector(player);
            } else if (args.length==4) {
                BlockPos blockpos=func_175757_a(sender,args,1,true);
                startPosition=MinecraftTASEditorUtil.toVector(blockpos);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            MinecraftTASEditorEditor.setStartPosition(startPosition);
        } else if (args[0].equals("setstartmotion")) {
            Vec3 startMotion;
            if (args.length==1) {
                EntityPlayerMP player=func_71521_c(sender);
                startMotion=MinecraftTASEditorUtil.getMotionVector(player);
            } else if (args.length==4) {
                BlockPos blockpos=func_175757_a(sender,args,1,true);
                startMotion=MinecraftTASEditorUtil.toVector(blockpos);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            MinecraftTASEditorEditor.setStartMotion(startMotion);
        } else if (args[0].equals("setstartinvulnerabilityframes")) {
            if (args.length==2) {
                MinecraftTASEditorEditor.setStartInvulnerabilityFrames(Integer.valueOf(args[1]));
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("toggleinheriteffectsfromallticks")) {
            if (args.length==1) {
                if (MinecraftTASEditorEditor.getInheritEffectsFromAllTicks()) {
                    MinecraftTASEditorEditor.setInheritEffectsFromAllTicks(false);
                    sender.func_145747_a(new ChatComponentText("Now only inherits effects on start"));
                } else {
                    MinecraftTASEditorEditor.setInheritEffectsFromAllTicks(true);
                    sender.func_145747_a(new ChatComponentText("Now inherits effects on every tick"));
                }
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("togglegui")) {
            if (args.length==1) {
                if (MinecraftTASEditorMod.gui.shown) {
                    MinecraftTASEditorMod.gui.shown=false;
                    sender.func_145747_a(new ChatComponentText("Hid GUI"));
                } else {
                    MinecraftTASEditorMod.gui.shown=true;
                    sender.func_145747_a(new ChatComponentText("Showed GUI"));
                }
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("detail")) {
            final int SRC_SIMULATION=1;
            final int SRC_PREDICTION=2;
            int source;
            int tick;
            if (args.length==1) {
                source=SRC_SIMULATION;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else if (args.length==2) {
                if (args[1].equalsIgnoreCase("s")) {
                    source=SRC_SIMULATION;
                    tick=MinecraftTASEditorEditor.getSelectedTick();
                } else if (args[1].equalsIgnoreCase("p")) {
                    source=SRC_PREDICTION;
                    tick=MinecraftTASEditorEditor.getSelectedTick();
                } else {
                    source=SRC_SIMULATION;
                    tick=Integer.valueOf(args[1]);
                }
            } else if (args.length==3) {
                if (args[1].equalsIgnoreCase("s")) {
                    source=SRC_SIMULATION;
                } else if (args[1].equalsIgnoreCase("p")) {
                    source=SRC_PREDICTION;
                } else {
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                    return;
                }
                tick=Integer.valueOf(args[2]);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            int computedTicksN;
            if (source==SRC_SIMULATION) {
                computedTicksN=MinecraftTASEditorEditor.getSimulator().getComputedTicksN();
            } else if (source==SRC_PREDICTION) {
                computedTicksN=MinecraftTASEditorEditor.getPredictor().getComputedTicksN();
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
                return;
            }
            int stateIndex=tick+1;
            if (stateIndex<0||stateIndex>computedTicksN) {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            String sourceStr;
            MinecraftTASEditorPlayerInfo playerState;
            if (source==SRC_SIMULATION) {
                sourceStr="simulation";
                playerState=MinecraftTASEditorEditor.getSimulator().getPlayerStateAtIndex(stateIndex);
            } else if (source==SRC_PREDICTION) {
                sourceStr="prediction";
                playerState=MinecraftTASEditorEditor.getPredictor().getPlayerStateAtIndex(stateIndex);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
                return;
            }
            sender.func_145747_a(new ChatComponentText("Tick "+tick+" in "+sourceStr));
            sender.func_145747_a(new ChatComponentText("Position: "+MinecraftTASEditorUtil.stringifyVector(playerState.getPosition(),8)));
            sender.func_145747_a(new ChatComponentText("Motion: "+MinecraftTASEditorUtil.stringifyVector(playerState.getMotion(),8)));
            sender.func_145747_a(new ChatComponentText("Horizontal Speed: "+String.format("%.8f",playerState.getHorizontalSpeed(),8)));
            sender.func_145747_a(new ChatComponentText(playerState.isOnGround()?"On ground":"In air"));
            sender.func_145747_a(new ChatComponentText(playerState.isCollidedHorizontally()?"Collided":"Not collided"));
            sender.func_145747_a(new ChatComponentText("Effects: "+MinecraftTASEditorUtil.stringifyPotionEffects(playerState.getPotionEffects())));
        } else {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Unknown command"));
        }
    }
    @Override
    public List<String> func_180525_a(ICommandSender sender,String[] args,BlockPos pos) {
        if (args.length<1) return null;
        if (args.length<=2) return func_71530_a(args,"start","stop","save","load","reinitsim","setstartpos","setstartmotion","setstartinvulnerabilityframes","toggleinheriteffectsfromallticks","togglegui","detail");
        return null;
    }
    @Override
    public String func_71517_b() {
        return MinecraftTASEditorMod.MODID;
    }
    @Override
    public String func_71518_a(ICommandSender sender) {
        return "command."+MinecraftTASEditorMod.MODID+".usage";
    }
}
