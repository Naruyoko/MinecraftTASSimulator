package com.github.naruyoko.minecrafttaseditor;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MinecraftTASEditorPredictor {
    private static Minecraft mc=Minecraft.func_71410_x();
    private MinecraftTASEditorInputList inputs=null;
    private ArrayList<MinecraftTASEditorPlayerInfo> playerStates=null;
    private ArrayList<MinecraftTASEditorPlayerInfo> importedPlayerStates=null;
    private int computedTicksN;
    private int startTick;
    private int targetTick;
    private Vec3 startPosition=null;
    private Vec3 startMotion=null;
    private boolean inheritEffectsFromAllTicks=false;
    private boolean isRunning;
    private Runnable callbackOnFinish;
    private Runnable callbackOnAbort;
    private MinecraftTASEditorEntityPlayerSPLike virtualPlayer;
    public MinecraftTASEditorPredictor(MinecraftTASEditorInputList inputs,Vec3 startPosition,Vec3 startMotion) {
        this.inputs=inputs;
        this.startPosition=startPosition;
        this.startMotion=startMotion;
        inheritEffectsFromAllTicks=false;
        playerStates=new ArrayList<MinecraftTASEditorPlayerInfo>();
        importedPlayerStates=new ArrayList<MinecraftTASEditorPlayerInfo>();
        computedTicksN=0;
        isRunning=false;
        startTick=-1;
        targetTick=-1;
        callbackOnFinish=null;
        virtualPlayer=new MinecraftTASEditorEntityPlayerSPLike(mc);
        mc.field_71441_e.func_72838_d(virtualPlayer);
    }
    public MinecraftTASEditorPredictor(Vec3 startPosition,Vec3 startMotion) {
        this(new MinecraftTASEditorInputList(),startPosition,startMotion);
    }
    public MinecraftTASEditorPredictor(MinecraftTASEditorInputList inputs,EntityPlayer player) {
        this(inputs,
                MinecraftTASEditorUtil.getPositionVector(player),
                MinecraftTASEditorUtil.getMotionVector(player));
    }
    public MinecraftTASEditorPredictor(EntityPlayer player) {
        this(new MinecraftTASEditorInputList(),player);
    }
    public MinecraftTASEditorPredictor(MinecraftTASEditorInputList inputs) {
        this(inputs,new Vec3(0,0,0),new Vec3(0,0,0));
    }
    public MinecraftTASEditorPredictor() {
        this(new MinecraftTASEditorInputList());
    }
    public void setInputs(MinecraftTASEditorInputList inputs) {
        this.inputs=inputs;
    }
    public MinecraftTASEditorInput getInputAt(int tick) {
        return inputs.get(tick);
    }
    public MinecraftTASEditorInput getCurrentInput() {
        int tick=computedTicksN-1;
        return getInputAt(tick);
    }
    public MinecraftTASEditorInput getLastInput() {
        int tick=computedTicksN-1;
        return getInputAt(tick-1);
    }
    public MinecraftTASEditorEntityPlayerSPLike getVirtualPlayer() {
        return virtualPlayer;
    }
    public void setStartPosition(Vec3 startPosition) {
        //TODO
        this.startPosition=startPosition;
    }
    public void setStartMotion(Vec3 startMotion) {
        //TODO
        this.startMotion=startMotion;
    }
    public ArrayList<MinecraftTASEditorPlayerInfo> getPlayerStates() {
        return playerStates;
    }
    public MinecraftTASEditorPlayerInfo getPlayerStateAtIndex(int index) {
        return playerStates.get(index);
    }
    public int getComputedTicksN() {
        return computedTicksN;
    }
    public boolean isRunning() {
        return isRunning;
    }
    @SuppressWarnings("unchecked")
    public boolean reset() {
        playerStates=(ArrayList<MinecraftTASEditorPlayerInfo>)importedPlayerStates.clone();
        while (playerStates.size()>startTick+1&&playerStates.size()>0) playerStates.remove(playerStates.size()-1);
        computedTicksN=playerStates.size();
        return playerStates.size()==computedTicksN;
    }
    public void resetAtTick(int tick) {
        //TODO
        while (computedTicksN>tick) {
            playerStates.remove(computedTicksN);
            computedTicksN--;
        }
    }
    public void appendState() {
        playerStates.add(new MinecraftTASEditorPlayerInfo(virtualPlayer));
        computedTicksN++;
    }
    public void setStartTick(int startTick) {
        this.startTick=startTick;
    }
    public void setInheritEffectsFromAllTicks(boolean inheritEffectsFromAllTicks) {
        this.inheritEffectsFromAllTicks=inheritEffectsFromAllTicks;
    }
    public void setTarget(int targetTick) {
        this.targetTick=targetTick;
    }
    @SuppressWarnings("unchecked")
    public void loadPlayerInfo(ArrayList<MinecraftTASEditorPlayerInfo> playerStates) {
        importedPlayerStates=(ArrayList<MinecraftTASEditorPlayerInfo>)playerStates.clone();
    }
    public void setCallbackOnFinish(Runnable callbackOnFinish) {
        this.callbackOnFinish=callbackOnFinish;
    }
    public void setCallbackOnAbort(Runnable callbackOnAbort) {
        this.callbackOnAbort=callbackOnAbort;
    }
    public void simulateNextTick(TickEvent.ClientTickEvent event) {
        if (!isRunning||isRunning) return;
        if (event==null) {
            int tick=computedTicksN-1;
            if (tick>=targetTick) stop();
            else {
                virtualPlayer.setInputs(inputs.get(tick));
            }
        } else if (event.phase==TickEvent.Phase.END) {
            appendState();
        }
    }
    public void simulateNextTick(boolean flag) {
        if (!isRunning) return;
        if (flag) {
            int tick=computedTicksN-1;
            if (tick>=targetTick) stop();
            else {
                virtualPlayer.setInputs(inputs.get(tick));
                if (inheritEffectsFromAllTicks&&computedTicksN<importedPlayerStates.size()) {
                    MinecraftTASEditorPlayerInfo playerState=importedPlayerStates.get(computedTicksN);
                    MinecraftTASEditorUtil.apply(virtualPlayer,MinecraftTASEditorUtil.changeDurations(playerState.getPotionEffects(),2));
                }
            }
        } else if (!flag) {
            appendState();
        }
    }
    public void start() {
        boolean canStart=reset();
        if (!canStart) abort();
        if (playerStates.size()<=0) {
            virtualPlayer.func_70107_b(startPosition.field_72450_a,startPosition.field_72448_b,startPosition.field_72449_c);
            virtualPlayer.field_70159_w=startMotion.field_72450_a;
            virtualPlayer.field_70181_x=startMotion.field_72448_b;
            virtualPlayer.field_70179_y=startMotion.field_72449_c;
        } else {
            MinecraftTASEditorPlayerInfo playerState=playerStates.get(startTick);
            virtualPlayer.func_70080_a(playerState.getPositionX(),playerState.getPositionY(),playerState.getPositionZ(),playerState.getRotationYaw(),playerState.getRotationPitch());
            virtualPlayer.field_70159_w=playerState.getMotionX();
            virtualPlayer.field_70181_x=playerState.getMotionY();
            virtualPlayer.field_70179_y=playerState.getMotionZ();
            virtualPlayer.func_70031_b(playerState.isSprinting());
            virtualPlayer.field_70122_E=playerState.isOnGround();
            virtualPlayer.field_70123_F=playerState.isCollidedHorizontally();
            virtualPlayer.field_70124_G=playerState.isCollidedVertically();
            virtualPlayer.jumpTicks=playerState.getJumpTicks();
            virtualPlayer.sprintToggleTimer=playerState.getSprintToggleTimer();
            virtualPlayer.sprintingTicksLeft=playerState.getSprintingTicksLeft();
            MinecraftTASEditorUtil.apply(virtualPlayer,MinecraftTASEditorUtil.changeDurations(playerState.getPotionEffects(),1));
        }
        isRunning=true;
    }
    public void stop() {
        isRunning=false;
        if (callbackOnFinish!=null) callbackOnFinish.run();
    }
    public void abort() {
        isRunning=false;
        if (callbackOnAbort!=null) callbackOnAbort.run();
    }
    /**
     * Be sure to call this when destroying.
     */
    public void cleanup() {
        mc.field_71441_e.func_72973_f(virtualPlayer);
    }
}
