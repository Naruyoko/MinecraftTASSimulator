package com.github.naruyoko.minecrafttassimulator;

import java.io.File;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.client.Minecraft;

public abstract class InputFileWorkerBase {
    Minecraft mc=Minecraft.getMinecraft();
    /**
     * The file to be read or written on
     */
    protected File file;
    public InputFileWorkerBase(File file) {
        this.file=file;
    }
    public InputFileWorkerBase() {
        this(null);
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file=file;
    }
    /**
     * Returns a recommended file extension. This is not enforced anywhere.
     * @return A file extension that should be starting with a period.
     */
    public static String fileExtension() {
        throw new NotImplementedException("");
    }
}
