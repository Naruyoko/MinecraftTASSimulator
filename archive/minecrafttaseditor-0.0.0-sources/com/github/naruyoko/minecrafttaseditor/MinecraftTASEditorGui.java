package com.github.naruyoko.minecrafttaseditor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class MinecraftTASEditorGui extends Gui {
    private Minecraft mc;
    private int drawX=0;
    private int drawY=0;
    private final int LINEH=10;
    public boolean shown=true;
    public MinecraftTASEditorGui(Minecraft mc) {
        this.mc=mc;
    }
    public void setDrawCoord(int x,int y) {
        drawX=x;
        drawY=y;
    }
    public void drawLine(String text,int color) {
        func_73731_b(mc.field_71466_p,text,drawX,drawY,color);
        drawY+=LINEH;
    }
    public void drawRightAlignedLine(String text,int color) {
        int textWidth=mc.field_71466_p.func_78256_a(text);
        func_73731_b(mc.field_71466_p,text,drawX-textWidth,drawY,color);
        drawY+=LINEH;
    }
    public void render(RenderGameOverlayEvent.Post event) {
        if (!shown) return;
        if (event.type!=ElementType.EXPERIENCE) return;
        if (!MinecraftTASEditorEditor.isRunning()) return;
        ScaledResolution scaled=new ScaledResolution(mc);
        int width=scaled.func_78326_a();
        int height=scaled.func_78328_b();
        int selectedTick=MinecraftTASEditorEditor.getSelectedTick();
        setDrawCoord(2,2);
        drawLine("Input length: "+MinecraftTASEditorEditor.getInputLength(),0xffffff);
        String tickDisplay=String.valueOf(MinecraftTASEditorEditor.getTickLength());
        if (MinecraftTASEditorEditor.isSimulationRunning()) {
            tickDisplay=MinecraftTASEditorEditor.getSimulator().getComputedTicksN()+"/"+tickDisplay;
        }
        if (MinecraftTASEditorEditor.isPredictionRunning()) {
            tickDisplay=MinecraftTASEditorEditor.getPredictor().getComputedTicksN()+"/"+tickDisplay;
        }
        drawLine("Tick length: "+tickDisplay,0xffffff);
        drawLine("Rerecords: "+MinecraftTASEditorEditor.getRerecords()+"/"+MinecraftTASEditorEditor.getPredictionRerecords()+"/"+MinecraftTASEditorEditor.getTotalRerecords(),0xffffff);
        drawLine("C<->V",0xffffff);
        drawLine("Selected: "+selectedTick,0xffffff);
        drawLine("+R<= R<->Y =>+Y",0xffffff);
        drawLine("Pred. start: "+MinecraftTASEditorEditor.getPredictionStartTick(),0xffffff);
        drawLine("^+R<= ^R<- ^T ->^Y =>^+Y",0xffffff);
        drawLine("",0xffffff);
        if (MinecraftTASEditorEditor.isSelectingTick()) {
            int inputColor=MinecraftTASEditorEditor.isSelectedInputNull()?0xff5555:0xffffff;
            drawLine("IJKLBNM",0xffffff);
            drawLine(MinecraftTASEditorUtil.stringifyKeys(MinecraftTASEditorEditor.getSelectedInput()),inputColor);
            drawLine("+(IJKLU)",0xffffff);
            drawLine(MinecraftTASEditorUtil.stringifyMouse(MinecraftTASEditorEditor.getSelectedInput(),8),inputColor);
            drawLine("^(U O)",0xffffff);
            drawLine(" (JKL)",0xffffff);
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
        }
        MinecraftTASEditorSimulator simulator=MinecraftTASEditorEditor.getSimulator();
        MinecraftTASEditorPredictor predictor=MinecraftTASEditorEditor.getPredictor();
        int stateIndex=selectedTick+1;
        setDrawCoord(width-2,2);
        EntityPlayerSP player=mc.field_71439_g;
        drawRightAlignedLine(MinecraftTASEditorUtil.stringifyKeys(MinecraftTASEditorInput.from(mc)),0xffffff);
        drawRightAlignedLine(MinecraftTASEditorUtil.stringifyMouse(MinecraftTASEditorInput.from(mc),8),0xffffff);
        drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorUtil.getPositionVector(player),8),0xffffff);
        drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorUtil.getMotionVector(player),8),0xffffff);
        drawRightAlignedLine(MinecraftTASEditorUtil.stringifyPotionEffects(player.func_70651_bq()),0xffffff);
        MinecraftTASEditorEntityPlayerSPLike virtualplayer=predictor.getVirtualPlayer();
        if (virtualplayer!=null) {
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorUtil.getPositionVector(virtualplayer),8),0xffffff);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorUtil.getMotionVector(virtualplayer),8),0xffffff);
        }
        drawLine("",0xffffff);
        if (simulator.getComputedTicksN()>stateIndex) {
            int infocolor=MinecraftTASEditorEditor.canTrustSimulationAt(selectedTick)?0xffffff:0xff5555;
            MinecraftTASEditorPlayerInfo playerState=simulator.getPlayerStateAtIndex(stateIndex);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(playerState.getPosition(),8),infocolor);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(playerState.getMotion(),8),infocolor);
            drawRightAlignedLine((playerState.isOnGround()?"On ground":"In air")+","+(playerState.isCollidedHorizontally()?"Collided":"Not collided"),infocolor);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyPotionEffects(playerState.getPotionEffects()),infocolor);
        } else {
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
        }
        drawRightAlignedLine("",0xffffff);
        if (selectedTick+1>=MinecraftTASEditorEditor.getPredictionStartTickActual()&&predictor.getComputedTicksN()>stateIndex) {
            int infocolor=MinecraftTASEditorEditor.canTrustPredictionAt(selectedTick)?0xffffff:0xff5555;
            MinecraftTASEditorPlayerInfo playerState=predictor.getPlayerStateAtIndex(stateIndex);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(playerState.getPosition(),8),infocolor);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyVector(playerState.getMotion(),8),infocolor);
            drawRightAlignedLine((playerState.isOnGround()?"On ground":"In air")+","+(playerState.isCollidedHorizontally()?"Collided":"Not collided"),infocolor);
            drawRightAlignedLine(MinecraftTASEditorUtil.stringifyPotionEffects(playerState.getPotionEffects()),infocolor);
        } else {
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
            drawRightAlignedLine("",0xffffff);
        }
        drawRightAlignedLine("",0xffffff);
        if (selectedTick==-1) {
            drawRightAlignedLine("Initial position: "+MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorEditor.getStartPosition(),8),0xffffff);
            drawRightAlignedLine("Initial motion: "+MinecraftTASEditorUtil.stringifyVector(MinecraftTASEditorEditor.getStartMotion(),8),0xffffff);
            drawRightAlignedLine("Initial invul.: "+String.valueOf(MinecraftTASEditorEditor.getStartInvulnerabilityFrames()),0xffffff);
        }
    }
}
