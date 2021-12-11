package com.github.naruyoko.minecrafttassimulator;

import java.io.File;
import java.io.IOException;

public abstract class InputFileWriter extends InputFileWorkerBase {
    /**
     * The information to be written
     */
    protected InputFileInformation inputFileInformation;
    public InputFileWriter(File file,InputFileInformation inputFileInformation) {
        super(file);
        this.inputFileInformation=inputFileInformation;
    }
    public InputFileWriter() {
        this(null,null);
    }
    public InputFileInformation getInputFileInformation() {
        return inputFileInformation;
    }
    public void setInputFileInformation(InputFileInformation inputFileInformation) {
        this.inputFileInformation=inputFileInformation;
    }
    /**
     * Write to the file held with the inputs in some format.
     * @throws IOException 
     */
    public abstract void write() throws IOException;
}
