package com.github.naruyoko.minecrafttassimulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.event.world.WorldEvent;

public class InputEditor {
    private static Minecraft mc=Minecraft.getMinecraft();
    private static Simulator simulator=null;
    private static Predictor predictor=null;
    private static InputList inputs=null;
    private static InputList actualInputs=null;
    private static boolean isRunning=false;
    private static int predictionStartTick;
    private static int predictionStartTickActual;
    private static int tickLength;
    private static int selectedTick;
    private static Vec3 startPosition;
    private static Vec3 startMotion;
    private static int startInvulnerabilityFrames;
    private static GameType startGametype;
    private static float mouseSensitivity;
    private static int mouseMaxSafeMovement;
    private static boolean inheritEffectsFromAllTicks;
    private static int rerecords;
    private static int predictionRerecords;
    private static int totalRerecords;
    private static boolean wasEverInit;
    private static Vec3 savedPosition;
    private static double savedRotationYaw;
    private static double savedRotationPitch;
    private static GameType savedGametype;
    private static boolean editedSinceLastSimulation;
    private static boolean editedSinceLastPrediction;
    private static int simulationUntrustableFrom;
    private static int predictionUntrustableFrom;
    private static ArrayList<ArrayList<SimulatedPlayerInfo>> savedPlayerStates;
    private static int playerStateSaveSlots;
    public static void init() {
        wasEverInit=false;
        startPosition=new Vec3(0,0,0);
        startMotion=new Vec3(0,0,0);
        startInvulnerabilityFrames=0;
        startGametype=GameType.NOT_SET;
        mouseSensitivity=0.5F;
        mouseMaxSafeMovement=900;
        inheritEffectsFromAllTicks=true;
        simulator=null;
        predictor=null;
        selectedTick=-1;
        inputs=new InputList();
        actualInputs=new InputList();
        predictionStartTick=0;
        predictionStartTickActual=0;
        tickLength=0;
        rerecords=0;
        predictionRerecords=0;
        totalRerecords=0;
        editedSinceLastSimulation=false;
        editedSinceLastPrediction=false;
        simulationUntrustableFrom=-1;
        predictionUntrustableFrom=-1;
        playerStateSaveSlots=3;
        savedPlayerStates=new ArrayList<ArrayList<SimulatedPlayerInfo>>(playerStateSaveSlots);
        for (int i=0;i<playerStateSaveSlots;i++) savedPlayerStates.add(null);
    }
    public static boolean isRunning() {
        return isRunning;
    }
    /**
     * Starts editor. Only works in single player mode.
     */
    public static void start() {
        if (mc.isSingleplayer()) {
            if (!isRunning) {
                if (!wasEverInit) {
                    wasEverInit=true;
                    if (mc.thePlayer!=null) {
                        startPosition=SimulatorUtil.getPositionVector(mc.thePlayer);
                        startMotion=SimulatorUtil.getMotionVector(mc.thePlayer);
                        try {
                            startInvulnerabilityFrames=SimulatorUtil.getRespawnInvulnerabilityTicks(SimulatorUtil.getPlayerMP(mc));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    instanciateRunners();
                }
                isRunning=true;
                mc.thePlayer.sendChatMessage("Started editor");
            }else {
                MinecraftTASSimulatorMod.logger.error("Editor is already running");
            }
        } else {
            MinecraftTASSimulatorMod.logger.error("Unable to start editor in multiplayer.");
        }
    }
    /**
     * Stops editor. Only works in single player mode.
     */
    public static void stop() {
        if (mc.isSingleplayer()) {
            if (isRunning) {
                isRunning=false;
                mc.thePlayer.sendChatMessage("Stopped editor");
            }else {
                MinecraftTASSimulatorMod.logger.error("Editor is not running");
            }
        } else {
            MinecraftTASSimulatorMod.logger.error("Unable to stop editor in multiplayer.");
        }
    }
    public static void onWorldUnload(WorldEvent.Unload event) {
        stop();
    }
    public static void selectTick(int tick) {
        selectedTick=tick;
    }
    public static void deselectTick() {
        selectedTick=-1;
    }
    public static boolean isSelectingTick() {
        return selectedTick!=-1;
    }
    public static int getSelectedTick() {
        return selectedTick;
    }
    public static void setStartPosition(Vec3 startPosition) {
        InputEditor.startPosition=startPosition;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartPosition() {
        return startPosition;
    }
    public static void setStartMotion(Vec3 startMotion) {
        InputEditor.startMotion=startMotion;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartMotion() {
        return startMotion;
    }
    public static void setStartInvulnerabilityFrames(int startInvulnerabilityFrames) {
        InputEditor.startInvulnerabilityFrames=startInvulnerabilityFrames;
        setupRunners();
        markEditedAt(-1);
    }
    public static int getStartInvulnerabilityFrames() {
        return startInvulnerabilityFrames;
    }
    public static void setStartGametype(GameType startGametype) {
        InputEditor.startGametype=startGametype;
        setupRunners();
        markEditedAt(-1);
    }
    public static GameType getStartGametype() {
        return startGametype;
    }
    public static void setMouseSensitivity(float mouseSensitivity) {
        InputEditor.mouseSensitivity=mouseSensitivity;
    }
    public static float getMouseSensitivity() {
        return mouseSensitivity;
    }
    public static void setMouseMaxSafeMovement(int mouseMaxSafeMovement) {
        InputEditor.mouseMaxSafeMovement=mouseMaxSafeMovement;
    }
    public static int getMouseMaxSafeMovement() {
        return mouseMaxSafeMovement;
    }
    public static void setInheritEffectsFromAllTicks(boolean inheritEffectsFromAllTicks) {
        InputEditor.inheritEffectsFromAllTicks=inheritEffectsFromAllTicks;
        setupPredictor();
    }
    public static boolean getInheritEffectsFromAllTicks() {
        return inheritEffectsFromAllTicks;
    }
    public static int getPredictionStartTick() {
        return predictionStartTick;
    }
    public static void setPredictionStartTick(int predictionStartTick) {
        InputEditor.predictionStartTick=predictionStartTick;
    }
    public static int getPredictionStartTickActual() {
        return predictionStartTickActual;
    }
    public static int getTickLength() {
        return tickLength;
    }
    public static void setTickLength(int tickLength) {
        InputEditor.tickLength=tickLength;
    }
    public static int getRerecords() {
        return rerecords;
    }
    public static int getPredictionRerecords() {
        return predictionRerecords;
    }
    public static int getTotalRerecords() {
        return totalRerecords;
    }
    /**
     * Creates and setup new instances of {@link Simulator} and {@link Predictor}.
     */
    public static void instanciateRunners() {
        instanciateSimulator();
        instanciatePredictor();
    }
    /**
     * Creates and setup a new instance of {@link Simulator}.
     */
    public static void instanciateSimulator() {
        if (simulator!=null) simulator.cleanup();
        simulator=new Simulator();
        setupSimulator();
    }
    public static Simulator getSimulator() {
        return simulator;
    }
    /**
     * Creates and setup a new instance of {@link Predictor}.
     */
    public static void instanciatePredictor() {
        if (predictor!=null) predictor.cleanup();
        predictor=new Predictor();
        setupPredictor();
    }
    public static Predictor getPredictor() {
        return predictor;
    }
    /**
     * Gets whether or not the input at the specified tick is null ("same as the last tick").
     * @param tick
     * @return boolean
     */
    public static boolean isInputNullAt(int tick) {
        return inputs.isNull(tick);
    }
    /**
     * Gets whether or not the input at the selected tick is null ("same as the last tick").
     * @return boolean
     */
    public static boolean isSelectedInputNull() {
        return isInputNullAt(selectedTick);
    }
    public static int getInputLength() {
        return inputs.size();
    }
    public static Input getInputAt(int tick) {
        return inputs.get(tick);
    }
    public static Input getSelectedInput() {
        return getInputAt(selectedTick);
    }
    public static void setInputAt(int tick,Input input) {
        inputs.set(tick,input);
        markEditedAt(tick);
    }
    public static void setSelectedInput(Input input) {
        setInputAt(selectedTick,input);
    }
    private static void markEditedAt(int tick) {
        if (!editedSinceLastSimulation) {
            rerecords++;
            simulationUntrustableFrom=tick;
        } else if (tick<simulationUntrustableFrom) {
            simulationUntrustableFrom=tick;
        }
        if (!editedSinceLastPrediction) {
            predictionRerecords++;
            predictionUntrustableFrom=tick;
        } else if (tick<predictionUntrustableFrom) {
            predictionUntrustableFrom=tick;
        }
        if (!editedSinceLastSimulation||!editedSinceLastPrediction) {
            totalRerecords++;
        }
        editedSinceLastSimulation=true;
        editedSinceLastPrediction=true;
    }
    /**
     * @param tick
     * @return Whether the saved results of the simulation can be trusted at the tick.
     */
    public static boolean canTrustSimulationAt(int tick) {
        return (!editedSinceLastSimulation||tick<simulationUntrustableFrom)&&tick<simulator.getComputedTicksN();
    }
    /**
     * @param tick
     * @return Whether the saved results of the prediction can be trusted at the tick.
     */
    public static boolean canTrustPredictionAt(int tick) {
        return !editedSinceLastPrediction||tick<predictionUntrustableFrom&&tick<predictor.getComputedTicksN();
    }
    /**
     * @return Number of save slots for player states.
     */
    public static int getPlayerStateSaveSlotNum() {
        return playerStateSaveSlots;
    }
    /**
     * @param slot The slot number to check.
     * @return Whether or not if the save slot exists.
     */
    public static boolean playerStatesSaveSlotExists(int slot) {
        return slot>=1&&slot<=playerStateSaveSlots;
    }
    /**
     * @param slot The slot number to get.
     * @return A saved list of player states for the slot.
     */
    public static ArrayList<SimulatedPlayerInfo> getSavedPlayerStates(int slot) {
        if (!playerStatesSaveSlotExists(slot)) return null;
        return savedPlayerStates.get(slot-1);
    }
    /**
     * Save current simulator result to a save slot.
     * @param slot The save slot to save to.
     */
    @SuppressWarnings("unchecked")
    public static void savePlayerStatesToSlot(int slot) {
        if (!playerStatesSaveSlotExists(slot)) return;
        savedPlayerStates.set(slot-1,(ArrayList<SimulatedPlayerInfo>)simulator.getPlayerStates().clone());
    }
    /**
     * Remove saved result from a save slot.
     * @param slot The save slot to erase.
     */
    public static void removePlayerStatesFromSlot(int slot) {
        if (!playerStatesSaveSlotExists(slot)) return;
        savedPlayerStates.set(slot-1,null);
    }
    private static float changeRotationFromPixels(float before,float mouseSensitivity,int pixels) {
        return (float)((double)before+(double)(pixels*SimulatorUtil.getRotationMult(mouseSensitivity))*0.15D);
    }
    /**
     * Calculate and save actual inputs to actualInputs.
     */
    private static void calculateAccurateInputs() {
        final double ROTATION_PER_PIXEL=SimulatorUtil.getRotationPerPixel(mouseSensitivity);
        float lastRotationYaw=0F;
        float lastRotationPitch=0F;
        actualInputs=new InputList();
        for (int i=0;i<getInputLength();i++) {
            Input input=inputs.get(i,false);
            if (input==null) {
                continue;
            } else {
                if (input.isRotationExact()) {
                    lastRotationYaw=(float)input.getRotationYaw();
                    lastRotationPitch=(float)input.getRotationPitch();
                    actualInputs.set(i,input);
                } else {
                    int yawDiff=(int)Math.round((input.getRotationYaw()-lastRotationYaw)/ROTATION_PER_PIXEL);
                    int pitchDiff=(int)Math.round((input.getRotationPitch()-lastRotationPitch)/ROTATION_PER_PIXEL);
                    if (Math.abs(yawDiff)>mouseMaxSafeMovement*100) { //NOTE: This is to prevent freezing when putting a massive angle
                        lastRotationYaw=(float)input.getRotationYaw();
                        yawDiff=0;
                    } else {
                        while (yawDiff>mouseMaxSafeMovement) {
                            lastRotationYaw=changeRotationFromPixels(lastRotationYaw,mouseSensitivity,mouseMaxSafeMovement);
                            yawDiff-=mouseMaxSafeMovement;
                        }
                        while (yawDiff<-mouseMaxSafeMovement) {
                            lastRotationYaw=changeRotationFromPixels(lastRotationYaw,mouseSensitivity,-mouseMaxSafeMovement);
                            yawDiff+=mouseMaxSafeMovement;
                        }
                        if (yawDiff!=0) lastRotationYaw=changeRotationFromPixels(lastRotationYaw,mouseSensitivity,yawDiff);
                    }
                    if (pitchDiff!=0) lastRotationPitch=changeRotationFromPixels(lastRotationPitch,mouseSensitivity,pitchDiff);
                    input.setRotationYaw(lastRotationYaw);
                    input.setRotationPitch(lastRotationPitch);
                    actualInputs.set(i,input);
                }
            }
        }
    }
    /**
     * Sends the inputs and configuration object to the runners.
     */
    public static void setupRunners() {
        setupSimulator();
        setupPredictor();
    }
    /**
     * Sends the inputs and configuration object to the simulator.
     */
    public static void setupSimulator() {
        simulator.setStartPosition(startPosition);
        simulator.setStartMotion(startMotion);
        simulator.setStartInvulnerabilityFrames(startInvulnerabilityFrames);
        simulator.setStartGametype(startGametype);
        simulator.setInputs(actualInputs);
    }
    /**
     * Sends the inputs, configuration object, and the simulator to the predictor.
     */
    public static void setupPredictor() {
        predictor.setStartPosition(startPosition);
        predictor.setStartMotion(startMotion);
        predictor.setInputs(actualInputs);
        predictor.setInheritEffectsFromAllTicks(inheritEffectsFromAllTicks);
    }
    /**
     * Returns whether or not the simulation is not null and is running.
     * @return boolean
     */
    public static boolean isSimulationRunning() {
        return simulator!=null&&simulator.isRunning();
    }
    /**
     * Starts simulation.
     */
    public static void startSimulation() {
        if (editedSinceLastSimulation) {
            calculateAccurateInputs();
            setupSimulator();
        }
        savedPosition=SimulatorUtil.getPositionVector(mc.thePlayer);
        savedRotationYaw=mc.thePlayer.rotationYaw;
        savedRotationPitch=mc.thePlayer.rotationPitch;
        savedGametype=mc.playerController.getCurrentGameType();
        simulator.setTarget(tickLength);
        simulator.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.thePlayer.setPositionAndUpdate(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord);
                mc.thePlayer.setPositionAndRotation(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord,(float)savedRotationYaw,(float)savedRotationPitch);
                mc.thePlayer.motionX=0;
                mc.thePlayer.motionY=0;
                mc.thePlayer.motionZ=0;
                mc.playerController.setGameType(savedGametype);
                mc.thePlayer.addChatMessage(new ChatComponentText("Finished simulation of length "+tickLength));
            }
        });
        simulator.setCallbackOnAbort(new Runnable() {
            public void run() {
                mc.thePlayer.setPositionAndUpdate(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord);
                mc.thePlayer.setPositionAndRotation(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord,(float)savedRotationYaw,(float)savedRotationPitch);
                mc.thePlayer.motionX=0;
                mc.thePlayer.motionY=0;
                mc.thePlayer.motionZ=0;
                mc.playerController.setGameType(savedGametype);
                mc.thePlayer.addChatMessage(new ChatComponentText("Aborted simulation"));
            }
        });
        simulator.start();
        editedSinceLastSimulation=false;
    }
    public static void abortSimulation() {
        simulator.abort();
    }
    /**
     * Returns whether or not the prediction is not null and is running.
     * @return boolean
     */
    public static boolean isPredictionRunning() {
        return predictor!=null&&predictor.isRunning();
    }
    /**
     * Starts prediction.
     */
    public static void startPrediction() {
        if (editedSinceLastPrediction) {
            calculateAccurateInputs();
            setupPredictor();
        }
        predictionStartTickActual=predictionStartTick;
        while (!canTrustSimulationAt(predictionStartTickActual-1)&&predictionStartTickActual>0) {
            predictionStartTickActual--;
        }
        if (predictionStartTickActual<0) predictionStartTickActual=0;
        predictor.setStartTick(predictionStartTickActual);
        predictor.setTarget(tickLength);
        predictor.loadPlayerInfo(simulator.getPlayerStates());
        predictor.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.thePlayer.addChatMessage(new ChatComponentText("Finished prediction from "+predictionStartTickActual+" to "+tickLength));
            }
        });
        predictor.setCallbackOnAbort(new Runnable() {
            public void run() {
                mc.thePlayer.addChatMessage(new ChatComponentText("Aborted prediction"));
            }
        });
        predictor.start();
        editedSinceLastPrediction=false;
    }
    public static void abortPrediction() {
        predictor.abort();
    }
    final static int FILE_FORMAT_VERSION=2;
    /*
     * File format:
     * Empty lines or lines that start with "%" are ignored.
     * Change detect mode with !<mode>, case ignored.
     * property mode:
     * * startPosition=<x>,<y>,<z>
     * * * Sets starting position to (x,y,z). If not present, default to (0,0,0).
     * * startMotion=<x>,<y>,<z>
     * * * Sets starting velocity to (x,y,z). If not present, default to (0,0,0).
     * * startInvulnerabilityFrames=<x>,<y>,<z>
     * * * Sets starting invulnerability ticks to (x,y,z). If not present, default to (0,0,0).
     * * startGametype=<str>
     * * * Sets starting game mode.
     * * mouseSensitivity=<f>
     * * * Sets the mouse sensitivity as written in config.txt. If not present, default to 0.5 (100%).
     * * mouseMaxSafeMovement=<n>
     * * * Sets the maximum pixels of movement done with 1 input. If more pixels are traveled, they are simulated to be broken up into multiple inputs, first are maxed. If not present, default to 900.
     * * tickLength=<n>
     * * * Sets length of simulation to n. If not present, default to 0.
     * * rerecords=<n>
     * * * Sets the simulation rerecord count to n. If not present, default to 0.
     * * predictionRerecords=<n>
     * * * Sets the prediction rerecord count to n. If not present, default to 0.
     * * totalRerecords=<n>
     * * * Sets the total rerecord count to n. If not present, default to 0.
     * * fileFormatVersion=<n>
     * * * Indicates that the file version is <n>.
     * * editorVersion=<str>
     * * * Indicates that the file was created with editor version <str>.
     * input mode:
     * * Lines are of format <tick#>|<buttons>|<yaw>,<pitch>|<flags>.
     * * tick# or buttons must not contain "|".
     * * Leading 0s in tick# are ignored.
     * * If some ticks are missing, they are filled in with same inputs.
     * * If tick# is of "#"s, then buttons represent the columns headers for button table.
     * * Buttons:
     * * * "W" or "w" - Move forward
     * * * "A" or "a" - Strafe left
     * * * "S" or "s" - Move backward
     * * * "D" or "d" - Strafe right
     * * * "_" - Jump
     * * * "+" - Sneak
     * * * "^" - Sprint
     * * Buttons are seen pressed if the corresponding position is not " " or ".".
     * * Flags:
     * * * There may be multiple flags for a line. A flag may be followed by "{" to provide additional options, which is closed by "}".
     * * * "=" - Yaw and pitch is exact at this point, otherwise approximate using the shortest path by pixel.
     * * * "m" - Mouse clicks should be simulated. There may be multiple inputs in a tick. The contents are the string of the letters:
     * * * * "L" - Press down LL
     * * * * "l" - Release LC
     * * * * "R" - Press down RC
     * * * * "r" - Release LC
     * * * ";" - Reserved to do nothing.
     */
    /**
     * Opens {@link JFileChooser} dialog and saves inputs to the selected file.
     */
    public static void saveFile() {
        JFileChooser fileChooser=new JFileChooser();
        int option=fileChooser.showSaveDialog(null);
        if (option==JFileChooser.APPROVE_OPTION) {
            saveFile(fileChooser.getSelectedFile());
        } else {
            MinecraftTASSimulatorMod.logger.info("Cancelled action.");
        }
    }
    /**
     * @param file File to be saved
     */
    public static void saveFile(File file) {
        if (file==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("File object was null.");
            return;
        }
        if (!wasEverInit) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Editor must be run at least once to save.");
            return;
        }
        try {
            if (file.exists()) {
                int option=JOptionPane.showConfirmDialog(null,null,"The file already exists. Do you want to overwrite?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (option!=JOptionPane.YES_OPTION) {
                    MinecraftTASSimulatorMod.logger.info("Cancelled action.");
                    return;
                }
            } else {
                file.createNewFile();
            }
            if (!file.canWrite()) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog("File cannot be written.");
                return;
            }
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,false));
            final int tickNumLen=String.valueOf(inputs.size()-1).length();
            writer.write("!property");
            writer.newLine();
            writer.write("startPosition="+SimulatorUtil.stringifyVec3(startPosition));
            writer.newLine();
            writer.write("startMotion="+SimulatorUtil.stringifyVec3(startMotion));
            writer.newLine();
            writer.write("startInvulnerabilityFrames="+startInvulnerabilityFrames);
            writer.newLine();
            writer.write("startGametype="+SimulatorUtil.stringifyGameType(startGametype));
            writer.newLine();
            writer.write("mouseSensitivity="+mouseSensitivity);
            writer.newLine();
            writer.write("mouseMaxSafeMovement="+mouseMaxSafeMovement);
            writer.newLine();
            writer.write("tickLength="+tickLength);
            writer.newLine();
            writer.write("rerecords="+rerecords);
            writer.newLine();
            writer.write("predictionRerecords="+predictionRerecords);
            writer.newLine();
            writer.write("totalRerecords="+totalRerecords);
            writer.newLine();
            writer.write("fileFormatVersion="+FILE_FORMAT_VERSION);
            writer.newLine();
            writer.write("editorVersion="+MinecraftTASSimulatorMod.VERSION);
            writer.newLine();
            writer.newLine();
            writer.write("!input");
            writer.newLine();
            writer.write(new String(new char[tickNumLen]).replace("\0","#")+"|WASD_+^|Yaw          ,Pitch        |Flags");
            writer.newLine();
            for (int tick=0;tick<inputs.size();tick++) {
                if (inputs.isNull(tick)) continue;
                Input input=inputs.get(tick);
                String flags="";
                if (input.isRotationExact()) flags+="=";
                if (input.getMouseButtonInputs().size()>0) flags+="m{"+SimulatorUtil.stringifyMouseButtonInputs(input)+"}";
                writer.write(String.format("%"+tickNumLen+"d|",tick)+
                        SimulatorUtil.stringifyKeys(input)+"|"+
                        String.format("%13.8f,%13.8f",input.getRotationYaw(),input.getRotationPitch())+"|"+
                        flags);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            mc.thePlayer.sendChatMessage(EnumChatFormatting.RED+"Failed to write to file.");
            e.printStackTrace();
            return;
        }
    }
    /**
     * Opens {@link JFileChooser} dialog and loads inputs from the selected file.
     */
    public static void loadFile() {
        JFileChooser fileChooser=new JFileChooser();
        int option=fileChooser.showSaveDialog(null);
        if (option==JFileChooser.APPROVE_OPTION) {
            loadFile(fileChooser.getSelectedFile());
        } else {
            MinecraftTASSimulatorMod.logger.info("Cancelled action.");
        }
    }
    /**
     * @param file File to be loaded
     */
    public static void loadFile(File file) {
        if (file==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("File object was null.");
            return;
        }
        int lineN=0;
        try {
            if (!file.exists()) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog("File does not exist.");
                return;
            }
            if (!file.canRead()) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog("File cannot be read.");
                return;
            }
            Vec3 newStartPosition=new Vec3(0,0,0);
            Vec3 newStartMotion=new Vec3(0,0,0);
            int newStartInvulnerabilityFrames=0;
            GameType newStartGametype=GameType.NOT_SET;
            float newMouseSensitivity=0.5F;
            int newMouseMaxSafeMovement=900;
            int newTickLength=0;
            int newRerecords=0;
            int newPredictionRerecords=0;
            int newTotalRerecords=0;
            int fileFormatVersionIndicated=0;
            String editorVersionIndicated="";
            InputList newInputs=new InputList();
            Scanner reader=new Scanner(new BufferedReader(new FileReader(file)));
            final byte MODE_INPUT=0;
            final byte MODE_PROPERTY=1;
            byte mode=MODE_INPUT;
            char[] buttonColumns=null;
            boolean readFailed=false;
            while (reader.hasNext()) {
                String line=reader.nextLine();
                lineN++;
                if (line.length()==0||line.charAt(0)=='%') {
                    continue;
                } else if (line.charAt(0)=='!') {
                    String modeStr=line.substring(1);
                    if (modeStr.equalsIgnoreCase("input")) mode=MODE_INPUT;
                    else if (modeStr.equalsIgnoreCase("property")) mode=MODE_PROPERTY;
                    else {
                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Unexpected mode '"+modeStr+"' at line "+lineN);
                    }
                } else if (mode==MODE_INPUT) {
                    String[] args=line.split("\\|");
                    String[] argsNoSpace=line.replace(" ","").split("\\|");
                    if (args.length<2) {
                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Not enough argument in input mode at line "+lineN);
                        readFailed=true;
                        break;
                    }
                    if (StringUtils.isNumeric(argsNoSpace[0])) {
                        if (args.length<3) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Not enough argument in input mode at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        Input input=new Input();
                        int tick=Integer.valueOf(argsNoSpace[0]);
                        if (buttonColumns.length!=args[1].length()) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Unmatched table width at line "+lineN);
                        }
                        for (int coli=0;coli<args[1].length()&&coli<buttonColumns.length;coli++) {
                            char button=buttonColumns[coli];
                            boolean isButtonPressed=args[1].charAt(coli)!=' '&&args[1].charAt(coli)!='.';
                            if (button=='W'||button=='w') {
                                input.setKeyForward(isButtonPressed);
                            } else if (button=='A'||button=='a') {
                                input.setKeyLeft(isButtonPressed);
                            } else if (button=='S'||button=='s') {
                                input.setKeyBackward(isButtonPressed);
                            } else if (button=='D'||button=='d') {
                                input.setKeyRight(isButtonPressed);
                            } else if (button=='_') {
                                input.setKeyJump(isButtonPressed);
                            } else if (button=='+') {
                                input.setKeySneak(isButtonPressed);
                            } else if (button=='^') {
                                input.setKeySprint(isButtonPressed);
                            }
                        }
                        try {
                            String[] cameraAngleStrs=argsNoSpace[2].split(",");
                            input.setRotationYaw(Float.valueOf(cameraAngleStrs[0]));
                            input.setRotationPitch(Float.valueOf(cameraAngleStrs[1]));
                        } catch (Exception e) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected floats");
                            readFailed=true;
                            break;
                        }
                        if (args.length>3) { //Required check for file format version < 2
                            for (int i=0;!readFailed&&i<args[3].length();i++) {
                                char flag=args[3].charAt(i);
                                String param;
                                if (i+1>=args[3].length()||args[3].charAt(i+1)!='{') {
                                    param=null;
                                } else {
                                    int closingIndex=args[3].indexOf('}');
                                    if (closingIndex==-1) {
                                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Unclosed {");
                                        readFailed=true;
                                        break;
                                    }
                                    param=args[3].substring(i+2,closingIndex);
                                    i=closingIndex;
                                }
                                switch (flag) {
                                case '=':
                                    if (param==null) input.setRotationExact(true);
                                    else {
                                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": = flag excepted no parameters");
                                        readFailed=true;
                                        break;
                                    }
                                    break;
                                case 'm':
                                    if (param==null) {
                                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": m flag expected a string of inputs");
                                        readFailed=true;
                                        break;
                                    } else {
                                        if (param.length()>50) MinecraftTASSimulatorMod.outputWarningToChatAndLog("Warning at line "+lineN+": Recieved inputs too long");
                                        input.setMouseButtonInputs(SimulatorUtil.parseMouseButtonInputs(param));
                                    }
                                    break;
                                case ';':
                                    break;
                                default:
                                    MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Unknown flag "+flag);
                                    readFailed=true;
                                    break;
                                }
                            }
                            if (readFailed) break;
                        }
                        newInputs.set(tick,input);
                    } else if (argsNoSpace[0].matches("#+")) {
                        buttonColumns=args[1].toCharArray();
                    } else {
                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Malformed at line "+lineN);
                        readFailed=true;
                        break;
                    }
                } else if (mode==MODE_PROPERTY) {
                    String[] args=line.split("=",2);
                    String[] argsNoSpace=line.replace(" ","").split("=",2);
                    if (args.length<2) {
                        MinecraftTASSimulatorMod.outputErrorToChatAndLog("Not enough argument in property mode at line "+lineN);
                        readFailed=true;
                        break;
                    }
                    String key=argsNoSpace[0];
                    String value=argsNoSpace[1];
                    if (key.equals("startPosition")) {
                        if (!SimulatorUtil.isStringParsableAsVec3(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Coordinate cannot be parsed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newStartPosition=SimulatorUtil.stringToVec3(value);
                    } else if (key.equals("startMotion")) {
                        if (!SimulatorUtil.isStringParsableAsVec3(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Coordinate cannot be parsed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newStartMotion=SimulatorUtil.stringToVec3(value);
                    } else if (key.equals("startInvulnerabilityFrames")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newStartInvulnerabilityFrames=Integer.valueOf(value);
                    } else if (key.equals("startGametype")) {
                        newStartGametype=SimulatorUtil.toGameType(value);
                    } else if (key.equals("mouseSensitivity")) {
                        try {
                            mouseSensitivity=Float.valueOf(value);
                        } catch(NumberFormatException e) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected a float");
                            readFailed=true;
                            break;
                        }
                    } else if (key.equals("mouseMaxSafeMovement")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newMouseMaxSafeMovement=Integer.valueOf(value);
                    } else if (key.equals("tickLength")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newTickLength=Integer.valueOf(value);
                    } else if (key.equals("rerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newRerecords=Integer.valueOf(value);
                    } else if (key.equals("predictionRerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newPredictionRerecords=Integer.valueOf(value);
                    } else if (key.equals("totalRerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        newTotalRerecords=Integer.valueOf(value);
                    } else if (key.equals("fileFormatVersion")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Parse failed at line "+lineN+": Expected an integer");
                            readFailed=true;
                            break;
                        }
                        fileFormatVersionIndicated=Integer.valueOf(value);
                    } else if (key.equals("editorVersion")) {
                        editorVersionIndicated=value;
                    }
                }
            }
            reader.close();
            if (!readFailed) {
                if (fileFormatVersionIndicated==0||fileFormatVersionIndicated>FILE_FORMAT_VERSION) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"File format version was not specified or later than the editor handles (Given "+fileFormatVersionIndicated+", expected up to "+FILE_FORMAT_VERSION+"). Simulation may be desynced."));
                }
                if (!editorVersionIndicated.equals(MinecraftTASSimulatorMod.VERSION)) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"Editor version used was not specified or is different (Given "+editorVersionIndicated+", using "+MinecraftTASSimulatorMod.VERSION+"). Simulation may be desynced."));
                }
                if (!isRunning) start();
                startPosition=newStartPosition;
                startMotion=newStartMotion;
                startInvulnerabilityFrames=newStartInvulnerabilityFrames;
                startGametype=newStartGametype;
                mouseSensitivity=newMouseSensitivity;
                mouseMaxSafeMovement=newMouseMaxSafeMovement;
                tickLength=newTickLength;
                rerecords=newRerecords;
                predictionRerecords=newPredictionRerecords;
                totalRerecords=newTotalRerecords;
                inputs=newInputs;
                calculateAccurateInputs();
                setupRunners();
                editedSinceLastSimulation=false;
                editedSinceLastPrediction=false;
            }
        } catch (IOException e) {
            mc.thePlayer.sendChatMessage(EnumChatFormatting.RED+"Failed to read from file. Error occurred at line "+lineN);
            e.printStackTrace();
            return;
        }
    }
}
