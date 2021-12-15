package com.github.naruyoko.minecrafttassimulator;

import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;

public class EntityMovementInfo {
    private Class<? extends Entity> thisClass;
    private int entityID;
    private Vec3 position;
    private Vec3 motion;
    private boolean onGround;
    private boolean isCollidedHorizontally;
    private boolean isCollidedVertically;
    private float rotationYaw;
    private float rotationPitch;
    /**
     * {@link net.minecraft.client.entity.EntityLivingBase#jumpTicks}
     */
    private int jumpTicks;
    private Collection<PotionEffect> potionEffects;
    private boolean isRiding;
    private EntityMovementInfo ridingEntityInfo;
    public EntityMovementInfo(Entity entity) {
        thisClass=entity.getClass();
        entityID=entity.getEntityId();
        try {
            position=SimulatorUtil.getPositionVectorUseSendFromServer(entity);
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        motion=SimulatorUtil.getMotionVector(entity);
        onGround=entity.onGround;
        isCollidedHorizontally=entity.isCollidedHorizontally;
        isCollidedVertically=entity.isCollidedVertically;
        rotationYaw=entity.rotationYaw;
        rotationPitch=entity.rotationPitch;
        if (entity instanceof EntityLivingBase) {
            try {
                jumpTicks=SimulatorUtil.getJumpTicks((EntityLivingBase)entity);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            potionEffects=SimulatorUtil.clone(((EntityLivingBase)entity).getActivePotionEffects());
        } else {
            jumpTicks=0;
            potionEffects=null;
        }
        isRiding=entity.isRiding();
        ridingEntityInfo=isRiding?new EntityMovementInfo(entity.ridingEntity):null;
    }
    public EntityMovementInfo() {
        thisClass=null;
        entityID=0;
        position=new Vec3(0,0,0);
        motion=new Vec3(0,0,0);
        onGround=false;
        isCollidedHorizontally=false;
        isCollidedVertically=false;
        rotationYaw=0;
        rotationPitch=0;
        jumpTicks=0;
        potionEffects=null;
        isRiding=false;
        ridingEntityInfo=null;
    }
    public Class<? extends Entity> getThisClass() {
        return thisClass;
    }
    public void setThisClass(Class<? extends Entity> thisClass) {
        this.thisClass = thisClass;
    }
    public int getEntityID() {
        return entityID;
    }
    public void setEntityID(int entityID) {
        this.entityID = entityID;
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
    public boolean isOnGround() {
        return onGround;
    }
    public void setOnGround(boolean onGround) {
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
    public int getJumpTicks() {
        return jumpTicks;
    }
    public void setJumpTicks(int jumpTicks) {
        this.jumpTicks = jumpTicks;
    }
    public float getRotationPitch() {
        return rotationPitch;
    }
    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch=rotationPitch;
    }
    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }
    public void setPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }
    public boolean isRiding() {
        return isRiding;
    }
    public void setRiding(boolean isRiding) {
        this.isRiding = isRiding;
    }
    public EntityMovementInfo getRidingEntityInfo() {
        return ridingEntityInfo;
    }
    public void setRidingEntityInfo(EntityMovementInfo ridingEntityInfo) {
        this.ridingEntityInfo = ridingEntityInfo;
    }
}
