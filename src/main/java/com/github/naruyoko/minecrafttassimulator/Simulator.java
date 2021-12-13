package com.github.naruyoko.minecrafttassimulator;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import com.github.naruyoko.minecrafttassimulator.Input.MouseButtonInputEnum;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Simulator {
    private static Minecraft mc=Minecraft.getMinecraft();
    private InputList inputs=null;
    private InputSideMenu inputSideMenu=new InputSideMenu();
    private ArrayList<SimulatedPlayerInfo> playerStates=null;
    private int computedTicksN;
    private int targetTick;
    private boolean isRunning;
    private Runnable callbackOnFinish;
    private Runnable callbackOnAbort;
    public Simulator(InputList inputs,InputSideMenu inputSideMenu) {
        this.inputs=inputs;
        this.inputSideMenu=inputSideMenu;
        playerStates=new ArrayList<SimulatedPlayerInfo>();
        computedTicksN=0;
        isRunning=false;
        targetTick=-1;
        callbackOnFinish=null;
        resetInBackground();
    }
    public Simulator(InputSideMenu inputSideMenu) {
        this(new InputList(),inputSideMenu);
    }
    public Simulator(InputList inputs) {
        this(inputs,new InputSideMenu());
    }
    public Simulator() {
        this(new InputList());
    }
    public void setInputs(InputList inputs,boolean reset) {
        this.inputs=inputs;
        if (reset) resetInBackground();
    }
    public void setInputs(InputList inputs) {
        setInputs(inputs,true);
    }
    public void setInputSideMenu(InputSideMenu inputSideMenu,boolean reset) {
        this.inputSideMenu=inputSideMenu;
        if (reset) resetInBackground();
    }
    public void setInputSideMenu(InputSideMenu inputSideMenu) {
        setInputSideMenu(inputSideMenu,true);
    }
    public void setInputs(InputList inputs,InputSideMenu inputSideMenu) {
        setInputs(inputs,false);
        setInputSideMenu(inputSideMenu,false);
        resetInBackground();
    }
    private Vec3 getStartPosition() {
        return inputSideMenu.getStartPosition();
    }
    private Vec3 getStartMotion() {
        return inputSideMenu.getStartMotion();
    }
    private GameType getStartGameType() {
        return inputSideMenu.getStartGametype();
    }
    private int getStartInvulnerabilityFrames() {
        return inputSideMenu.getStartInvulnerabilityFrames();
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
    public void reset(boolean appendFirst,boolean silent) {
        //resetAtTick(0);
        computedTicksN=0;
        playerStates=new ArrayList<SimulatedPlayerInfo>();
        if (!silent) {
            mc.thePlayer.setPositionAndUpdate(getStartPosition().xCoord,getStartPosition().yCoord,getStartPosition().zCoord);
            if (inputs.size()>0) mc.thePlayer.setPositionAndRotation(getStartPosition().xCoord,getStartPosition().yCoord,getStartPosition().zCoord,(float)inputs.get(0).getRotationYaw(),(float)inputs.get(0).getRotationPitch());
            else mc.thePlayer.setPosition(getStartPosition().xCoord,getStartPosition().yCoord,getStartPosition().zCoord);
            mc.thePlayer.motionX=getStartMotion().xCoord;
            mc.thePlayer.motionY=getStartMotion().yCoord;
            mc.thePlayer.motionZ=getStartMotion().zCoord;
            if (getStartGameType()!=null&&!getStartGameType().equals(GameType.NOT_SET)) mc.playerController.setGameType(getStartGameType());
            try {
                SimulatorUtil.setRespawnInvulnerabilityTicks(SimulatorUtil.getPlayerMP(mc),getStartInvulnerabilityFrames());
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
        playerStates.add(new SimulatedPlayerInfo(mc.thePlayer));
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
                Input input = inputs.get(tick);
                input.apply(mc);
                ByteBuffer mouseByteBuffer=null;
                try {
                    mouseByteBuffer=SimulatorUtil.getMouseByteBuffer();
                } catch (IllegalArgumentException e) {
                    MinecraftTASSimulatorMod.logger.error("Failed to access Mouse.readBuffer");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    MinecraftTASSimulatorMod.logger.error("Failed to access Mouse.readBuffer");
                    e.printStackTrace();
                }
                if (mouseByteBuffer!=null) {
                    try {
                        /*
                         * Structure:
                         * * byte 1 eventButton
                         * * byte 1 eventState
                         * * int 4 event_dx/new_event_dx
                         * * int 4 event_dy/new_event_dy
                         * * int 4 event_dwheel
                         * * long 8 event_nanos
                         * See org.lwjgl.input.Mouse#read()
                         * */
                        for (MouseButtonInputEnum v:input.getMouseButtonInputs()) {
                            mouseByteBuffer
                                .position(mouseByteBuffer.limit())
                                .limit(mouseByteBuffer.limit()+Mouse.EVENT_SIZE);
                            mouseByteBuffer
                                .put(v.getButton())
                                .put(v.getState())
                                .putInt(0)
                                .putInt(0)
                                .putInt(0)
                                .putLong(System.nanoTime());
                        }
                        mouseByteBuffer.position(0);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (event.phase==TickEvent.Phase.END) {
            if (mc.isGamePaused()) return;
            appendState();
        }
    }
    public void start() {
        reset(false);
        isRunning=true;
    }
    public void stop() {
        isRunning=false;
        new Input().apply(mc);
        if (callbackOnFinish!=null) callbackOnFinish.run();
    }
    public void abort() {
        isRunning=false;
        new Input().apply(mc);
        if (callbackOnAbort!=null) callbackOnAbort.run();
    }
    /**
     * Be sure to call this when destroying.
     */
    public void cleanup() {}
}
