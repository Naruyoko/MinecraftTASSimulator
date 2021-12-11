package com.github.naruyoko.minecrafttassimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MCSimFileWriter extends InputFileWriter {
    public MCSimFileWriter(File file,InputFileInformation inputFileInformation) {
        super(file,inputFileInformation);
    }
    public static String fileExtension() {
        return ".mcsim";
    }
    /**
     * @throws IOException 
     * @throws IllegalArgumentException
     */
    @Override
    public void write() throws IOException {
        if (file==null) throw new IllegalArgumentException("File object was null.");
        if (!file.exists()) file.createNewFile();
        if (!file.canWrite()) throw new IllegalArgumentException("File can not be written.");
        if (inputFileInformation==null) throw new IllegalArgumentException("Inputs object was null.");
        InputList inputs=inputFileInformation.getInputs();
        InputSideMenu inputSideMenu=inputFileInformation.getInputSideMenu();
        int fileFormatVersion=inputFileInformation.getFileFormatVersion();
        String editorVersion=inputFileInformation.getEditorVersion();
        if (inputs==null||inputSideMenu==null||editorVersion==null) throw new IllegalArgumentException("Some properties of the input was null.");
        BufferedWriter writer=new BufferedWriter(new FileWriter(file,false));
        final int tickNumLen=String.valueOf(inputs.size()-1).length();
        writer.write("!property");
        writer.newLine();
        writer.write("startPosition="+SimulatorUtil.stringifyVec3(inputSideMenu.startPosition));
        writer.newLine();
        writer.write("startMotion="+SimulatorUtil.stringifyVec3(inputSideMenu.startMotion));
        writer.newLine();
        writer.write("startInvulnerabilityFrames="+inputSideMenu.startInvulnerabilityFrames);
        writer.newLine();
        writer.write("startGametype="+SimulatorUtil.stringifyGameType(inputSideMenu.startGametype));
        writer.newLine();
        writer.write("mouseSensitivity="+inputSideMenu.mouseSensitivity);
        writer.newLine();
        writer.write("mouseMaxSafeMovement="+inputSideMenu.mouseMaxSafeMovement);
        writer.newLine();
        writer.write("tickLength="+inputSideMenu.tickLength);
        writer.newLine();
        writer.write("rerecords="+inputSideMenu.rerecords);
        writer.newLine();
        writer.write("predictionRerecords="+inputSideMenu.predictionRerecords);
        writer.newLine();
        writer.write("totalRerecords="+inputSideMenu.totalRerecords);
        writer.newLine();
        writer.write("fileFormatVersion="+fileFormatVersion);
        writer.newLine();
        writer.write("editorVersion="+editorVersion);
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
    }
}
