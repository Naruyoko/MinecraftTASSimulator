package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;
import java.util.List;

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
        boolean hasNextLayer=true;
        @SuppressWarnings("unchecked")
        List<EntityMovementInfo> entityInfoList=(List<EntityMovementInfo>)(List<? extends EntityMovementInfo>)getPlayerStates(component).clone();
        while (hasNextLayer) {
            if (computedTicksN-startIndex>=2) {
                byte[] lineColor=getLineColor(component);
                boolean lineStarted=false;
                for (int i=startIndex;i<computedTicksN;i++) {
                    EntityMovementInfo entityInfo=entityInfoList.get(i);
                    if (entityInfo==null) {
                        if (lineStarted) {
                            GL11.glEnd();
                            lineStarted=false;
                        }
                    } else {
                        if (!lineStarted) {
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            lineStarted=true;
                        }
                        int tick=i-1;
                        byte alpha=(byte)(canTrustAt(component,tick)?255:127);
                        Vec3 position=entityInfo.getPosition();
                        GL11.glColor4ub(lineColor[0],lineColor[1],lineColor[2],alpha);
                        GL11.glVertex3d(position.xCoord,position.yCoord,position.zCoord);
                    }
                }
                if (lineStarted) GL11.glEnd();
            }
            GL11.glPointSize(POINT_SIZE);
            GL11.glBegin(GL11.GL_POINTS);
            for (int i=startIndex;i<computedTicksN;i++) {
                EntityMovementInfo entityInfo=entityInfoList.get(i);
                if (entityInfo!=null) {
                    int tick=i-1;
                    if (i!=stateIndex) drawPoint(entityInfo,canTrustAt(component,tick));
                }
            }
            GL11.glEnd();
            if (stateIndex>=startIndex-1&&computedTicksN>stateIndex) {
                EntityMovementInfo entityInfo=entityInfoList.get(stateIndex);
                if (entityInfo!=null) {
                    GL11.glPointSize(POINT_SIZE_FOCUSED);
                    GL11.glBegin(GL11.GL_POINTS);
                    drawPoint(entityInfo,canTrustAt(component,stateIndex));
                    GL11.glEnd();
                }
            }
            hasNextLayer=false;
            for (int i=startIndex;i<computedTicksN;i++) {
                EntityMovementInfo entityInfo=entityInfoList.get(i);
                if (entityInfo!=null) {
                    if (entityInfo.isRiding()) hasNextLayer=true;
                    entityInfoList.set(i,entityInfo.getRidingEntityInfo());
                }
            }
        }
    }
    private boolean shouldRender(int component) {
        switch (component) {
        case RENDER_SIMULATION: return true;
        case RENDER_PREDICTION: return true;
        case RENDER_SAVESLOT1: return InputEditor.getSavedPlayerStates(1)!=null;
        case RENDER_SAVESLOT2: return InputEditor.getSavedPlayerStates(2)!=null;
        case RENDER_SAVESLOT3: return InputEditor.getSavedPlayerStates(3)!=null;
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private byte[] getLineColor(int component) {
        switch (component) {
        case RENDER_SIMULATION: return new byte[] {(byte)255,(byte)0,(byte)0};
        case RENDER_PREDICTION: return new byte[] {(byte)255,(byte)127,(byte)0};
        case RENDER_SAVESLOT1: return new byte[] {(byte)0,(byte)127,(byte)255};
        case RENDER_SAVESLOT2: return new byte[] {(byte)0,(byte)0,(byte)255};
        case RENDER_SAVESLOT3: return new byte[] {(byte)127,(byte)0,(byte)255};
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private int getStartIndex(int component) {
        switch (component) {
        case RENDER_SIMULATION: return 0;
        case RENDER_PREDICTION: return InputEditor.getPredictionStartTickActual();
        case RENDER_SAVESLOT1: return 0;
        case RENDER_SAVESLOT2: return 0;
        case RENDER_SAVESLOT3: return 0;
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private int getComputedTicksN(int component) {
        switch (component) {
        case RENDER_SIMULATION: return InputEditor.getSimulator().getComputedTicksN();
        case RENDER_PREDICTION: return InputEditor.getPredictor().getComputedTicksN();
        case RENDER_SAVESLOT1: return InputEditor.getSavedPlayerStates(1).size();
        case RENDER_SAVESLOT2: return InputEditor.getSavedPlayerStates(2).size();
        case RENDER_SAVESLOT3: return InputEditor.getSavedPlayerStates(3).size();
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private boolean canTrustAt(int component,int tick) {
        switch (component) {
        case RENDER_SIMULATION: return InputEditor.canTrustSimulationAt(tick);
        case RENDER_PREDICTION: return InputEditor.canTrustPredictionAt(tick);
        case RENDER_SAVESLOT1: return true;
        case RENDER_SAVESLOT2: return true;
        case RENDER_SAVESLOT3: return true;
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private ArrayList<SimulatedPlayerInfo> getPlayerStates(int component){
        switch (component) {
        case RENDER_SIMULATION: return InputEditor.getSimulator().getPlayerStates();
        case RENDER_PREDICTION: return InputEditor.getPredictor().getPlayerStates();
        case RENDER_SAVESLOT1: return InputEditor.getSavedPlayerStates(1);
        case RENDER_SAVESLOT2: return InputEditor.getSavedPlayerStates(2);
        case RENDER_SAVESLOT3: return InputEditor.getSavedPlayerStates(3);
        default: throw new IllegalArgumentException("Asked for an invalid path");
        }
    }
    private void drawPoint(EntityMovementInfo playerState,boolean canTrust) {
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