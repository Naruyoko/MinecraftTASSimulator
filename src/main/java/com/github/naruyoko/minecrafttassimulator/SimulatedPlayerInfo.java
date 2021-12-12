package com.github.naruyoko.minecrafttassimulator;

import net.minecraft.client.entity.EntityPlayerSP;

public class SimulatedPlayerInfo extends EntityMovementInfo {
    private boolean isSneaking;
    private boolean isSprinting;
    /**
     * {@link net.minecraft.client.entity.EntityPlayerSP#sprintingTicksLeft}
     */
    private int sprintingTicksLeft;
    /**
     * {@link net.minecraft.client.entity.EntityPlayerSP#sprintToggleTimer}
     */
    private int sprintToggleTimer;
    public SimulatedPlayerInfo(EntityPlayerSP player) {
        super(player);
        isSneaking=player.isSneaking();
        isSprinting=player.isSprinting();
        try {
            sprintingTicksLeft=SimulatorUtil.getSprintingTicksLeft(player);
            sprintToggleTimer=SimulatorUtil.getSprintToggleTimer(player);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public SimulatedPlayerInfo(EntityPlayerSPLike virtualPlayer) {
        super(virtualPlayer);
        isSneaking=virtualPlayer.isSneaking();
        isSprinting=virtualPlayer.isSprinting();
        sprintingTicksLeft=virtualPlayer.sprintingTicksLeft;
        sprintToggleTimer=virtualPlayer.sprintToggleTimer;
    }
    public SimulatedPlayerInfo() {
        super();
        isSneaking=false;
        isSprinting=false;
        sprintingTicksLeft=0;
        sprintToggleTimer=0;
    }
    public boolean isSneaking() {
        return isSneaking;
    }
    public void setSneaking(boolean isSneaking) {
        this.isSneaking=isSneaking;
    }
    public boolean isSprinting() {
        return isSprinting;
    }
    public void setSprinting(boolean isSprinting) {
        this.isSprinting=isSprinting;
    }
    public int getSprintingTicksLeft() {
        return sprintingTicksLeft;
    }
    public void setSprintingTicksLeft(int sprintingTicksLeft) {
        this.sprintingTicksLeft = sprintingTicksLeft;
    }
    public int getSprintToggleTimer() {
        return sprintToggleTimer;
    }
    public void setSprintToggleTimer(int sprintToggleTimer) {
        this.sprintToggleTimer = sprintToggleTimer;
    }
}
