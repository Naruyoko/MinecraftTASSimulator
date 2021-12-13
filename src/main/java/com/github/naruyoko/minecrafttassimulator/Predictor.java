package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Predictor {
    private static Minecraft mc=Minecraft.getMinecraft();
    private InputList inputs=null;
    private InputSideMenu inputSideMenu=null;
    private ArrayList<SimulatedPlayerInfo> playerStates=null;
    private ArrayList<SimulatedPlayerInfo> importedPlayerStates=null;
    private int computedTicksN;
    private int startTick;
    private int targetTick;
    private boolean inheritEffectsFromAllTicks=false;
    private boolean isRunning;
    private Runnable callbackOnFinish;
    private Runnable callbackOnAbort;
    private EntityPlayerSPLike virtualPlayer;
    public Predictor(InputList inputs,InputSideMenu inputSideMenu) {
        this.inputs=inputs;
        this.inputSideMenu=inputSideMenu;
        inheritEffectsFromAllTicks=false;
        playerStates=new ArrayList<SimulatedPlayerInfo>();
        importedPlayerStates=new ArrayList<SimulatedPlayerInfo>();
        computedTicksN=0;
        isRunning=false;
        startTick=-1;
        targetTick=-1;
        callbackOnFinish=null;
        virtualPlayer=new EntityPlayerSPLike(mc);
        mc.theWorld.spawnEntityInWorld(virtualPlayer);
    }
    public Predictor(InputSideMenu inputSideMenu) {
        this(new InputList(),inputSideMenu);
    }
    public Predictor(InputList inputs) {
        this(inputs,new InputSideMenu());
    }
    public Predictor() {
        this(new InputList());
    }
    public void setInputs(InputList inputs,boolean reset) {
        this.inputs=inputs;
        if (reset) reset();
    }
    public void setInputs(InputList inputs) {
        setInputs(inputs,true);
    }
    public void setInputSideMenu(InputSideMenu inputSideMenu,boolean reset) {
        this.inputSideMenu=inputSideMenu;
        if (reset) reset();
    }
    public void setInputSideMenu(InputSideMenu inputSideMenu) {
        setInputSideMenu(inputSideMenu,true);
    }
    public void setInputs(InputList inputs,InputSideMenu inputSideMenu) {
        setInputs(inputs,false);
        setInputSideMenu(inputSideMenu,false);
        reset();
    }
    public Input getInputAt(int tick) {
        return inputs.get(tick);
    }
    public Input getCurrentInput() {
        int tick=computedTicksN-1;
        return getInputAt(tick);
    }
    public Input getLastInput() {
        int tick=computedTicksN-1;
        return getInputAt(tick-1);
    }
    public EntityPlayerSPLike getVirtualPlayer() {
        return virtualPlayer;
    }
    private Vec3 getStartPosition() {
        return inputSideMenu.getStartPosition();
    }
    private Vec3 getStartMotion() {
        return inputSideMenu.getStartMotion();
    }
    public ArrayList<SimulatedPlayerInfo> getPlayerStates() {
        return playerStates;
    }
    public SimulatedPlayerInfo getPlayerStateAtIndex(int index) {
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
        playerStates=(ArrayList<SimulatedPlayerInfo>)importedPlayerStates.clone();
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
        playerStates.add(new SimulatedPlayerInfo(virtualPlayer));
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
    public void loadPlayerInfo(ArrayList<SimulatedPlayerInfo> playerStates) {
        importedPlayerStates=(ArrayList<SimulatedPlayerInfo>)playerStates.clone();
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
                    SimulatedPlayerInfo playerState=importedPlayerStates.get(computedTicksN);
                    SimulatorUtil.apply(virtualPlayer,SimulatorUtil.changeDurations(playerState.getPotionEffects(),2));
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
            virtualPlayer.setPosition(getStartPosition().xCoord,getStartPosition().yCoord,getStartPosition().zCoord);
            virtualPlayer.motionX=getStartMotion().xCoord;
            virtualPlayer.motionY=getStartMotion().yCoord;
            virtualPlayer.motionZ=getStartMotion().zCoord;
        } else {
            SimulatedPlayerInfo playerState=playerStates.get(startTick);
            virtualPlayer.setPositionAndRotation(playerState.getPositionX(),playerState.getPositionY(),playerState.getPositionZ(),playerState.getRotationYaw(),playerState.getRotationPitch());
            virtualPlayer.motionX=playerState.getMotionX();
            virtualPlayer.motionY=playerState.getMotionY();
            virtualPlayer.motionZ=playerState.getMotionZ();
            virtualPlayer.setSprinting(playerState.isSprinting());
            virtualPlayer.onGround=playerState.isOnGround();
            virtualPlayer.isCollidedHorizontally=playerState.isCollidedHorizontally();
            virtualPlayer.isCollidedVertically=playerState.isCollidedVertically();
            virtualPlayer.jumpTicks=playerState.getJumpTicks();
            virtualPlayer.sprintToggleTimer=playerState.getSprintToggleTimer();
            virtualPlayer.sprintingTicksLeft=playerState.getSprintingTicksLeft();
            SimulatorUtil.apply(virtualPlayer,SimulatorUtil.changeDurations(playerState.getPotionEffects(),1));
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
        mc.theWorld.removePlayerEntityDangerously(virtualPlayer);
    }
}
