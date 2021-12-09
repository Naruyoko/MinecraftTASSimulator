package com.github.naruyoko.minecrafttaseditor;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraft.world.WorldSettings.GameType;
public class MinecraftTASEditorSimulator {
    private static Minecraft mc=Minecraft.func_71410_x();
    private MinecraftTASEditorInputList inputs=null;
    private ArrayList<MinecraftTASEditorPlayerInfo> playerStates=null;
    private int computedTicksN;
    private int targetTick;
    private Vec3 startPosition=null;
    private Vec3 startMotion=null;
    private int startInvulnerabilityFrames=0;
    private GameType startGametype=null;
    private boolean isRunning;
    private Runnable callbackOnFinish;
    private Runnable callbackOnAbort;
    public MinecraftTASEditorSimulator(MinecraftTASEditorInputList inputs,Vec3 startPosition,Vec3 startMotion,int startInvulnerabilityFrames) {
        this.inputs=inputs;
        this.startPosition=startPosition;
        this.startMotion=startMotion;
        this.startInvulnerabilityFrames=startInvulnerabilityFrames;
        playerStates=new ArrayList<MinecraftTASEditorPlayerInfo>();
        computedTicksN=0;
        isRunning=false;
        targetTick=-1;
        callbackOnFinish=null;
        resetInBackground();
    }
    public MinecraftTASEditorSimulator(Vec3 startPosition,Vec3 startMotion,int startInvulnerabilityFrames) {
        this(new MinecraftTASEditorInputList(),startPosition,startMotion,startInvulnerabilityFrames);
    }
    public MinecraftTASEditorSimulator(MinecraftTASEditorInputList inputs,EntityPlayer player) throws IllegalArgumentException, IllegalAccessException {
        this(inputs,
                MinecraftTASEditorUtil.getPositionVector(player),
                MinecraftTASEditorUtil.getMotionVector(player),
                MinecraftTASEditorUtil.getRespawnInvulnerabilityTicks(MinecraftTASEditorUtil.getPlayerMP(mc)));
    }
    public MinecraftTASEditorSimulator(EntityPlayer player) throws IllegalArgumentException, IllegalAccessException {
        this(new MinecraftTASEditorInputList(),player);
    }
    public MinecraftTASEditorSimulator(MinecraftTASEditorInputList inputs) {
        this(inputs,new Vec3(0,0,0),new Vec3(0,0,0),0);
    }
    public MinecraftTASEditorSimulator() {
        this(new MinecraftTASEditorInputList());
    }
    public void setInputs(MinecraftTASEditorInputList inputs) {
        this.inputs=inputs;
        resetInBackground();
    }
    public void setStartPosition(Vec3 startPosition) {
        this.startPosition=startPosition;
        resetInBackground();
    }
    public void setStartMotion(Vec3 startMotion) {
        this.startMotion=startMotion;
        resetInBackground();
    }
    public void setStartInvulnerabilityFrames(int startInvulnerabilityFrames) {
        this.startInvulnerabilityFrames=startInvulnerabilityFrames;
        resetInBackground();
    }
    public void setStartGametype(GameType startGametype) {
        this.startGametype=startGametype;
        resetInBackground();
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
    public void reset(boolean appendFirst,boolean silent) {
        //resetAtTick(0);
        computedTicksN=0;
        playerStates=new ArrayList<MinecraftTASEditorPlayerInfo>();
        if (!silent) {
            mc.field_71439_g.func_70634_a(startPosition.field_72450_a,startPosition.field_72448_b,startPosition.field_72449_c);
            if (inputs.size()>0) mc.field_71439_g.func_70080_a(startPosition.field_72450_a,startPosition.field_72448_b,startPosition.field_72449_c,(float)inputs.get(0).getRotationYaw(),(float)inputs.get(0).getRotationPitch());
            else mc.field_71439_g.func_70107_b(startPosition.field_72450_a,startPosition.field_72448_b,startPosition.field_72449_c);
            mc.field_71439_g.field_70159_w=startMotion.field_72450_a;
            mc.field_71439_g.field_70181_x=startMotion.field_72448_b;
            mc.field_71439_g.field_70179_y=startMotion.field_72449_c;
            if (startGametype!=null&&!startGametype.equals(GameType.NOT_SET)) mc.field_71442_b.func_78746_a(startGametype);
            try {
                MinecraftTASEditorUtil.setRespawnInvulnerabilityTicks(MinecraftTASEditorUtil.getPlayerMP(mc),startInvulnerabilityFrames);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (appendFirst) appendState();
    }
    public void reset(boolean appendFirst) {
        reset(appendFirst,false);
    }
    public void reset() {
        reset(true);
    }
    public void resetInBackground() {
        reset(true,true);
    }
    public void resetAtTick(int tick) {
        //TODO
        while (computedTicksN>tick) {
            playerStates.remove(computedTicksN);
            computedTicksN--;
        }
    }
    public void appendState() {
        playerStates.add(new MinecraftTASEditorPlayerInfo(mc.field_71439_g));
        computedTicksN++;
    }
    public void setTarget(int targetTick) {
        this.targetTick=targetTick;
    }
    public void setCallbackOnFinish(Runnable callbackOnFinish) {
        this.callbackOnFinish=callbackOnFinish;
    }
    public void setCallbackOnAbort(Runnable callbackOnAbort) {
        this.callbackOnAbort=callbackOnAbort;
    }
    public void applyInputs(TickEvent.ClientTickEvent event) {
        if (!isRunning) return;
        if (event.phase==TickEvent.Phase.START) {
            int tick=computedTicksN-1;
            if (tick>=targetTick) stop();
            else {
                inputs.get(tick).apply(mc.field_71439_g,mc.field_71474_y);
            }
        } else if (event.phase==TickEvent.Phase.END) {
            if (mc.func_147113_T()) return;
            appendState();
        }
    }
    public void start() {
        reset(false);
        isRunning=true;
    }
    public void stop() {
        isRunning=false;
        new MinecraftTASEditorInput().apply(mc.field_71439_g,mc.field_71474_y);
        if (callbackOnFinish!=null) callbackOnFinish.run();
    }
    public void abort() {
        isRunning=false;
        new MinecraftTASEditorInput().apply(mc.field_71439_g,mc.field_71474_y);
        if (callbackOnAbort!=null) callbackOnAbort.run();
    }
    /**
     * Be sure to call this when destroying.
     */
    public void cleanup() {}
}
