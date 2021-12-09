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
    final int SRC_SIMULATION=1;
    final int SRC_PREDICTION=2;
    final int SRC_SAVESLOT1=3;
    final int SRC_SAVESLOT2=4;
    final int SRC_SAVESLOT3=5;
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
        } else if (args[0].equals("setstartgametype")) {
            if (args.length==2) {
                MinecraftTASEditorEditor.setStartGametype(MinecraftTASEditorUtil.toGameType(args[1]));
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
        } else if (args[0].equals("selecttick")) {
            if (args.length==2) {
                int tick=Integer.parseInt(args[1]);
                if (tick<-1) tick=-1;
                MinecraftTASEditorEditor.selectTick(tick);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("warpto")) {
            PlayerStateInfo playerStateInfo=getPlayerStateInfo(sender,args,1);
            MinecraftTASEditorPlayerInfo playerState=playerStateInfo.playerState;
            mc.field_71439_g.func_70080_a(playerState.getPositionX(),playerState.getPositionY(),playerState.getPositionZ(),playerState.getRotationYaw(),playerState.getRotationPitch());
        } else if (args[0].equals("setrotationyaw")) {
            if (!(MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick())) {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            double rotationYaw;
            if (args.length==1) {
                EntityPlayerMP player=func_71521_c(sender);
                rotationYaw=player.field_70177_z;
            } else if (args.length==2) {
                rotationYaw=Double.parseDouble(args[1]);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
            input.setRotationYaw(rotationYaw);
            MinecraftTASEditorEditor.setSelectedInput(input);
        } else if (args[0].equals("setrotationpitch")) {
            if (!(MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick())) {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            double rotationPitch;
            if (args.length==1) {
                EntityPlayerMP player=func_71521_c(sender);
                rotationPitch=player.field_70125_A;
            } else if (args.length==2) {
                rotationPitch=Double.parseDouble(args[1]);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
            input.setRotationPitch(rotationPitch);
            MinecraftTASEditorEditor.setSelectedInput(input);
        } else if (args[0].equals("setrotationexact")) {
            if (!(MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick())) {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            boolean isRotationExact;
            if (args.length==2) {
                isRotationExact=Boolean.parseBoolean(args[1]);
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
            input.setRotationExact(isRotationExact);
            MinecraftTASEditorEditor.setSelectedInput(input);
        } else if (args[0].equals("detail")) {
            PlayerStateInfo playerStateInfo=getPlayerStateInfo(sender,args,1);
            int tick=playerStateInfo.tick;
            MinecraftTASEditorPlayerInfo playerState=playerStateInfo.playerState;
            String sourceStr=playerStateInfo.sourceStr;
            sender.func_145747_a(new ChatComponentText("Tick "+tick+" in "+sourceStr));
            sender.func_145747_a(new ChatComponentText("Position: "+MinecraftTASEditorUtil.stringifyVector(playerState.getPosition(),8)));
            sender.func_145747_a(new ChatComponentText("Motion: "+MinecraftTASEditorUtil.stringifyVector(playerState.getMotion(),8)));
            sender.func_145747_a(new ChatComponentText("Rotation: "+MinecraftTASEditorUtil.stringifyMouse(playerState,8)));
            sender.func_145747_a(new ChatComponentText("Horizontal Speed: "+String.format("%.8f",playerState.getHorizontalSpeed(),8)));
            sender.func_145747_a(new ChatComponentText(playerState.isOnGround()?"On ground":"In air"));
            sender.func_145747_a(new ChatComponentText(playerState.isCollidedHorizontally()?"Collided":"Not collided"));
            sender.func_145747_a(new ChatComponentText("Effects: "+MinecraftTASEditorUtil.stringifyPotionEffects(playerState.getPotionEffects())));
        } else if (args[0].equals("savestatestoslot")) {
            if (args.length==2) {
                int slot=Integer.parseInt(args[1]);
                if (MinecraftTASEditorEditor.playerStatesSaveSlotExists(slot)) {
                    MinecraftTASEditorEditor.savePlayerStatesToSlot(slot);
                } else {
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"The save slot doesn't exist."));
                }
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("removestatesfromslot")) {
            if (args.length==2) {
                int slot=Integer.parseInt(args[1]);
                if (MinecraftTASEditorEditor.playerStatesSaveSlotExists(slot)) {
                    MinecraftTASEditorEditor.removePlayerStatesFromSlot(slot);
                } else {
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"The save slot doesn't exist."));
                }
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Unknown command"));
        }
    }
    public PlayerStateInfo getPlayerStateInfo(ICommandSender sender,String[] args,int startIndex) {
        int source;
        int tick;
        if (args.length==startIndex) {
            source=SRC_SIMULATION;
            tick=MinecraftTASEditorEditor.getSelectedTick();
        } else if (args.length==startIndex+1) {
            if (args[startIndex].equalsIgnoreCase("s")) {
                source=SRC_SIMULATION;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("p")) {
                source=SRC_PREDICTION;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save1")) {
                source=SRC_SAVESLOT1;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save2")) {
                source=SRC_SAVESLOT2;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save3")) {
                source=SRC_SAVESLOT3;
                tick=MinecraftTASEditorEditor.getSelectedTick();
            } else {
                source=SRC_SIMULATION;
                tick=Integer.valueOf(args[startIndex]);
            }
        } else if (args.length==startIndex+2) {
            if (args[startIndex].equalsIgnoreCase("s")) {
                source=SRC_SIMULATION;
            } else if (args[startIndex].equalsIgnoreCase("p")) {
                source=SRC_PREDICTION;
            } else if (args[startIndex].equalsIgnoreCase("save1")) {
                source=SRC_SAVESLOT1;
            } else if (args[startIndex].equalsIgnoreCase("save2")) {
                source=SRC_SAVESLOT2;
            } else if (args[startIndex].equalsIgnoreCase("save3")) {
                source=SRC_SAVESLOT3;
            } else {
                sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return null;
            }
            tick=Integer.valueOf(args[startIndex+1]);
        } else {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
            return null;
        }
        int computedTicksN;
        if (source==SRC_SIMULATION) {
            computedTicksN=MinecraftTASEditorEditor.getSimulator().getComputedTicksN();
        } else if (source==SRC_PREDICTION) {
            computedTicksN=MinecraftTASEditorEditor.getPredictor().getComputedTicksN();
        } else if (source==SRC_SAVESLOT1) {
            computedTicksN=MinecraftTASEditorEditor.getSavedPlayerStates(1).size();
        } else if (source==SRC_SAVESLOT2) {
            computedTicksN=MinecraftTASEditorEditor.getSavedPlayerStates(2).size();
        } else if (source==SRC_SAVESLOT3) {
            computedTicksN=MinecraftTASEditorEditor.getSavedPlayerStates(3).size();
        } else {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
            return null;
        }
        int stateIndex=tick+1;
        if (stateIndex<0||stateIndex>computedTicksN) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
            return null;
        }
        String sourceStr;
        MinecraftTASEditorPlayerInfo playerState;
        if (source==SRC_SIMULATION) {
            sourceStr="simulation";
            playerState=MinecraftTASEditorEditor.getSimulator().getPlayerStateAtIndex(stateIndex);
        } else if (source==SRC_PREDICTION) {
            sourceStr="prediction";
            playerState=MinecraftTASEditorEditor.getPredictor().getPlayerStateAtIndex(stateIndex);
        } else if (source==SRC_SAVESLOT1) {
            sourceStr="save 1";
            playerState=MinecraftTASEditorEditor.getSavedPlayerStates(1).get(stateIndex);
        } else if (source==SRC_SAVESLOT2) {
            sourceStr="save 2";
            playerState=MinecraftTASEditorEditor.getSavedPlayerStates(2).get(stateIndex);
        } else if (source==SRC_SAVESLOT3) {
            sourceStr="save 3";
            playerState=MinecraftTASEditorEditor.getSavedPlayerStates(3).get(stateIndex);
        } else {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
            return null;
        }
        return new PlayerStateInfo(source,tick,playerState,sourceStr);
    }
    @Override
    public List<String> func_180525_a(ICommandSender sender,String[] args,BlockPos pos) {
        if (args.length<1) return null;
        if (args.length<=2) return func_71530_a(args,"start","stop","save","load","reinitsim","selecttick","warpto","setstartpos","setstartmotion","setstartinvulnerabilityframes","setstartgametype","setrotationyaw","setrotationpitch","setrotationexact","toggleinheriteffectsfromallticks","togglegui","detail","savestatestoslot","removestatesfromslot");
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
    private class PlayerStateInfo {
        @SuppressWarnings("unused")
        public int source;
        public int tick;
        public MinecraftTASEditorPlayerInfo playerState;
        public String sourceStr;
        public PlayerStateInfo(int source,int tick,MinecraftTASEditorPlayerInfo playerState,String sourceStr) {
            this.source=source;
            this.tick=tick;
            this.playerState=playerState;
            this.sourceStr=sourceStr;
        }
    }
}
