package com.github.naruyoko.minecrafttassimulator;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;

/**
 * Holds the configurations around the input file and the editor.
 */
public class InputSideMenu {
    public Vec3 startPosition;
    public Vec3 startMotion;
    public int startInvulnerabilityFrames;
    public GameType startGametype;
    public float mouseSensitivity;
    public int mouseMaxSafeMovement;
    public int tickLength;
    public int rerecords;
    public int predictionRerecords;
    public int totalRerecords;
    public InputSideMenu(Vec3 startPosition,Vec3 startMotion,int startInvulnerabilityFrames,GameType startGametype,float mouseSensitivity,int mouseMaxSafeMovement,int tickLength,int rerecords,int predictionRerecords,int totalRerecords) {
        this.startPosition=startPosition;
        this.startMotion=startMotion;
        this.startInvulnerabilityFrames=startInvulnerabilityFrames;
        this.startGametype=startGametype;
        this.mouseSensitivity=mouseSensitivity;
        this.mouseMaxSafeMovement=mouseMaxSafeMovement;
        this.tickLength=tickLength;
        this.rerecords=rerecords;
        this.predictionRerecords=predictionRerecords;
        this.totalRerecords=totalRerecords;
    }
    public InputSideMenu() {
        this(new Vec3(0,0,0),new Vec3(0,0,0),0,GameType.NOT_SET,0.5F,900,0,0,0,0);
    }
    public Vec3 getStartPosition() {
        return startPosition;
    }
    public void setStartPosition(Vec3 startPosition) {
        this.startPosition = startPosition;
    }
    public Vec3 getStartMotion() {
        return startMotion;
    }
    public void setStartMotion(Vec3 startMotion) {
        this.startMotion = startMotion;
    }
    public int getStartInvulnerabilityFrames() {
        return startInvulnerabilityFrames;
    }
    public void setStartInvulnerabilityFrames(int startInvulnerabilityFrames) {
        this.startInvulnerabilityFrames = startInvulnerabilityFrames;
    }
    public GameType getStartGametype() {
        return startGametype;
    }
    public void setStartGametype(GameType startGametype) {
        this.startGametype = startGametype;
    }
    public float getMouseSensitivity() {
        return mouseSensitivity;
    }
    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }
    public int getMouseMaxSafeMovement() {
        return mouseMaxSafeMovement;
    }
    public void setMouseMaxSafeMovement(int mouseMaxSafeMovement) {
        this.mouseMaxSafeMovement = mouseMaxSafeMovement;
    }
    public int getTickLength() {
        return tickLength;
    }
    public void setTickLength(int tickLength) {
        this.tickLength = tickLength;
    }
    public int getRerecords() {
        return rerecords;
    }
    public void setRerecords(int rerecords) {
        this.rerecords = rerecords;
    }
    public int getPredictionRerecords() {
        return predictionRerecords;
    }
    public void setPredictionRerecords(int predictionRerecords) {
        this.predictionRerecords = predictionRerecords;
    }
    public int getTotalRerecords() {
        return totalRerecords;
    }
    public void setTotalRerecords(int totalRerecords) {
        this.totalRerecords = totalRerecords;
    }
}
