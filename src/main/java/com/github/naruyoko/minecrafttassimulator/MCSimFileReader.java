package com.github.naruyoko.minecrafttassimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class MCSimFileReader extends InputFileReader {
    public MCSimFileReader(File file) {
        super(file);
    }
    public static String fileExtension() {
        return ".mcsim";
    }
    public static boolean isExternalFormat() {
        return false;
    }
    /**
     * @throws IllegalArgumentException
     */
    @Override
    public ReadReturnValue read() {
        if (file==null) throw new IllegalArgumentException("File object was null.");
        if (!file.exists()) throw new IllegalArgumentException("File does not exist.");
        if (!file.canRead()) throw new IllegalArgumentException("File cannot be read.");
        int lineN=0;
        List<MessageWithLevelAndLineNum> messages=new ArrayList<MessageWithLevelAndLineNum>();
        try {
            InputSideMenu inputSideMenu=new InputSideMenu();
            int fileFormatVersionIndicated=0;
            String editorVersionIndicated="";
            InputList newInputs=new InputList();
            Scanner reader=new Scanner(new BufferedReader(new FileReader(file)));
            final byte MODE_INPUT=0;
            final byte MODE_PROPERTY=1;
            byte mode=MODE_INPUT;
            char[] buttonColumns=null;
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
                        reader.close();
                        return new ReadFailure(messages,"Unexpected mod '"+modeStr+"'", lineN);
                    }
                } else if (mode==MODE_INPUT) {
                    String[] args=line.split("\\|");
                    String[] argsNoSpace=line.replace(" ","").split("\\|");
                    if (args.length<2) {
                        reader.close();
                        return new ReadFailure(messages,"Not enough argument in input mode",lineN);
                    }
                    if (StringUtils.isNumeric(argsNoSpace[0])) {
                        if (args.length<3) {
                            reader.close();
                            return new ReadFailure(messages,"Not enough argument in input mode",lineN);
                        }
                        Input input=new Input();
                        int tick=Integer.valueOf(argsNoSpace[0]);
                        if (buttonColumns.length!=args[1].length()) messages.add(new MessageWithLevelAndLineNum(Level.WARN,"Unmatched table width",lineN));
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
                        } catch (NumberFormatException e) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected floats",lineN);
                        }
                        if (args.length>3) { //Required check for file format version < 2
                            for (int i=0;i<args[3].length();i++) {
                                char flag=args[3].charAt(i);
                                String param;
                                if (i+1>=args[3].length()||args[3].charAt(i+1)!='{') {
                                    param=null;
                                } else {
                                    int closingIndex=args[3].indexOf('}');
                                    if (closingIndex==-1) {
                                        reader.close();
                                        return new ReadFailure(messages,"Parse failed: Unclosed {",lineN);
                                    }
                                    param=args[3].substring(i+2,closingIndex);
                                    i=closingIndex;
                                }
                                switch (flag) {
                                case '=':
                                    if (param==null) input.setRotationExact(true);
                                    else{
                                        reader.close();
                                        return new ReadFailure(messages,"Parse failed: = flag excepted no parameters",lineN);
                                    }
                                    break;
                                case 'm':
                                    if (param==null) {
                                        reader.close();
                                        return new ReadFailure(messages,"Parse failed: m flag expected a string of inputs",lineN);
                                    }
                                    else {
                                        if (param.length()>50) messages.add(new MessageWithLevelAndLineNum(Level.WARN,"Recieved inputs too long",lineN));
                                        input.setMouseButtonInputs(SimulatorUtil.parseMouseButtonInputs(param));
                                    }
                                    break;
                                case ';':
                                    break;
                                default:
                                    reader.close();
                                    return new ReadFailure(messages,"Parse failed: Unknown flag "+flag,lineN);
                                }
                            }
                        }
                        newInputs.set(tick,input);
                    } else if (argsNoSpace[0].matches("#+")) {
                        buttonColumns=args[1].toCharArray();
                    } else {
                        reader.close();
                        return new ReadFailure(messages,"Malformed",lineN);
                    }
                } else if (mode==MODE_PROPERTY) {
                    String[] args=line.split("=",2);
                    String[] argsNoSpace=line.replace(" ","").split("=",2);
                    if (args.length<2) {
                        reader.close();
                        return new ReadFailure(messages,"Not enough argument in property mode",lineN);
                    }
                    String key=argsNoSpace[0];
                    String value=argsNoSpace[1];
                    if (key.equals("startPosition")) {
                        if (!SimulatorUtil.isStringParsableAsVec3(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Coordinate cannot be parsed",lineN);
                        }
                        inputSideMenu.setStartPosition(SimulatorUtil.stringToVec3(value));
                    } else if (key.equals("startMotion")) {
                        if (!SimulatorUtil.isStringParsableAsVec3(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Coordinate cannot be parsed",lineN);
                        }
                        inputSideMenu.setStartMotion(SimulatorUtil.stringToVec3(value));
                    } else if (key.equals("startInvulnerabilityFrames")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setStartInvulnerabilityFrames(Integer.valueOf(value));
                    } else if (key.equals("startGametype")) {
                        inputSideMenu.setStartGametype(SimulatorUtil.toGameType(value));
                    } else if (key.equals("mouseSensitivity")) {
                        try {
                            inputSideMenu.setMouseSensitivity(Float.valueOf(value));
                        } catch(NumberFormatException e) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected a float",lineN);
                        }
                    } else if (key.equals("mouseMaxSafeMovement")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setMouseMaxSafeMovement(Integer.valueOf(value));
                    } else if (key.equals("tickLength")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setTickLength(Integer.valueOf(value));
                    } else if (key.equals("rerecords")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setRerecords(Integer.valueOf(value));
                    } else if (key.equals("predictionRerecords")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setPredictionRerecords(Integer.valueOf(value));
                    } else if (key.equals("totalRerecords")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        inputSideMenu.setTotalRerecords(Integer.valueOf(value));
                    } else if (key.equals("fileFormatVersion")) {
                        if (!SimulatorUtil.isInteger(value)) {
                            reader.close();
                            return new ReadFailure(messages,"Parse failed: Expected an integer",lineN);
                        }
                        fileFormatVersionIndicated=Integer.valueOf(value);
                    } else if (key.equals("editorVersion")) {
                        editorVersionIndicated=value;
                    }
                }
            }
            reader.close();
            InputFileInformation inputFileInformation=new InputFileInformation(newInputs,inputSideMenu,fileFormatVersionIndicated,editorVersionIndicated,isExternalFormat());
            return new ReadSuccess(messages,inputFileInformation);
        } catch (IOException e) {
            return new ReadException(messages,e,lineN);
        }
    }
}
