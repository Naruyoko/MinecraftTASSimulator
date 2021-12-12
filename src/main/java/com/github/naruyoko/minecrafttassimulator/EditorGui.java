package com.github.naruyoko.minecrafttassimulator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class EditorGui extends Gui {
    private Minecraft mc;
    private int drawX=0;
    private int drawY=0;
    private final int LINEH=10;
    public boolean shown=true;
    public EditorGui(Minecraft mc) {
        this.mc=mc;
    }
    public void setDrawCoord(int x,int y) {
        drawX=x;
        drawY=y;
    }
    public void drawLine(String text,int color) {
        drawString(mc.fontRendererObj,text,drawX,drawY,color);
        drawY+=LINEH;
    }
    public void drawRightAlignedLine(String text,int color) {
        int textWidth=mc.fontRendererObj.getStringWidth(text);
        drawString(mc.fontRendererObj,text,drawX-textWidth,drawY,color);
        drawY+=LINEH;
    }
    public void render(RenderGameOverlayEvent.Post event) {
        if (!shown) return;
        if (event.type!=ElementType.EXPERIENCE) return;
        if (!InputEditor.isRunning()) return;
        ScaledResolution scaled=new ScaledResolution(mc);
        int width=scaled.getScaledWidth();
        //int height=scaled.getScaledHeight();
        int selectedTick=InputEditor.getSelectedTick();
        setDrawCoord(2,2);
        drawLine("Input length: "+InputEditor.getInputLength(),0xffffff);
        String tickDisplay=String.valueOf(InputEditor.getTickLength());
        if (InputEditor.isSimulationRunning()) {
            tickDisplay=InputEditor.getSimulator().getComputedTicksN()+"/"+tickDisplay;
        }
        if (InputEditor.isPredictionRunning()) {
            tickDisplay=InputEditor.getPredictor().getComputedTicksN()+"/"+tickDisplay;
        }
        drawLine("Tick length: "+tickDisplay,0xffffff);
        drawLine("Rerecords: "+InputEditor.getRerecords()+"/"+InputEditor.getPredictionRerecords()+"/"+InputEditor.getTotalRerecords(),0xffffff);
        drawLine("C<->V",0xffffff);
        drawLine("Selected: "+selectedTick,0xffffff);
        drawLine("+R<= R<->Y =>+Y",0xffffff);
        drawLine("Pred. start: "+InputEditor.getPredictionStartTick(),0xffffff);
        drawLine("^+R<= ^R<- ^T ->^Y =>^+Y",0xffffff);
        drawLine("",0xffffff);
        if (InputEditor.isSelectingTick()) {
            int inputColor=InputEditor.isSelectedInputNull()?0xff5555:0xffffff;
            Input input=InputEditor.getSelectedInput();
            drawLine("IJKLBNM",0xffffff);
            drawLine(SimulatorUtil.stringifyKeys(input),inputColor);
            drawLine("+(IJKLU)",0xffffff);
            drawLine(SimulatorUtil.stringifyMouse(input,8),inputColor);
            drawLine("^(U O)",0xffffff);
            drawLine(" (JKL)",0xffffff);
            String flags="";
            if (input.isRotationExact()) {
                if (!flags.isEmpty()) flags+=",";
                flags+="isRotationExact";
            }
            if (input.getMouseButtonInputs().size()>0) {
                if (!flags.isEmpty()) flags+=",";
                flags+="mouseButtonInputs{"+SimulatorUtil.stringifyMouseButtonInputs(input)+"}";
            }
            drawLine(flags,inputColor);
            drawLine("",0xffffff);
            drawLine("^Delete",0xffffff);
        } else {
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
            drawLine("",0xffffff);
        }
        Simulator simulator=InputEditor.getSimulator();
        Predictor predictor=InputEditor.getPredictor();
        int stateIndex=selectedTick+1;
        setDrawCoord(width-2,2);
        EntityPlayerSP player=mc.thePlayer;
        drawRightAlignedLine(SimulatorUtil.stringifyKeys(Input.from(mc)),0xffffff);
        drawRightAlignedLine(SimulatorUtil.stringifyMouse(Input.from(mc),8),0xffffff);
        try {
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(SimulatorUtil.getPositionVector(player),8),0xffffff);
        } catch (IllegalArgumentException e) {
            drawRightAlignedLine("Error",0xff5555);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            drawRightAlignedLine("Error",0xff5555);
            e.printStackTrace();
        }
        drawRightAlignedLine(SimulatorUtil.stringifyVec3(SimulatorUtil.getMotionVector(player),8),0xffffff);
        drawRightAlignedLine(SimulatorUtil.stringifyPotionEffects(player.getActivePotionEffects()),0xffffff);
        EntityPlayerSPLike virtualplayer=predictor.getVirtualPlayer();
        if (virtualplayer!=null) {
            drawRightAlignedLine("",0xffffff);
            try {
                drawRightAlignedLine(SimulatorUtil.stringifyVec3(SimulatorUtil.getPositionVector(virtualplayer),8),0xffffff);
            } catch (IllegalArgumentException e) {
                drawRightAlignedLine("Error",0xff5555);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                drawRightAlignedLine("Error",0xff5555);
                e.printStackTrace();
            }
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(SimulatorUtil.getMotionVector(virtualplayer),8),0xffffff);
        }
        drawLine("",0xffffff);
        if (simulator.getComputedTicksN()>stateIndex) {
            int infocolor=InputEditor.canTrustSimulationAt(selectedTick)?0xffffff:0xff5555;
            SimulatedPlayerInfo playerState=simulator.getPlayerStateAtIndex(stateIndex);
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(playerState.getPosition(),8),infocolor);
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(playerState.getMotion(),8),infocolor);
            drawRightAlignedLine((playerState.isOnGround()?"On ground":"In air")+","+(playerState.isCollidedHorizontally()?"Collided":"Not collided")+","+(playerState.isRiding()?"Riding":"Not riding"),infocolor);
            drawRightAlignedLine(SimulatorUtil.stringifyPotionEffects(playerState.getPotionEffects()),infocolor);
        } else {
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
        }
        drawRightAlignedLine("",0xffffff);
        if (selectedTick+1>=InputEditor.getPredictionStartTickActual()&&predictor.getComputedTicksN()>stateIndex) {
            int infocolor=InputEditor.canTrustPredictionAt(selectedTick)?0xffffff:0xff5555;
            SimulatedPlayerInfo playerState=predictor.getPlayerStateAtIndex(stateIndex);
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(playerState.getPosition(),8),infocolor);
            drawRightAlignedLine(SimulatorUtil.stringifyVec3(playerState.getMotion(),8),infocolor);
            drawRightAlignedLine((playerState.isOnGround()?"On ground":"In air")+","+(playerState.isCollidedHorizontally()?"Collided":"Not collided")+","+(playerState.isRiding()?"Riding":"Not riding"),infocolor);
            drawRightAlignedLine(SimulatorUtil.stringifyPotionEffects(playerState.getPotionEffects()),infocolor);
        } else {
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
        }
        drawRightAlignedLine("",0xffffff);
        if (selectedTick==-1) {
            drawRightAlignedLine("Initial position: "+SimulatorUtil.stringifyVec3(InputEditor.getStartPosition(),8),0xffffff);
            drawRightAlignedLine("Initial motion: "+SimulatorUtil.stringifyVec3(InputEditor.getStartMotion(),8),0xffffff);
            drawRightAlignedLine("Initial invul.: "+String.valueOf(InputEditor.getStartInvulnerabilityFrames()),0xffffff);
            drawRightAlignedLine("Initial game mode: "+SimulatorUtil.stringifyGameType(InputEditor.getStartGametype()),0xffffff);
            drawRightAlignedLine("Mouse sensitivity: "+String.valueOf(InputEditor.getMouseSensitivity()),0xffffff);
            drawRightAlignedLine("Maximum safe movement: "+String.valueOf(InputEditor.getMouseMaxSafeMovement()),0xffffff);
        }
    }
}
