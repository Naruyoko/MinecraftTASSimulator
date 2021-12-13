package com.github.naruyoko.minecrafttassimulator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
    private static InputSideMenu inputSideMenu=new InputSideMenu();
    private static InputList actualInputs=null;
    private static boolean isRunning=false;
    private static int predictionStartTick;
    private static int predictionStartTickActual;
    private static int selectedTick;
    private static boolean inheritEffectsFromAllTicks;
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
        inputs=new InputList();
        inputSideMenu.setStartPosition(new Vec3(0,0,0));
        inputSideMenu.setStartMotion(new Vec3(0,0,0));
        inputSideMenu.setStartInvulnerabilityFrames(0);
        inputSideMenu.setStartGametype(GameType.NOT_SET);
        inputSideMenu.setMouseSensitivity(0.5F);
        inputSideMenu.setMouseMaxSafeMovement(900);
        inputSideMenu.setTickLength(0);
        inputSideMenu.setRerecords(0);
        inputSideMenu.setPredictionRerecords(0);
        inputSideMenu.setTotalRerecords(0);
        inheritEffectsFromAllTicks=true;
        actualInputs=new InputList();
        selectedTick=-1;
        simulator=null;
        predictor=null;
        predictionStartTick=0;
        predictionStartTickActual=0;
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
                    instanciateRunners();
                    if (mc.thePlayer!=null) {
                        try {
                            setStartPosition(SimulatorUtil.getPositionVector(mc.thePlayer));
                            setStartMotion(SimulatorUtil.getMotionVector(mc.thePlayer));
                            setStartInvulnerabilityFrames(SimulatorUtil.getRespawnInvulnerabilityTicks(SimulatorUtil.getPlayerMP(mc)));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
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
        inputSideMenu.startPosition=startPosition;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartPosition() {
        return inputSideMenu.startPosition;
    }
    public static void setStartMotion(Vec3 startMotion) {
        inputSideMenu.startMotion=startMotion;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartMotion() {
        return inputSideMenu.startMotion;
    }
    public static void setStartInvulnerabilityFrames(int startInvulnerabilityFrames) {
        inputSideMenu.startInvulnerabilityFrames=startInvulnerabilityFrames;
        setupRunners();
        markEditedAt(-1);
    }
    public static int getStartInvulnerabilityFrames() {
        return inputSideMenu.startInvulnerabilityFrames;
    }
    public static void setStartGametype(GameType startGametype) {
        inputSideMenu.startGametype=startGametype;
        setupRunners();
        markEditedAt(-1);
    }
    public static GameType getStartGametype() {
        return inputSideMenu.startGametype;
    }
    public static void setMouseSensitivity(float mouseSensitivity) {
        inputSideMenu.mouseSensitivity=mouseSensitivity;
    }
    public static float getMouseSensitivity() {
        return inputSideMenu.mouseSensitivity;
    }
    public static void setMouseMaxSafeMovement(int mouseMaxSafeMovement) {
        inputSideMenu.mouseMaxSafeMovement=mouseMaxSafeMovement;
    }
    public static int getMouseMaxSafeMovement() {
        return inputSideMenu.mouseMaxSafeMovement;
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
        return inputSideMenu.tickLength;
    }
    public static void setTickLength(int tickLength) {
        inputSideMenu.tickLength=tickLength;
    }
    public static int getRerecords() {
        return inputSideMenu.rerecords;
    }
    public static int getPredictionRerecords() {
        return inputSideMenu.predictionRerecords;
    }
    public static int getTotalRerecords() {
        return inputSideMenu.totalRerecords;
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
            inputSideMenu.rerecords++;
            simulationUntrustableFrom=tick;
        } else if (tick<simulationUntrustableFrom) {
            simulationUntrustableFrom=tick;
        }
        if (!editedSinceLastPrediction) {
            inputSideMenu.predictionRerecords++;
            predictionUntrustableFrom=tick;
        } else if (tick<predictionUntrustableFrom) {
            predictionUntrustableFrom=tick;
        }
        if (!editedSinceLastSimulation||!editedSinceLastPrediction) {
            inputSideMenu.totalRerecords++;
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
        float mouseSensitivity=getMouseSensitivity();
        int mouseMaxSafeMovement=getMouseMaxSafeMovement();
        final double ROTATION_PER_PIXEL=SimulatorUtil.getRotationPerPixel(mouseSensitivity);
        float lastRotationYaw=0F;
        float lastRotationPitch=0F;
        actualInputs=new InputList();
        int inputLength=getInputLength();
        for (int i=0;i<inputLength;i++) {
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
        simulator.setInputs(actualInputs,inputSideMenu);
    }
    /**
     * Sends the inputs, configuration object, and the simulator to the predictor.
     */
    public static void setupPredictor() {
        predictor.setInputs(actualInputs,inputSideMenu);
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
        try {
            savedPosition=SimulatorUtil.getPositionVector(mc.thePlayer);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        savedRotationYaw=mc.thePlayer.rotationYaw;
        savedRotationPitch=mc.thePlayer.rotationPitch;
        savedGametype=mc.playerController.getCurrentGameType();
        simulator.setTarget(getTickLength());
        simulator.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.thePlayer.setPositionAndUpdate(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord);
                mc.thePlayer.setPositionAndRotation(savedPosition.xCoord,savedPosition.yCoord,savedPosition.zCoord,(float)savedRotationYaw,(float)savedRotationPitch);
                mc.thePlayer.motionX=0;
                mc.thePlayer.motionY=0;
                mc.thePlayer.motionZ=0;
                mc.playerController.setGameType(savedGametype);
                mc.thePlayer.addChatMessage(new ChatComponentText("Finished simulation of length "+getTickLength()));
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
        predictor.setTarget(getTickLength());
        predictor.loadPlayerInfo(simulator.getPlayerStates());
        predictor.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.thePlayer.addChatMessage(new ChatComponentText("Finished prediction from "+predictionStartTickActual+" to "+getTickLength()));
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
    final static String DEFAULT_FILE_FORMAT=".mcsim";
    /**
     * Opens {@link JFileChooser} dialog and saves inputs to the selected file.
     */
    public static void saveFile() {
        saveFile(DEFAULT_FILE_FORMAT);
    }
    /**
     * Opens {@link JFileChooser} dialog and saves inputs to the selected file.
     * @param fileExtension The file extension that represents the file format
     */
    public static void saveFile(String fileExtension) {
        if (!wasEverInit) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Editor must be run at least once to save.");
            return;
        }
        if (fileExtension==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format was not given.");
            return;
        }
        if (!InputFileWorkerEnum.isWritableExtension(fileExtension)) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format is not supported for saving.");
            return;
        }
        JFileChooser fileChooser=new JFileChooser();
        int option=fileChooser.showSaveDialog(null);
        if (option==JFileChooser.APPROVE_OPTION) {
            saveFile(fileChooser.getSelectedFile(),fileExtension);
        } else {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Action cancelled.");
        }
    }
    /**
     * @param file File to be saved
     */
    public static void saveFile(File file) {
        saveFile(file,DEFAULT_FILE_FORMAT);
    }
    /**
     * @param file File to be saved
     * @param fileExtension The file extension that represents the file format
     */
    public static void saveFile(File file,String fileExtension) {
        if (!wasEverInit) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Editor must be run at least once to save.");
            return;
        }
        if (fileExtension==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format was not given.");
            return;
        }
        if (!InputFileWorkerEnum.isWritableExtension(fileExtension)) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format is not supported for saving.");
            return;
        }
        if (file.exists()) {
            int option=JOptionPane.showConfirmDialog(null,null,"The file already exists. Do you want to overwrite?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if (option!=JOptionPane.YES_OPTION) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog("Action cancelled.");
                return;
            }
        }
        try {
            InputFileInformation inputFileInformation=new InputFileInformation(inputs,inputSideMenu,FILE_FORMAT_VERSION,MinecraftTASSimulatorMod.VERSION);
            InputFileWriter writer=InputFileWorkerEnum.newWriter(fileExtension, file, inputFileInformation);
            writer.write();
        } catch (IllegalArgumentException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Opens {@link JFileChooser} dialog and loads inputs from the selected file.
     */
    public static void loadFile() {
        loadFile(DEFAULT_FILE_FORMAT);
    }
    /**
     * Opens {@link JFileChooser} dialog and loads inputs from the selected file.
     * @param fileExtension The file extension that represents the file format
     */
    public static void loadFile(String fileExtension) {
        if (fileExtension==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format was not given.");
            return;
        }
        if (!InputFileWorkerEnum.isReadableExtension(fileExtension)) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format is not supported for loading.");
            return;
        }
        JFileChooser fileChooser=new JFileChooser();
        int option=fileChooser.showOpenDialog(null);
        if (option==JFileChooser.APPROVE_OPTION) {
            loadFile(fileChooser.getSelectedFile(),fileExtension);
        } else {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("Action cancelled.");
        }
    }
    /**
     * @param file File to be loaded
     */
    public static void loadFile(File file) {
        loadFile(file,DEFAULT_FILE_FORMAT);
    }
    /**
     * @param file File to be loaded
     * @param fileExtension The file extension that represents the file format
     */
    public static void loadFile(File file,String fileExtension) {
        if (fileExtension==null) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format was not given.");
            return;
        }
        if (!InputFileWorkerEnum.isReadableExtension(fileExtension)) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog("The file format is not supported for loading.");
            return;
        }
        try {
            InputFileReader reader = InputFileWorkerEnum.newReader(fileExtension, file);
            InputFileReader.ReadReturnValue readReturnValue=reader.read();
            for (InputFileReader.MessageWithLevelAndLineNum message:readReturnValue.messages) {
                switch (message.level) {
                case INFO:
                    MinecraftTASSimulatorMod.outputInfoToChatAndLog(String.format("Info at line %d: %s",message.lineN,message.message));
                    break;
                case WARN:
                    MinecraftTASSimulatorMod.outputWarningToChatAndLog(String.format("Warning at line %d: %s",message.lineN,message.message));
                    break;
                case ERROR:
                    MinecraftTASSimulatorMod.outputErrorToChatAndLog(String.format("Error at line %d: %s",message.lineN,message.message));
                    break;
                default:
                    MinecraftTASSimulatorMod.logger.log(message.level,String.format("Message at line %d: %s",message.lineN,message.message));
                    break;
                }
            }
            if (readReturnValue instanceof InputFileReader.ReadSuccess) {
                InputFileInformation inputFileInformation=((InputFileReader.ReadSuccess) readReturnValue).inputFileInformation;
                if (inputFileInformation.isExternalFormat()) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"Read from an external file format. Some features may be incompatible."));
                } else {
                    int fileFormatVersionIndicated=inputFileInformation.getFileFormatVersion();
                    if (fileFormatVersionIndicated==0||fileFormatVersionIndicated>FILE_FORMAT_VERSION) {
                        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"File format version was not specified or later than the editor handles (Given "+fileFormatVersionIndicated+", expected up to "+FILE_FORMAT_VERSION+"). Simulation may be desynced."));
                    }
                    String editorVersionIndicated=inputFileInformation.getEditorVersion();
                    if (!editorVersionIndicated.equals(MinecraftTASSimulatorMod.VERSION)) {
                        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"Editor version used was not specified or is different (Given "+editorVersionIndicated+", using "+MinecraftTASSimulatorMod.VERSION+"). Simulation may be desynced."));
                    }
                }
                if (!isRunning) start();
                inputs=inputFileInformation.getInputs();
                inputSideMenu=inputFileInformation.getInputSideMenu();
                calculateAccurateInputs();
                setupRunners();
                editedSinceLastSimulation=false;
                editedSinceLastPrediction=false;
            } else if (readReturnValue instanceof InputFileReader.ReadFailure) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog(String.format("Read failed at line %d: %s",((InputFileReader.ReadFailure) readReturnValue).lineN,((InputFileReader.ReadFailure) readReturnValue).message));
            } else if (readReturnValue instanceof InputFileReader.ReadException) {
                MinecraftTASSimulatorMod.outputErrorToChatAndLog(String.format("Error raised at line %d: %s",((InputFileReader.ReadException) readReturnValue).lineN,((InputFileReader.ReadException) readReturnValue).exception.getMessage()));
            }
        } catch (NoSuchMethodException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            MinecraftTASSimulatorMod.outputErrorToChatAndLog(e.getMessage());
            e.printStackTrace();
        }
    }
}
