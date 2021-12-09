package com.github.naruyoko.minecrafttassimulator;

import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityPlayerSPLike extends EntityPlayer {
    private Input input;
    protected Minecraft mc;
    public MovementInputLike movementInput;
    public int jumpTicks;
    public int sprintToggleTimer;
    public int sprintingTicksLeft;
    public EntityPlayerSPLike(Minecraft mcIn) {
        super(mcIn.getIntegratedServer().worldServerForDimension(mcIn.thePlayer.dimension),new GameProfile(MathHelper.getRandomUuid(new Random()),"FakePlayer"));
        this.mc=mcIn;
        this.movementInput=new MovementInputLike();
        this.dimension=0;
        this.input=new Input();
    }
    public Vec3 getPositionVector(){
        return super.getPositionVector();
    }
    public boolean attackEntityFrom(DamageSource source,float amount) {return false;}
    public void heal(float healAmount) {}
    public void onUpdate() {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX,0.0D,this.posZ))) {
            InputEditor.getPredictor().simulateNextTick(true);
            super.onUpdate();
            InputEditor.getPredictor().simulateNextTick(false);
        }
    }
    public boolean isEntityInvulnerable(DamageSource source){
        return mc.thePlayer.isEntityInvulnerable(source);
    }
    public boolean isUser() {
        return true;
    }
    private boolean isHeadspaceFree(BlockPos pos,int height) {
        for (int y=0;y<height;y++) {
            if (!isOpenBlockSpace(pos.add(0,y,0))) return false;
        }
        return true;
    }
    protected boolean pushOutOfBlocks(double x,double y,double z) {
        BlockPos blockpos=new BlockPos(x, y, z);
        double d0=x-(double)blockpos.getX();
        double d1=z-(double)blockpos.getZ();
        int entHeight=Math.max((int)Math.ceil(this.height),1);
        boolean inTranslucentBlock=!this.isHeadspaceFree(blockpos,entHeight);
        if (inTranslucentBlock) {
            int i=-1;
            double d2=9999.0D;
            if (this.isHeadspaceFree(blockpos.west(),entHeight)&&d0<d2) {
                d2=d0;
                i=0;
            }
            if (this.isHeadspaceFree(blockpos.east(),entHeight)&&1.0D-d0<d2) {
                d2=1.0D-d0;
                i=1;
            }
            if (this.isHeadspaceFree(blockpos.north(),entHeight)&&d1<d2) {
                d2=d1;
                i=4;
            }
            if (this.isHeadspaceFree(blockpos.south(),entHeight)&&1.0D-d1<d2) {
                d2=1.0D-d1;
                i=5;
            }
            float f=0.1F;
            if (i==0) this.motionX=(double)(-f);
            if (i==1) this.motionX=(double)f;
            if (i==4) this.motionZ=(double)(-f);
            if (i==5) this.motionZ=(double)f;
        }
        return false;
    }
    private boolean isOpenBlockSpace(BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube();
    }
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft=sprinting?600:0;
    }
    public BlockPos getPosition() {
        return new BlockPos(this.posX+0.5D,this.posY+0.5D,this.posZ+0.5D);
    }
    public boolean isServerWorld() {
        return true;
    }
    public boolean isSneaking() {
        return this.movementInput.sneak;
    }
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing=this.movementInput.moveStrafe;
            this.moveForward=this.movementInput.moveForward;
            this.isJumping=this.movementInput.jump;
        }
    }
    protected boolean isCurrentViewEntity() {
        return true;
    }
    public void onLivingUpdate() {
        if (this.sprintingTicksLeft>0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft==0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer>0) {
            --this.sprintToggleTimer;
        }
        boolean isSneakKeydown=this.movementInput.sneak;
        float f=0.8F;
        boolean isAcceleratingFast=this.movementInput.moveForward>=f;
        this.movementInput.updatePlayerMoveState(input);
        this.pushOutOfBlocks(this.posX-(double)this.width*0.35D,this.getEntityBoundingBox().minY+0.5D,this.posZ+(double)this.width*0.35D);
        this.pushOutOfBlocks(this.posX-(double)this.width*0.35D,this.getEntityBoundingBox().minY+0.5D,this.posZ-(double)this.width*0.35D);
        this.pushOutOfBlocks(this.posX+(double)this.width*0.35D,this.getEntityBoundingBox().minY+0.5D,this.posZ-(double)this.width*0.35D);
        this.pushOutOfBlocks(this.posX+(double)this.width*0.35D,this.getEntityBoundingBox().minY+0.5D,this.posZ+(double)this.width*0.35D);
        boolean canSprint=true;
        if (this.onGround&&!isSneakKeydown&&!isAcceleratingFast&&this.movementInput.moveForward>=f&&!this.isSprinting()&&canSprint&&!this.isUsingItem()&&!this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer<=0&&!input.isKeySprint()) {
                this.sprintToggleTimer=7;
            } else {
                this.setSprinting(true);
            }
        }
        if (!this.isSprinting()&&this.movementInput.moveForward>=f&&canSprint&&!this.isUsingItem()&&!this.isPotionActive(Potion.blindness)&&input.isKeySprint()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()&&(this.movementInput.moveForward<f||this.isCollidedHorizontally||!canSprint)) {
            this.setSprinting(false);
        }
        super.onLivingUpdate();
    }
    public void setInputs(Input input) {
        this.input=input;
        this.rotationYaw=(float)input.getRotationYaw();
        this.rotationPitch=(float)input.getRotationPitch();
    }
    
    //@Override public Vec3 getPositionVector(){ return new Vec3(0, 0, 0); }
    @Override public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override public void addChatComponentMessage(IChatComponent chatmessagecomponent){}
    @Override public void addStat(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    //@Override public boolean isEntityInvulnerable(DamageSource source){ return true; }
    @Override public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    //@Override public void onUpdate(){ return; }
    @Override public void travelToDimension(int dim){ return; }
    @Override
    public boolean isSpectator() {
        // TODO Auto-generated method stub
        return false;
    }
}
