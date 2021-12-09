package com.github.naruyoko.minecrafttaseditor;

import java.util.Collection;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;

public class MinecraftTASEditorPlayerInfo {
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
    public MinecraftTASEditorPlayerInfo(EntityPlayerSP player) {
        position=MinecraftTASEditorUtil.getPositionVector(player);
        motion=MinecraftTASEditorUtil.getMotionVector(player);
        isSneaking=player.func_70093_af();
        isSprinting=player.func_70051_ag();
        onGround=player.field_70122_E;
        isCollidedHorizontally=player.field_70123_F;
        isCollidedVertically=player.field_70124_G;
        rotationYaw=player.field_70177_z;
        rotationPitch=player.field_70125_A;
        try {
            jumpTicks=MinecraftTASEditorUtil.getJumpTicks(player);
            sprintingTicksLeft=MinecraftTASEditorUtil.getSprintingTicksLeft(player);
            sprintToggleTimer=MinecraftTASEditorUtil.getSprintToggleTimer(player);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        potionEffects=MinecraftTASEditorUtil.clone(player.func_70651_bq());
    }
    public MinecraftTASEditorPlayerInfo(MinecraftTASEditorEntityPlayerSPLike virtualPlayer) {
        position=MinecraftTASEditorUtil.getPositionVector(virtualPlayer);
        motion=MinecraftTASEditorUtil.getMotionVector(virtualPlayer);
        isSneaking=virtualPlayer.func_70093_af();
        isSprinting=virtualPlayer.func_70051_ag();
        onGround=virtualPlayer.field_70122_E;
        isCollidedHorizontally=virtualPlayer.field_70123_F;
        isCollidedVertically=virtualPlayer.field_70124_G;
        rotationYaw=virtualPlayer.field_70177_z;
        rotationPitch=virtualPlayer.field_70125_A;
        try {
            jumpTicks=MinecraftTASEditorUtil.getJumpTicks(virtualPlayer);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sprintingTicksLeft=virtualPlayer.sprintingTicksLeft;
        sprintToggleTimer=virtualPlayer.sprintToggleTimer;
        potionEffects=MinecraftTASEditorUtil.changeDurations(virtualPlayer.func_70651_bq(),-1);
    }
    public MinecraftTASEditorPlayerInfo() {
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
        this.position=MinecraftTASEditorUtil.clone(position);
    }
    public double getPositionX() {
        return position.field_72450_a;
    }
    public void setPositionX(double xCoord) {
        position=new Vec3(xCoord,position.field_72448_b,position.field_72449_c);
    }
    public double getPositionY() {
        return position.field_72448_b;
    }
    public void setPositionY(double yCoord) {
        position=new Vec3(position.field_72450_a,yCoord,position.field_72449_c);
    }
    public double getPositionZ() {
        return position.field_72449_c;
    }
    public void setPositionZ(double zCoord) {
        position=new Vec3(position.field_72450_a,position.field_72448_b,zCoord);
    }
    public Vec3 getMotion() {
        return motion;
    }
    public void setMotion(Vec3 motion) {
        this.motion=MinecraftTASEditorUtil.clone(motion);
    }
    public double getMotionX() {
        return motion.field_72450_a;
    }
    public double getMotionXMeters() {
        return getMotionX()*20;
    }
    public void setMotionX(double xCoord) {
        motion=new Vec3(xCoord,motion.field_72448_b,motion.field_72449_c);
    }
    public double getMotionY() {
        return motion.field_72448_b;
    }
    public double getMotionYMeters() {
        return getMotionY()*20;
    }
    public void setMotionY(double yCoord) {
        motion=new Vec3(motion.field_72450_a,yCoord,motion.field_72449_c);
    }
    public double getMotionZ() {
        return motion.field_72449_c;
    }
    public double getMotionZMeters() {
        return getMotionZ()*20;
    }
    public void setMotionZ(double zCoord) {
        motion=new Vec3(motion.field_72450_a,motion.field_72448_b,zCoord);
    }
    public double getHorizontalSpeed() {
        return Math.sqrt(Math.pow(motion.field_72450_a,2)+Math.pow(motion.field_72449_c,2));
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
