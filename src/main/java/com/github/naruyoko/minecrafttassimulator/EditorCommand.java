package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.naruyoko.minecrafttassimulator.Input.MouseButtonInputEnum;

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
import net.minecraft.world.WorldSettings.GameType;

public class EditorCommand extends CommandBase {
	private static Minecraft mc=Minecraft.getMinecraft();
    final int SRC_SIMULATION=1;
    final int SRC_PREDICTION=2;
    final int SRC_SAVESLOT1=3;
    final int SRC_SAVESLOT2=4;
    final int SRC_SAVESLOT3=5;
    @Override
    public void processCommand(ICommandSender sender,String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;
        if (!mc.isSingleplayer()) return;
        if (args==null||args.length==0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Too few arguments."));
        } else if (args[0].equals("start")) {
            InputEditor.start();
        } else if (args[0].equals("stop")) {
            InputEditor.stop();
        } else if (args[0].equals("save")) {
            if (args.length==1) {
                InputEditor.saveFile();
            } else {
                InputEditor.saveFile(args[1]);
            }
        } else if (args[0].equals("load")) {
            if (args.length==1) {
                InputEditor.loadFile();
            } else {
                InputEditor.loadFile(args[1]);
            }
        } else if (args[0].equals("reinitsim")) {
            InputEditor.instanciateRunners();
        } else if (args[0].equals("setstartpos")) {
            Vec3 startPosition = null;
            if (args.length==1) {
                EntityPlayerMP player=getCommandSenderAsPlayer(sender);
                try {
                    startPosition=SimulatorUtil.getPositionVector(player);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (args.length==4) {
                EntityPlayerMP player=getCommandSenderAsPlayer(sender);
                CommandBase.CoordinateArg coordinateArg1=parseCoordinate(player.posX,args[1],true);
                CommandBase.CoordinateArg coordinateArg2=parseCoordinate(player.posX,args[2],0,0,false);
                CommandBase.CoordinateArg coordinateArg3=parseCoordinate(player.posX,args[3],true);
                startPosition=new Vec3(coordinateArg1.func_179628_a(),coordinateArg2.func_179628_a(),coordinateArg3.func_179628_a());
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            InputEditor.setStartPosition(startPosition);
            sender.addChatMessage(new ChatComponentText("Initial position set to "+SimulatorUtil.stringifyVec3(startPosition)));
        } else if (args[0].equals("setstartmotion")) {
            Vec3 startMotion;
            if (args.length==1) {
                EntityPlayerMP player=getCommandSenderAsPlayer(sender);
                startMotion=SimulatorUtil.getMotionVector(player);
            } else if (args.length==4) {
                BlockPos blockpos=parseBlockPos(sender,args,1,true);
                startMotion=SimulatorUtil.toVec3(blockpos);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            InputEditor.setStartMotion(startMotion);
            sender.addChatMessage(new ChatComponentText("Initial velocity set to "+SimulatorUtil.stringifyVec3(startMotion)));
        } else if (args[0].equals("setstartinvulnerabilityframes")) {
            if (args.length==2) {
                Integer startInvulnerabilityFrames = Integer.valueOf(args[1]);
                InputEditor.setStartInvulnerabilityFrames(startInvulnerabilityFrames);
                sender.addChatMessage(new ChatComponentText("Initial invulnerability set to "+startInvulnerabilityFrames+" frames"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("setstartgametype")) {
            if (args.length==2) {
                GameType startGameType = SimulatorUtil.toGameType(args[1]);
                InputEditor.setStartGametype(startGameType);
                sender.addChatMessage(new ChatComponentText("Initial game mode set to "+SimulatorUtil.stringifyGameType(startGameType)));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("setmousesensitivity")) {
            if (args.length==2) {
                Float mouseSensitivity = Float.valueOf(args[1]);
                InputEditor.setMouseSensitivity(mouseSensitivity);
                sender.addChatMessage(new ChatComponentText("Mouse sensitivity set to "+mouseSensitivity+" ("+String.format("%.8f",SimulatorUtil.getRotationPerPixel(mouseSensitivity))+"deg/px, "+(int)(mouseSensitivity*200)+"%)"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("setmousemaxsafemovement")) {
            if (args.length==2) {
                Integer mouseMaxSafeMovement = Integer.valueOf(args[1]);
                InputEditor.setMouseMaxSafeMovement(mouseMaxSafeMovement);
                sender.addChatMessage(new ChatComponentText("Maximum safe singular mouse movement set to "+mouseMaxSafeMovement+"px"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("toggleinheriteffectsfromallticks")) {
            if (args.length==1) {
                if (InputEditor.getInheritEffectsFromAllTicks()) {
                    InputEditor.setInheritEffectsFromAllTicks(false);
                    sender.addChatMessage(new ChatComponentText("Now only inherits effects on start"));
                } else {
                    InputEditor.setInheritEffectsFromAllTicks(true);
                    sender.addChatMessage(new ChatComponentText("Now inherits effects on every tick"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("togglegui")) {
            if (args.length==1) {
                if (MinecraftTASSimulatorMod.gui.shown) {
                    MinecraftTASSimulatorMod.gui.shown=false;
                    sender.addChatMessage(new ChatComponentText("Hid GUI"));
                } else {
                    MinecraftTASSimulatorMod.gui.shown=true;
                    sender.addChatMessage(new ChatComponentText("Showed GUI"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("selecttick")) {
            if (args.length==2) {
                int tick=Integer.parseInt(args[1]);
                if (tick<-1) tick=-1;
                InputEditor.selectTick(tick);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("warpto")) {
            PlayerStateInfo playerStateInfo=getPlayerStateInfo(sender,args,1);
            SimulatedPlayerInfo playerState=playerStateInfo.playerState;
            mc.thePlayer.setPositionAndRotation(playerState.getPositionX(),playerState.getPositionY(),playerState.getPositionZ(),playerState.getRotationYaw(),playerState.getRotationPitch());
        } else if (args[0].equals("setrotationyaw")) {
            if (!(InputEditor.isRunning()&&InputEditor.isSelectingTick())) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            double rotationYaw;
            if (args.length==1) {
                EntityPlayerMP player=getCommandSenderAsPlayer(sender);
                rotationYaw=player.rotationYaw;
            } else if (args.length==2) {
                rotationYaw=Double.parseDouble(args[1]);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            Input input=InputEditor.getSelectedInput();
            input.setRotationYaw(rotationYaw);
            InputEditor.setSelectedInput(input);
        } else if (args[0].equals("setrotationpitch")) {
            if (!(InputEditor.isRunning()&&InputEditor.isSelectingTick())) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            double rotationPitch;
            if (args.length==1) {
                EntityPlayerMP player=getCommandSenderAsPlayer(sender);
                rotationPitch=player.rotationPitch;
            } else if (args.length==2) {
                rotationPitch=Double.parseDouble(args[1]);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            Input input=InputEditor.getSelectedInput();
            input.setRotationPitch(rotationPitch);
            InputEditor.setSelectedInput(input);
        } else if (args[0].equals("setrotationexact")) {
            if (!(InputEditor.isRunning()&&InputEditor.isSelectingTick())) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            boolean isRotationExact;
            if (args.length==2) {
                isRotationExact=Boolean.parseBoolean(args[1]);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            Input input=InputEditor.getSelectedInput();
            input.setRotationExact(isRotationExact);
            InputEditor.setSelectedInput(input);
        } else if (args[0].equals("setmousebuttoninputs")) {
            if (!(InputEditor.isRunning()&&InputEditor.isSelectingTick())) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Editor must be running and a tick must be selected."));
                return;
            }
            List<MouseButtonInputEnum> mouseButtonInputs;
            if (args.length==1) {
                mouseButtonInputs=Collections.emptyList();
            } else if (args.length==2) {
                mouseButtonInputs=SimulatorUtil.parseMouseButtonInputs(args[1]);
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
            Input input=InputEditor.getSelectedInput();
            input.setMouseButtonInputs(mouseButtonInputs);
            InputEditor.setSelectedInput(input);
        } else if (args[0].equals("detail")) {
            PlayerStateInfo playerStateInfo=getPlayerStateInfo(sender,args,1);
            int tick=playerStateInfo.tick;
            EntityMovementInfo entityInfo=playerStateInfo.playerState;
            String sourceStr=playerStateInfo.sourceStr;
            sender.addChatMessage(new ChatComponentText("Tick "+tick+" in "+sourceStr));
            while (entityInfo!=null) {
                sender.addChatMessage(new ChatComponentText("Position: "+SimulatorUtil.stringifyVec3(entityInfo.getPosition(),8)));
                sender.addChatMessage(new ChatComponentText("Motion: "+SimulatorUtil.stringifyVec3(entityInfo.getMotion(),8)));
                sender.addChatMessage(new ChatComponentText("Rotation: "+SimulatorUtil.stringifyMouse(entityInfo,8)));
                sender.addChatMessage(new ChatComponentText("Horizontal Speed: "+String.format("%.8f",entityInfo.getHorizontalSpeed(),8)));
                sender.addChatMessage(new ChatComponentText(entityInfo.isOnGround()?"On ground":"In air"));
                sender.addChatMessage(new ChatComponentText(entityInfo.isCollidedHorizontally()?"Collided":"Not collided"));
                sender.addChatMessage(new ChatComponentText("Effects: "+SimulatorUtil.stringifyPotionEffects(entityInfo.getPotionEffects())));
                if (entityInfo.isRiding()) {
                    sender.addChatMessage(new ChatComponentText("Riding: "+entityInfo.getRidingEntityInfo().getThisClass().getSimpleName()));
                }
                entityInfo=entityInfo.getRidingEntityInfo();
            }
        } else if (args[0].equals("savestatestoslot")) {
            if (args.length==2) {
                int slot=Integer.parseInt(args[1]);
                if (InputEditor.playerStatesSaveSlotExists(slot)) {
                    InputEditor.savePlayerStatesToSlot(slot);
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"The save slot doesn't exist."));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else if (args[0].equals("removestatesfromslot")) {
            if (args.length==2) {
                int slot=Integer.parseInt(args[1]);
                if (InputEditor.playerStatesSaveSlotExists(slot)) {
                    InputEditor.removePlayerStatesFromSlot(slot);
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"The save slot doesn't exist."));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return;
            }
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Unknown command"));
        }
    }
    public PlayerStateInfo getPlayerStateInfo(ICommandSender sender,String[] args,int startIndex) {
        int source;
        int tick;
        if (args.length==startIndex) {
            source=SRC_SIMULATION;
            tick=InputEditor.getSelectedTick();
        } else if (args.length==startIndex+1) {
            if (args[startIndex].equalsIgnoreCase("s")) {
                source=SRC_SIMULATION;
                tick=InputEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("p")) {
                source=SRC_PREDICTION;
                tick=InputEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save1")) {
                source=SRC_SAVESLOT1;
                tick=InputEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save2")) {
                source=SRC_SAVESLOT2;
                tick=InputEditor.getSelectedTick();
            } else if (args[startIndex].equalsIgnoreCase("save3")) {
                source=SRC_SAVESLOT3;
                tick=InputEditor.getSelectedTick();
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
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
                return null;
            }
            tick=Integer.valueOf(args[startIndex+1]);
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
            return null;
        }
        int computedTicksN;
        if (source==SRC_SIMULATION) {
            computedTicksN=InputEditor.getSimulator().getComputedTicksN();
        } else if (source==SRC_PREDICTION) {
            computedTicksN=InputEditor.getPredictor().getComputedTicksN();
        } else if (source==SRC_SAVESLOT1) {
            computedTicksN=InputEditor.getSavedPlayerStates(1).size();
        } else if (source==SRC_SAVESLOT2) {
            computedTicksN=InputEditor.getSavedPlayerStates(2).size();
        } else if (source==SRC_SAVESLOT3) {
            computedTicksN=InputEditor.getSavedPlayerStates(3).size();
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
            return null;
        }
        int stateIndex=tick+1;
        if (stateIndex<0||stateIndex>computedTicksN) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid arguments"));
            return null;
        }
        String sourceStr;
        SimulatedPlayerInfo playerState;
        if (source==SRC_SIMULATION) {
            sourceStr="simulation";
            playerState=InputEditor.getSimulator().getPlayerStateAtIndex(stateIndex);
        } else if (source==SRC_PREDICTION) {
            sourceStr="prediction";
            playerState=InputEditor.getPredictor().getPlayerStateAtIndex(stateIndex);
        } else if (source==SRC_SAVESLOT1) {
            sourceStr="save 1";
            playerState=InputEditor.getSavedPlayerStates(1).get(stateIndex);
        } else if (source==SRC_SAVESLOT2) {
            sourceStr="save 2";
            playerState=InputEditor.getSavedPlayerStates(2).get(stateIndex);
        } else if (source==SRC_SAVESLOT3) {
            sourceStr="save 3";
            playerState=InputEditor.getSavedPlayerStates(3).get(stateIndex);
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Something went wrong"));
            return null;
        }
        return new PlayerStateInfo(source,tick,playerState,sourceStr);
    }
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender,String[] args,BlockPos pos) {
        if (args.length<=1) return getListOfStringsMatchingLastWord(args,
                "start",
                "stop",
                "save",
                "load",
                "reinitsim",
                "selecttick",
                "warpto",
                "setstartpos",
                "setstartmotion",
                "setstartinvulnerabilityframes",
                "setstartgametype",
                "setmousesensitivity",
                "setmousemaxsafemovement",
                "setrotationyaw",
                "setrotationpitch",
                "setrotationexact",
                "setmousebuttoninputs",
                "toggleinheriteffectsfromallticks",
                "togglegui",
                "detail",
                "savestatestoslot",
                "removestatesfromslot");
        if (args.length==2) {
            if (args[0].equals("save")) {
                List<String> list=new ArrayList<String>();
                Set<String> set=InputFileWorkerEnum.getWritableExtensions();
                Iterator<String> iterator=set.iterator();
                boolean hasPeriod=args[1].length()==0||args[1].charAt(0)=='.';
                while (iterator.hasNext()) list.add(iterator.next().substring(hasPeriod?0:1));
                return getListOfStringsMatchingLastWord(args,list);
            } else if (args[0].equals("load")) {
                List<String> list=new ArrayList<String>();
                Set<String> set=InputFileWorkerEnum.getReadableExtensions();
                Iterator<String> iterator=set.iterator();
                boolean hasPeriod=args[1].length()==0||args[1].charAt(0)=='.';
                while (iterator.hasNext()) list.add(iterator.next().substring(hasPeriod?0:1));
                return getListOfStringsMatchingLastWord(args,list);
            }
        }
        return null;
    }
    @Override
    public String getCommandName() {
        return MinecraftTASSimulatorMod.MODID;
    }
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command."+MinecraftTASSimulatorMod.MODID+".usage";
    }
    private class PlayerStateInfo {
        @SuppressWarnings("unused")
        public int source;
        public int tick;
        public SimulatedPlayerInfo playerState;
        public String sourceStr;
        public PlayerStateInfo(int source,int tick,SimulatedPlayerInfo playerState,String sourceStr) {
            this.source=source;
            this.tick=tick;
            this.playerState=playerState;
            this.sourceStr=sourceStr;
        }
    }
}
