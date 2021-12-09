package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Renderer {
    private static final int LINE_WIDTH=4;
    private static final int POINT_SIZE=8;
    private static final int POINT_SIZE_FOCUSED=12;
    private static final int RENDER_SIMULATION=0;
    private static final int RENDER_PREDICTION=1;
    private static final int RENDER_SAVESLOT1=2;
    private static final int RENDER_SAVESLOT2=3;
    private static final int RENDER_SAVESLOT3=4;
    private Minecraft mc;
    public Renderer(Minecraft mc) {
        this.mc=mc;
    }
    public void render(RenderWorldLastEvent event) {
        if (!InputEditor.isRunning()) return;
        EntityPlayerSP player=mc.thePlayer;
        double playerX=player.prevPosX+(player.posX-player.prevPosX)*event.partialTicks;
        double playerY=player.prevPosY+(player.posY-player.prevPosY)*event.partialTicks;
        double playerZ=player.prevPosZ+(player.posZ-player.prevPosZ)*event.partialTicks;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(LINE_WIDTH);
        GL11.glTranslated(-playerX,-playerY,-playerZ);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderEach(RENDER_SIMULATION);
        renderEach(RENDER_PREDICTION);
        renderEach(RENDER_SAVESLOT1);
        renderEach(RENDER_SAVESLOT2);
        renderEach(RENDER_SAVESLOT3);
        //GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    private void renderEach(int component) {
        if (!shouldRender(component)) return;
        int stateTick=InputEditor.getSelectedTick();
        int stateIndex=stateTick+1;
        int startIndex=getStartIndex(component);
        int computedTicksN=getComputedTicksN(component);
        if (computedTicksN-startIndex>=2) {
            byte[] lineColor=getLineColor(component);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int index=startIndex;index<computedTicksN;index++) {
                int tick=index-1;
                byte alpha=(byte)(canTrustAt(component,tick)?255:127);
                SimulatedPlayerInfo playerState=getPlayerStateAtIndex(component,index);
                Vec3 position=playerState.getPosition();
                GL11.glColor4ub(lineColor[0],lineColor[1],lineColor[2],alpha);
                GL11.glVertex3d(position.xCoord,position.yCoord,position.zCoord);
            }
            GL11.glEnd();
        }
        GL11.glPointSize(POINT_SIZE);
        GL11.glBegin(GL11.GL_POINTS);
        for (int index=startIndex;index<computedTicksN;index++) {
            int tick=index-1;
            if (index!=stateIndex) drawPoint(getPlayerStateAtIndex(component,index),canTrustAt(component,tick));
        }
        GL11.glEnd();
        if (stateIndex>=startIndex-1&&computedTicksN>stateIndex) {
            GL11.glPointSize(POINT_SIZE_FOCUSED);
            GL11.glBegin(GL11.GL_POINTS);
            drawPoint(getPlayerStateAtIndex(component,stateIndex),canTrustAt(component,stateIndex));
            GL11.glEnd();
        }
    }
    private boolean shouldRender(int component) {
        if (component==RENDER_SIMULATION) return true;
        else if (component==RENDER_PREDICTION) return true;
        else if (component==RENDER_SAVESLOT1) return InputEditor.getSavedPlayerStates(1)!=null;
        else if (component==RENDER_SAVESLOT2) return InputEditor.getSavedPlayerStates(2)!=null;
        else if (component==RENDER_SAVESLOT3) return InputEditor.getSavedPlayerStates(3)!=null;
        else return false;
    }
    private byte[] getLineColor(int component) {
        if (component==RENDER_SIMULATION) return new byte[] {(byte)255,(byte)0,(byte)0};
        else if (component==RENDER_PREDICTION) return new byte[] {(byte)255,(byte)127,(byte)0};
        else if (component==RENDER_SAVESLOT1) return new byte[] {(byte)0,(byte)127,(byte)255};
        else if (component==RENDER_SAVESLOT2) return new byte[] {(byte)0,(byte)0,(byte)255};
        else if (component==RENDER_SAVESLOT3) return new byte[] {(byte)127,(byte)0,(byte)255};
        else return null;
    }
    private int getStartIndex(int component) {
        if (component==RENDER_SIMULATION) return 0;
        else if (component==RENDER_PREDICTION) return InputEditor.getPredictionStartTickActual();
        else if (component==RENDER_SAVESLOT1) return 0;
        else if (component==RENDER_SAVESLOT2) return 0;
        else if (component==RENDER_SAVESLOT3) return 0;
        else return 0;
    }
    private int getComputedTicksN(int component) {
        if (component==RENDER_SIMULATION) {
            Simulator simulator=InputEditor.getSimulator();
            return simulator.getComputedTicksN();
        } else if (component==RENDER_PREDICTION) {
            Predictor predictor=InputEditor.getPredictor();
            return predictor.getComputedTicksN();
        } else if (component==RENDER_SAVESLOT1) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(1);
            return playerStates.size();
        } else if (component==RENDER_SAVESLOT2) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(2);
            return playerStates.size();
        } else if (component==RENDER_SAVESLOT3) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(3);
            return playerStates.size();
        } else return 0;
    }
    private boolean canTrustAt(int component,int tick) {
        if (component==RENDER_SIMULATION) return InputEditor.canTrustSimulationAt(tick);
        else if (component==RENDER_PREDICTION) return InputEditor.canTrustPredictionAt(tick);
        else if (component==RENDER_SAVESLOT1) return true;
        else if (component==RENDER_SAVESLOT2) return true;
        else if (component==RENDER_SAVESLOT3) return true;
        else return false;
    }
    private SimulatedPlayerInfo getPlayerStateAtIndex(int component,int index) {
        if (component==RENDER_SIMULATION) {
            Simulator simulator=InputEditor.getSimulator();
            return simulator.getPlayerStateAtIndex(index);
        } else if (component==RENDER_PREDICTION) {
            Predictor predictor=InputEditor.getPredictor();
            return predictor.getPlayerStateAtIndex(index);
        } else if (component==RENDER_SAVESLOT1) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(1);
            return playerStates.get(index);
        } else if (component==RENDER_SAVESLOT2) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(2);
            return playerStates.get(index);
        } else if (component==RENDER_SAVESLOT3) {
            ArrayList<SimulatedPlayerInfo> playerStates=InputEditor.getSavedPlayerStates(3);
            return playerStates.get(index);
        } else return null;
    }
    private void drawPoint(SimulatedPlayerInfo playerState,boolean canTrust) {
        byte alpha=(byte)(canTrust?255:127);
        if (playerState.isOnGround()) {
            if (playerState.isCollidedHorizontally()) {
                GL11.glColor4ub((byte)0,(byte)128,(byte)0,alpha);
            } else {
                GL11.glColor4ub((byte)0,(byte)255,(byte)0,alpha);
            }
        } else {
            if (playerState.isCollidedHorizontally()) {
                GL11.glColor4ub((byte)0,(byte)128,(byte)128,alpha);
            } else {
                GL11.glColor4ub((byte)0,(byte)255,(byte)255,alpha);
            }
        }
        Vec3 position=playerState.getPosition();
        GL11.glVertex3d(position.xCoord,position.yCoord,position.zCoord);
    }
}