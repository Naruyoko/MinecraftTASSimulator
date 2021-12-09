package com.github.naruyoko.minecrafttaseditor;

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

/**
 * Partly uses {@link net.minecraftforge.common.util.FakePlayer}
 */
public class MinecraftTASEditorEntityPlayerSPLike extends EntityPlayer {
    private MinecraftTASEditorInput input;
    protected Minecraft mc;
    public MinecraftTASEditorMovementInputLike movementInput;
    public int jumpTicks;
    public int sprintToggleTimer;
    public int sprintingTicksLeft;
    public MinecraftTASEditorEntityPlayerSPLike(Minecraft mcIn) {
        super(mcIn.func_71401_C().func_71218_a(mcIn.field_71439_g.field_71093_bK),new GameProfile(MathHelper.func_180182_a(new Random()),"FakePlayer"));
        this.mc=mcIn;
        this.movementInput=new MinecraftTASEditorMovementInputLike();
        this.field_71093_bK=0;
        this.input=new MinecraftTASEditorInput();
    }
    public Vec3 func_174791_d(){
        return super.func_174791_d();
    }
    public boolean func_70097_a(DamageSource source,float amount) {return false;}
    public void func_70691_i(float healAmount) {}
    public void func_70071_h_() {
        if (this.field_70170_p.func_175667_e(new BlockPos(this.field_70165_t,0.0D,this.field_70161_v))) {
            MinecraftTASEditorEditor.getPredictor().simulateNextTick(true);
            super.func_70071_h_();
            MinecraftTASEditorEditor.getPredictor().simulateNextTick(false);
        }
    }
    public boolean func_180431_b(DamageSource source){
        return mc.field_71439_g.func_180431_b(source);
    }
    public boolean func_175144_cb() {
        return true;
    }
    private boolean isHeadspaceFree(BlockPos pos,int height) {
        for (int y=0;y<height;y++) {
            if (!isOpenBlockSpace(pos.func_177982_a(0,y,0))) return false;
        }
        return true;
    }
    protected boolean func_145771_j(double x,double y,double z) {
        BlockPos blockpos=new BlockPos(x, y, z);
        double d0=x-(double)blockpos.func_177958_n();
        double d1=z-(double)blockpos.func_177952_p();
        int entHeight=Math.max((int)Math.ceil(this.field_70131_O),1);
        boolean inTranslucentBlock=!this.isHeadspaceFree(blockpos,entHeight);
        if (inTranslucentBlock) {
            int i=-1;
            double d2=9999.0D;
            if (this.isHeadspaceFree(blockpos.func_177976_e(),entHeight)&&d0<d2) {
                d2=d0;
                i=0;
            }
            if (this.isHeadspaceFree(blockpos.func_177974_f(),entHeight)&&1.0D-d0<d2) {
                d2=1.0D-d0;
                i=1;
            }
            if (this.isHeadspaceFree(blockpos.func_177978_c(),entHeight)&&d1<d2) {
                d2=d1;
                i=4;
            }
            if (this.isHeadspaceFree(blockpos.func_177968_d(),entHeight)&&1.0D-d1<d2) {
                d2=1.0D-d1;
                i=5;
            }
            float f=0.1F;
            if (i==0) this.field_70159_w=(double)(-f);
            if (i==1) this.field_70159_w=(double)f;
            if (i==4) this.field_70179_y=(double)(-f);
            if (i==5) this.field_70179_y=(double)f;
        }
        return false;
    }
    private boolean isOpenBlockSpace(BlockPos pos) {
        return !this.field_70170_p.func_180495_p(pos).func_177230_c().func_149721_r();
    }
    public void func_70031_b(boolean sprinting) {
        super.func_70031_b(sprinting);
        this.sprintingTicksLeft=sprinting?600:0;
    }
    public BlockPos func_180425_c() {
        return new BlockPos(this.field_70165_t+0.5D,this.field_70163_u+0.5D,this.field_70161_v+0.5D);
    }
    public boolean func_70613_aW() {
        return true;
    }
    public boolean func_70093_af() {
        return this.movementInput.field_78899_d;
    }
    public void func_70626_be() {
        super.func_70626_be();
        if (this.isCurrentViewEntity()) {
            this.field_70702_br=this.movementInput.field_78902_a;
            this.field_70701_bs=this.movementInput.field_78900_b;
            this.field_70703_bu=this.movementInput.field_78901_c;
        }
    }
    protected boolean isCurrentViewEntity() {
        return true;
    }
    public void func_70636_d() {
        if (this.sprintingTicksLeft>0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft==0) {
                this.func_70031_b(false);
            }
        }
        if (this.sprintToggleTimer>0) {
            --this.sprintToggleTimer;
        }
        boolean isSneakKeydown=this.movementInput.field_78899_d;
        float f=0.8F;
        boolean isAcceleratingFast=this.movementInput.field_78900_b>=f;
        this.movementInput.updatePlayerMoveState(input);
        this.func_145771_j(this.field_70165_t-(double)this.field_70130_N*0.35D,this.func_174813_aQ().field_72338_b+0.5D,this.field_70161_v+(double)this.field_70130_N*0.35D);
        this.func_145771_j(this.field_70165_t-(double)this.field_70130_N*0.35D,this.func_174813_aQ().field_72338_b+0.5D,this.field_70161_v-(double)this.field_70130_N*0.35D);
        this.func_145771_j(this.field_70165_t+(double)this.field_70130_N*0.35D,this.func_174813_aQ().field_72338_b+0.5D,this.field_70161_v-(double)this.field_70130_N*0.35D);
        this.func_145771_j(this.field_70165_t+(double)this.field_70130_N*0.35D,this.func_174813_aQ().field_72338_b+0.5D,this.field_70161_v+(double)this.field_70130_N*0.35D);
        boolean canSprint=true;
        if (this.field_70122_E&&!isSneakKeydown&&!isAcceleratingFast&&this.movementInput.field_78900_b>=f&&!this.func_70051_ag()&&canSprint&&!this.func_71039_bw()&&!this.func_70644_a(Potion.field_76440_q)) {
            if (this.sprintToggleTimer<=0&&!input.isKeySprint()) {
                this.sprintToggleTimer=7;
            } else {
                this.func_70031_b(true);
            }
        }
        if (!this.func_70051_ag()&&this.movementInput.field_78900_b>=f&&canSprint&&!this.func_71039_bw()&&!this.func_70644_a(Potion.field_76440_q)&&input.isKeySprint()) {
            this.func_70031_b(true);
        }
        if (this.func_70051_ag()&&(this.movementInput.field_78900_b<f||this.field_70123_F||!canSprint)) {
            this.func_70031_b(false);
        }
        super.func_70636_d();
    }
    public void setInputs(MinecraftTASEditorInput input) {
        this.input=input;
        this.field_70177_z=(float)input.getRotationYaw();
        this.field_70125_A=(float)input.getRotationPitch();
    }
    
    //@Override public Vec3 getPositionVector(){ return new Vec3(0, 0, 0); }
    @Override public boolean func_70003_b(int i, String s){ return false; }
    @Override public void func_146105_b(IChatComponent chatmessagecomponent){}
    @Override public void func_71064_a(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    //@Override public boolean isEntityInvulnerable(DamageSource source){ return true; }
    @Override public boolean func_96122_a(EntityPlayer player){ return false; }
    @Override public void func_70645_a(DamageSource source){ return; }
    //@Override public void onUpdate(){ return; }
    @Override public void func_71027_c(int dim){ return; }
    @Override
    public boolean func_175149_v() {
        // TODO Auto-generated method stub
        return false;
    }
}
