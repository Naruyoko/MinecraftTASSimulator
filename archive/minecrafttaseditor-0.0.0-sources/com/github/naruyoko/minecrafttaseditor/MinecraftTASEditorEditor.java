package com.github.naruyoko.minecrafttaseditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.WorldEvent;

public class MinecraftTASEditorEditor {
    private static Minecraft mc=Minecraft.func_71410_x();
    private static MinecraftTASEditorSimulator simulator=null;
    private static MinecraftTASEditorPredictor predictor=null;
    private static MinecraftTASEditorInputList inputs=null;
    private static boolean isRunning=false;
    private static int predictionStartTick;
    private static int predictionStartTickActual;
    private static int tickLength;
    private static int selectedTick;
    private static Vec3 startPosition;
    private static Vec3 startMotion;
    private static int startInvulnerabilityFrames;
    private static boolean inheritEffectsFromAllTicks;
    private static int rerecords;
    private static int predictionRerecords;
    private static int totalRerecords;
    private static boolean wasEverInit;
    private static Vec3 savedPosition;
    private static double savedRotationYaw;
    private static double savedRotationPitch;
    private static boolean editedSinceLastSimulation;
    private static boolean editedSinceLastPrediction;
    private static int simulationUntrustableFrom;
    private static int predictionUntrustableFrom;
    public static void init() {
        wasEverInit=false;
        startPosition=new Vec3(0,0,0);
        startMotion=new Vec3(0,0,0);
        startInvulnerabilityFrames=0;
        inheritEffectsFromAllTicks=true;
        simulator=null;
        predictor=null;
        selectedTick=-1;
        inputs=new MinecraftTASEditorInputList();
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
    }
    public static boolean isRunning() {
        return isRunning;
    }
    /**
     * Starts editor. Only works in single player mode.
     */
    public static void start() {
        if (mc.func_71356_B()) {
            if (!isRunning) {
                if (!wasEverInit) {
                    wasEverInit=true;
                    if (mc.field_71439_g!=null) {
                        startPosition=MinecraftTASEditorUtil.getPositionVector(mc.field_71439_g);
                        startMotion=MinecraftTASEditorUtil.getMotionVector(mc.field_71439_g);
                        try {
                            startInvulnerabilityFrames=MinecraftTASEditorUtil.getRespawnInvulnerabilityTicks(MinecraftTASEditorUtil.getPlayerMP(mc));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    instanciateRunners();
                }
                isRunning=true;
                mc.field_71439_g.func_71165_d("Started editor");
            }else {
                MinecraftTASEditorMod.logger.error("Editor is already running");
            }
        } else {
            MinecraftTASEditorMod.logger.error("Unable to start editor in multiplayer.");
        }
    }
    /**
     * Stops editor. Only works in single player mode.
     */
    public static void stop() {
        if (mc.func_71356_B()) {
            if (isRunning) {
                isRunning=false;
                mc.field_71439_g.func_71165_d("Stopped editor");
            }else {
                MinecraftTASEditorMod.logger.error("Editor is not running");
            }
        } else {
            MinecraftTASEditorMod.logger.error("Unable to stop editor in multiplayer.");
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
        MinecraftTASEditorEditor.startPosition=startPosition;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartPosition() {
        return startPosition;
    }
    public static void setStartMotion(Vec3 startMotion) {
        MinecraftTASEditorEditor.startMotion=startMotion;
        setupRunners();
        markEditedAt(-1);
    }
    public static Vec3 getStartMotion() {
        return startMotion;
    }
    public static void setStartInvulnerabilityFrames(int startInvulnerabilityFrames) {
        MinecraftTASEditorEditor.startInvulnerabilityFrames=startInvulnerabilityFrames;
        setupRunners();
        markEditedAt(-1);
    }
    public static int getStartInvulnerabilityFrames() {
        return startInvulnerabilityFrames;
    }
    public static void setInheritEffectsFromAllTicks(boolean inheritEffectsFromAllTicks) {
        MinecraftTASEditorEditor.inheritEffectsFromAllTicks=inheritEffectsFromAllTicks;
        setupPredictor();
    }
    public static boolean getInheritEffectsFromAllTicks() {
        return inheritEffectsFromAllTicks;
    }
    public static int getPredictionStartTick() {
        return predictionStartTick;
    }
    public static void setPredictionStartTick(int predictionStartTick) {
        MinecraftTASEditorEditor.predictionStartTick=predictionStartTick;
    }
    public static int getPredictionStartTickActual() {
        return predictionStartTickActual;
    }
    public static int getTickLength() {
        return tickLength;
    }
    public static void setTickLength(int tickLength) {
        MinecraftTASEditorEditor.tickLength=tickLength;
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
     * Creates and setup new instances of {@link MinecraftTASEditorSimulator} and {@link MinecraftTASEditorPredictor}.
     */
    public static void instanciateRunners() {
        instanciateSimulator();
        instanciatePredictor();
    }
    /**
     * Creates and setup a new instance of {@link MinecraftTASEditorSimulator}.
     */
    public static void instanciateSimulator() {
        if (simulator!=null) simulator.cleanup();
        simulator=new MinecraftTASEditorSimulator();
        setupSimulator();
    }
    public static MinecraftTASEditorSimulator getSimulator() {
        return simulator;
    }
    /**
     * Creates and setup a new instance of {@link MinecraftTASEditorPredictor}.
     */
    public static void instanciatePredictor() {
        if (predictor!=null) predictor.cleanup();
        predictor=new MinecraftTASEditorPredictor();
        setupPredictor();
    }
    public static MinecraftTASEditorPredictor getPredictor() {
        return predictor;
    }
    /**
     * Gets whether or not the input at the specified tick is null ("same as the last tick").
     * @param tick
     * @return boolean
     */
    public static boolean isNullAt(int tick) {
        return inputs.isNull(tick);
    }
    /**
     * Gets whether or not the input at the selected tick is null ("same as the last tick").
     * @return boolean
     */
    public static boolean isSelectedInputNull() {
        return isNullAt(selectedTick);
    }
    public static int getInputLength() {
        return inputs.size();
    }
    public static MinecraftTASEditorInput getInputAt(int tick) {
        return inputs.get(tick);
    }
    public static MinecraftTASEditorInput getSelectedInput() {
        return getInputAt(selectedTick);
    }
    public static void setInputAt(int tick,MinecraftTASEditorInput input) {
        inputs.set(tick,input);
        markEditedAt(tick);
    }
    public static void setSelectedInput(MinecraftTASEditorInput input) {
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
        return !editedSinceLastSimulation||tick<simulationUntrustableFrom;
    }
    /**
     * @param tick
     * @return Whether the saved results of the prediction can be trusted at the tick.
     */
    public static boolean canTrustPredictionAt(int tick) {
        return !editedSinceLastPrediction||tick<predictionUntrustableFrom;
    }
    /**
     * Sends the inputs and configuration object to the simulator.
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
        simulator.setInputs(inputs);
    }
    /**
     * Sends the inputs, configuration object, and the simulator to the predictor.
     */
    public static void setupPredictor() {
        predictor.setStartPosition(startPosition);
        predictor.setStartMotion(startMotion);
        predictor.setInputs(inputs);
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
        savedPosition=MinecraftTASEditorUtil.getPositionVector(mc.field_71439_g);
        savedRotationYaw=mc.field_71439_g.field_70177_z;
        savedRotationPitch=mc.field_71439_g.field_70125_A;
        simulator.setTarget(tickLength);
        simulator.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.field_71439_g.func_70080_a(savedPosition.field_72450_a,savedPosition.field_72448_b,savedPosition.field_72449_c,(float)savedRotationYaw,(float)savedRotationPitch);
                mc.field_71439_g.field_70159_w=0;
                mc.field_71439_g.field_70181_x=0;
                mc.field_71439_g.field_70179_y=0;
                mc.field_71439_g.func_145747_a(new ChatComponentText("Finished simulation of length "+tickLength));
            }
        });
        simulator.setCallbackOnAbort(new Runnable() {
            public void run() {
                mc.field_71439_g.func_70080_a(savedPosition.field_72450_a,savedPosition.field_72448_b,savedPosition.field_72449_c,(float)savedRotationYaw,(float)savedRotationPitch);
                mc.field_71439_g.field_70159_w=0;
                mc.field_71439_g.field_70181_x=0;
                mc.field_71439_g.field_70179_y=0;
                mc.field_71439_g.func_145747_a(new ChatComponentText("Aborted simulation"));
            }
        });
        simulator.start();
        editedSinceLastSimulation=false;
        editedSinceLastPrediction=false;
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
        predictionStartTickActual=predictionStartTick;
        while (!canTrustSimulationAt(predictionStartTickActual)&&predictionStartTickActual>0) {
            predictionStartTickActual--;
        }
        if (predictionStartTickActual<0) predictionStartTickActual=0;
        predictor.setStartTick(predictionStartTickActual);
        predictor.setTarget(tickLength);
        predictor.loadPlayerInfo(simulator.getPlayerStates());
        predictor.setCallbackOnFinish(new Runnable() {
            public void run() {
                mc.field_71439_g.func_145747_a(new ChatComponentText("Finished prediction from "+predictionStartTickActual+" to "+tickLength));
            }
        });
        predictor.setCallbackOnAbort(new Runnable() {
            public void run() {
                mc.field_71439_g.func_145747_a(new ChatComponentText("Aborted prediction"));
            }
        });
        predictor.start();
        editedSinceLastPrediction=false;
    }
    public static void abortPrediction() {
        predictor.abort();
    }
    final static int FILE_FORMAT_VERSION=1;
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
     * input mode:
     * * Lines are of format <tick#>|<buttons>|<yaw>,<pitch>.
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
            MinecraftTASEditorMod.logger.info("Cancelled action.");
        }
    }
    /**
     * @param file File to be saved
     */
    public static void saveFile(File file) {
        if (file==null) {
            MinecraftTASEditorMod.outputErrorToChatAndLog("File object was null.");
            return;
        }
        if (!wasEverInit) {
            MinecraftTASEditorMod.outputErrorToChatAndLog("Editor must be run at least once to save.");
            return;
        }
        try {
            if (file.exists()) {
                int option=JOptionPane.showConfirmDialog(null,null,"The file already exists. Do you want to overwrite?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (option!=JOptionPane.YES_OPTION) {
                    MinecraftTASEditorMod.logger.info("Cancelled action.");
                    return;
                }
            } else {
                file.createNewFile();
            }
            if (!file.canWrite()) {
                MinecraftTASEditorMod.outputErrorToChatAndLog("File cannot be written.");
                return;
            }
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,false));
            final int tickNumLen=String.valueOf(inputs.size()-1).length();
            writer.write("!property");
            writer.newLine();
            writer.write("startPosition="+MinecraftTASEditorUtil.stringifyVector(startPosition));
            writer.newLine();
            writer.write("startMotion="+MinecraftTASEditorUtil.stringifyVector(startMotion));
            writer.newLine();
            writer.write("startInvulnerabilityFrames="+startInvulnerabilityFrames);
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
            writer.newLine();
            writer.write("!input");
            writer.newLine();
            writer.write(new String(new char[tickNumLen]).replace("\0","#")+"|WASD_+^|Yaw       ,Pitch     ");
            writer.newLine();
            for (int tick=0;tick<inputs.size();tick++) {
                if (inputs.isNull(tick)) continue;
                MinecraftTASEditorInput input=inputs.get(tick);
                writer.write(String.format("%"+tickNumLen+"d|",tick)+
                        MinecraftTASEditorUtil.stringifyKeys(input)+"|"+
                        String.format("%10.5f,%10.5f",input.getRotationYaw(),input.getRotationPitch()));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            mc.field_71439_g.func_71165_d(EnumChatFormatting.RED+"Failed to write to file.");
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
            MinecraftTASEditorMod.logger.info("Cancelled action.");
        }
    }
    /**
     * @param file File to be loaded
     */
    public static void loadFile(File file) {
        if (file==null) {
            MinecraftTASEditorMod.outputErrorToChatAndLog("File object was null.");
            return;
        }
        try {
            if (!file.exists()) {
                MinecraftTASEditorMod.outputErrorToChatAndLog("File does not exist.");
                return;
            }
            if (!file.canRead()) {
                MinecraftTASEditorMod.outputErrorToChatAndLog("File cannot be read.");
                return;
            }
            Vec3 newStartPosition=new Vec3(0,0,0);
            Vec3 newStartMotion=new Vec3(0,0,0);
            int newStartInvulnerabilityFrames=0;
            int newTickLength=0;
            int newRerecords=0;
            int newPredictionRerecords=0;
            int newTotalRerecords=0;
            int fileFormatVersionIndicated=0;
            MinecraftTASEditorInputList newInputs=new MinecraftTASEditorInputList();
            Scanner reader=new Scanner(new BufferedReader(new FileReader(file)));
            final byte MODE_INPUT=0;
            final byte MODE_PROPERTY=1;
            byte mode=MODE_INPUT;
            char[] buttonColumns=null;
            boolean readFailed=false;
            int lineN=0;
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
                        MinecraftTASEditorMod.outputErrorToChatAndLog("Unexpected mode '"+modeStr+"' at line "+lineN);
                    }
                } else if (mode==MODE_INPUT) {
                    String[] args=line.split("\\|");
                    String[] argsNoSpace=line.replace(" ","").split("\\|");
                    if (args.length<2) {
                        MinecraftTASEditorMod.outputErrorToChatAndLog("Not enough argument in input mode at line "+lineN);
                        readFailed=true;
                        break;
                    }
                    if (StringUtils.isNumeric(argsNoSpace[0])) {
                        if (args.length<3) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Not enough argument in input mode at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        MinecraftTASEditorInput input=new MinecraftTASEditorInput();
                        int tick=Integer.valueOf(argsNoSpace[0]);
                        if (buttonColumns.length!=args[1].length()) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Unmatched table width at line "+lineN);
                        }
                        for (int coli=0;coli<args[1].length();coli++) {
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
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        while (newInputs.size()<tick) newInputs.add(null);
                        newInputs.add(tick,input);
                    } else if (argsNoSpace[0].matches("#+")) {
                        buttonColumns=args[1].toCharArray();
                    } else {
                        MinecraftTASEditorMod.outputErrorToChatAndLog("Malformed at line "+lineN);
                        readFailed=true;
                        break;
                    }
                } else if (mode==MODE_PROPERTY) {
                    String[] args=line.split("=",2);
                    String[] argsNoSpace=line.replace(" ","").split("=",2);
                    if (args.length<2) {
                        MinecraftTASEditorMod.outputErrorToChatAndLog("Not enough argument in property mode at line "+lineN);
                        readFailed=true;
                        break;
                    }
                    String key=argsNoSpace[0];
                    String value=argsNoSpace[1];
                    if (key.equals("startPosition")) {
                        if (!MinecraftTASEditorUtil.isStringParsableAsVector(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Coordinate cannot be parsed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newStartPosition=MinecraftTASEditorUtil.stringToVector(value);
                    } else if (key.equals("startMotion")) {
                        if (!MinecraftTASEditorUtil.isStringParsableAsVector(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Coordinate cannot be parsed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newStartMotion=MinecraftTASEditorUtil.stringToVector(value);
                    } else if (key.equals("startInvulnerabilityFrames")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newStartInvulnerabilityFrames=Integer.valueOf(value);
                    } else if (key.equals("tickLength")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newTickLength=Integer.valueOf(value);
                    } else if (key.equals("rerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newRerecords=Integer.valueOf(value);
                    } else if (key.equals("predictionRerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newPredictionRerecords=Integer.valueOf(value);
                    } else if (key.equals("totalRerecords")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        newTotalRerecords=Integer.valueOf(value);
                    } else if (key.equals("fileFormatVersion")) {
                        if (!StringUtils.isNumeric(value)) {
                            MinecraftTASEditorMod.outputErrorToChatAndLog("Parse failed at line "+lineN);
                            readFailed=true;
                            break;
                        }
                        fileFormatVersionIndicated=Integer.valueOf(value);
                    }
                }
            }
            reader.close();
            if (!readFailed) {
                if (fileFormatVersionIndicated==0||fileFormatVersionIndicated>FILE_FORMAT_VERSION) {
                    mc.field_71439_g.func_71165_d(EnumChatFormatting.YELLOW+"File format version was not specified or later than expected. Simulation may not be accurate.");
                }
                if (!isRunning) start();
                startPosition=newStartPosition;
                startMotion=newStartMotion;
                startInvulnerabilityFrames=newStartInvulnerabilityFrames;
                tickLength=newTickLength;
                rerecords=newRerecords;
                predictionRerecords=newPredictionRerecords;
                totalRerecords=newTotalRerecords;
                inputs=newInputs;
                setupRunners();
                editedSinceLastSimulation=false;
                editedSinceLastPrediction=false;
            }
        } catch (IOException e) {
            mc.field_71439_g.func_71165_d(EnumChatFormatting.RED+"Failed to write to file.");
            e.printStackTrace();
            return;
        }
    }
}
