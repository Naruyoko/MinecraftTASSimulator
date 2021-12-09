package com.github.naruyoko.minecrafttassimulator;

import java.util.Collection;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;

public class SimulatedPlayerInfo {
    private Vec3 position;
    private Vec3 motion;
    private boolean isSneaking;
    private boolean isSprinting;
    private boolean onGround;
    private boolean isCollidedHorizontally;
    private boolean isCollidedVertically;
    private float rotationYaw;
    private float rotationPitch;
    /**
     * {@link net.minecraft.client.entity.EntityLivingBase#jumpTicks}
     */
    private int jumpTicks;
    /**
     * {@link net.minecraft.client.entity.EntityPlayerSP#sprintingTicksLeft}
     */
    private int sprintingTicksLeft;
    /**
     * {@link net.minecraft.client.entity.EntityPlayerSP#sprintToggleTimer}
     */
    private int sprintToggleTimer;
    private Collection<PotionEffect> potionEffects;
    public SimulatedPlayerInfo(EntityPlayerSP player) {
        position=SimulatorUtil.getPositionVector(player);
        motion=SimulatorUtil.getMotionVector(player);
        isSneaking=player.isSneaking();
        isSprinting=player.isSprinting();
        onGround=player.onGround;
        isCollidedHorizontally=player.isCollidedHorizontally;
        isCollidedVertically=player.isCollidedVertically;
        rotationYaw=player.rotationYaw;
        rotationPitch=player.rotationPitch;
        try {
            jumpTicks=SimulatorUtil.getJumpTicks(player);
            sprintingTicksLeft=SimulatorUtil.getSprintingTicksLeft(player);
            sprintToggleTimer=SimulatorUtil.getSprintToggleTimer(player);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        potionEffects=SimulatorUtil.clone(player.getActivePotionEffects());
    }
    public SimulatedPlayerInfo(EntityPlayerSPLike virtualPlayer) {
        position=SimulatorUtil.getPositionVector(virtualPlayer);
        motion=SimulatorUtil.getMotionVector(virtualPlayer);
        isSneaking=virtualPlayer.isSneaking();
        isSprinting=virtualPlayer.isSprinting();
        onGround=virtualPlayer.onGround;
        isCollidedHorizontally=virtualPlayer.isCollidedHorizontally;
        isCollidedVertically=virtualPlayer.isCollidedVertically;
        rotationYaw=virtualPlayer.rotationYaw;
        rotationPitch=virtualPlayer.rotationPitch;
        try {
            jumpTicks=SimulatorUtil.getJumpTicks(virtualPlayer);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sprintingTicksLeft=virtualPlayer.sprintingTicksLeft;
        sprintToggleTimer=virtualPlayer.sprintToggleTimer;
        potionEffects=SimulatorUtil.changeDurations(virtualPlayer.getActivePotionEffects(),-1);
    }
    public SimulatedPlayerInfo() {
        position=new Vec3(0,0,0);
        motion=new Vec3(0,0,0);
        isSneaking=false;
        isSprinting=false;
        onGround=false;
        isCollidedHorizontally=false;
        isCollidedVertically=false;
        rotationYaw=0;
        rotationPitch=0;
        jumpTicks=0;
        sprintingTicksLeft=0;
        sprintToggleTimer=0;
        potionEffects=null;
    }
    public Vec3 getPosition() {
        return position;
    }
    public void setPosition(Vec3 position) {
        this.position=SimulatorUtil.clone(position);
    }
    public double getPositionX() {
        return position.xCoord;
    }
    public void setPositionX(double xCoord) {
        position=new Vec3(xCoord,position.yCoord,position.zCoord);
    }
    public double getPositionY() {
        return position.yCoord;
    }
    public void setPositionY(double yCoord) {
        position=new Vec3(position.xCoord,yCoord,position.zCoord);
    }
    public double getPositionZ() {
        return position.zCoord;
    }
    public void setPositionZ(double zCoord) {
        position=new Vec3(position.xCoord,position.yCoord,zCoord);
    }
    public Vec3 getMotion() {
        return motion;
    }
    public void setMotion(Vec3 motion) {
        this.motion=SimulatorUtil.clone(motion);
    }
    public double getMotionX() {
        return motion.xCoord;
    }
    public double getMotionXMeters() {
        return getMotionX()*20;
    }
    public void setMotionX(double xCoord) {
        motion=new Vec3(xCoord,motion.yCoord,motion.zCoord);
    }
    public double getMotionY() {
        return motion.yCoord;
    }
    public double getMotionYMeters() {
        return getMotionY()*20;
    }
    public void setMotionY(double yCoord) {
        motion=new Vec3(motion.xCoord,yCoord,motion.zCoord);
    }
    public double getMotionZ() {
        return motion.zCoord;
    }
    public double getMotionZMeters() {
        return getMotionZ()*20;
    }
    public void setMotionZ(double zCoord) {
        motion=new Vec3(motion.xCoord,motion.yCoord,zCoord);
    }
    public double getHorizontalSpeed() {
        return Math.sqrt(Math.pow(motion.xCoord,2)+Math.pow(motion.zCoord,2));
    }
    public double getHorizontalSpeedMeters() {
        return getHorizontalSpeed()*20;
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
    public boolean isOnGround() {
        return onGround;
    }
    public void setGrounded(boolean onGround) {
        this.onGround=onGround;
    }
    public boolean isCollidedHorizontally() {
        return isCollidedHorizontally;
    }
    public void setCollidedHorizontally(boolean isCollidedHorizontally) {
        this.isCollidedHorizontally=isCollidedHorizontally;
    }
    public boolean isCollidedVertically() {
        return isCollidedVertically;
    }
    public void setCollidedVertically(boolean isCollidedVertically) {
        this.isCollidedVertically=isCollidedVertically;
    }
    public float getRotationYaw() {
        return rotationYaw;
    }
    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw=rotationYaw;
    }
    public float getRotationPitch() {
        return rotationPitch;
    }
    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch=rotationPitch;
    }
    public int getJumpTicks() {
        return jumpTicks;
    }
    public void setJumpTicks(int jumpTicks) {
        this.jumpTicks = jumpTicks;
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
    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }
    public void setPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }
}
