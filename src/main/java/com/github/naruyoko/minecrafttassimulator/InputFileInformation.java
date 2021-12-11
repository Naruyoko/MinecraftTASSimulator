package com.github.naruyoko.minecrafttassimulator;

/**
 * A container for input file's information on read or write, such as the list of inputs, configurations, and other file informations
 */
public class InputFileInformation {
    public InputList inputs;
    public InputSideMenu inputSideMenu;
    public int fileFormatVersion;
    public String editorVersion;
    /**
     * Whether or not if this was generated from an external format. Used only for reading.
     */
    public boolean isExternalFormat;
    public InputFileInformation(InputList inputs,InputSideMenu inputSideMenu,int fileFormatVersion,String editorVersion,boolean isExternalFormat) {
        this.inputs=inputs;
        this.inputSideMenu=inputSideMenu;
        this.fileFormatVersion=fileFormatVersion;
        this.editorVersion=editorVersion;
        this.isExternalFormat=isExternalFormat;
    }
    public InputFileInformation(InputList inputs,InputSideMenu inputSideMenu,int fileFormatVersion,String editorVersion) {
        this(inputs,inputSideMenu,fileFormatVersion,editorVersion,false);
    }
    public InputList getInputs() {
        return inputs;
    }
    public void setInputs(InputList inputs) {
        this.inputs = inputs;
    }
    public InputSideMenu getInputSideMenu() {
        return inputSideMenu;
    }
    public void setInputSideMenu(InputSideMenu inputSideMenu) {
        this.inputSideMenu = inputSideMenu;
    }
    public int getFileFormatVersion() {
        return fileFormatVersion;
    }
    public void setFileFormatVersion(int fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }
    public String getEditorVersion() {
        return editorVersion;
    }
    public void setEditorVersion(String editorVersion) {
        this.editorVersion = editorVersion;
    }
    public boolean isExternalFormat() {
        return isExternalFormat;
    }
    public void setExternalFormat(boolean isExternalFormat) {
        this.isExternalFormat = isExternalFormat;
    }
}
