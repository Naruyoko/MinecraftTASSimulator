package com.github.naruyoko.minecrafttaseditor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class MinecraftTASEditorInput {
    private boolean keyForward=false;
    private boolean keyLeft=false;
    private boolean keyBackward=false;
    private boolean keyRight=false;
    private boolean keyJump=false;
    private boolean keySneak=false;
    private boolean keySprint=false;
    private double rotationYaw=0;
    private double rotationPitch=0;
    private boolean isRotationExact=true;
    public MinecraftTASEditorInput(boolean keyForward,boolean keyLeft,boolean keyBackward,boolean keyRight,boolean keyJump,boolean keySneak,boolean keySprint,double rotationYaw,double rotationPitch,boolean isRotationExact) {
        this.keyForward=keyForward;
        this.keyLeft=keyLeft;
        this.keyBackward=keyBackward;
        this.keyRight=keyRight;
        this.keyJump=keyJump;
        this.keySneak=keySneak;
        this.keySprint=keySprint;
        this.rotationYaw=rotationYaw;
        this.rotationPitch=rotationPitch;
        this.isRotationExact=isRotationExact;
    }
    public MinecraftTASEditorInput() {
        this(false,false,false,false,false,false,false,0,0,false);
    }
    @Override
    public MinecraftTASEditorInput clone() {
        return new MinecraftTASEditorInput(keyForward,keyLeft,keyBackward,keyRight,keyJump,keySneak,keySprint,rotationYaw,rotationPitch,isRotationExact);
    }
    public boolean isKeyForward() {
        return keyForward;
    }
    public void setKeyForward(boolean keyForward) {
        this.keyForward = keyForward;
    }
    public boolean isKeyLeft() {
        return keyLeft;
    }
    public void setKeyLeft(boolean keyLeft) {
        this.keyLeft = keyLeft;
    }
    public boolean isKeyBackward() {
        return keyBackward;
    }
    public void setKeyBackward(boolean keyBackward) {
        this.keyBackward = keyBackward;
    }
    public boolean isKeyRight() {
        return keyRight;
    }
    public void setKeyRight(boolean keyRight) {
        this.keyRight = keyRight;
    }
    public boolean isKeyJump() {
        return keyJump;
    }
    public void setKeyJump(boolean keyJump) {
        this.keyJump = keyJump;
    }
    public boolean isKeySneak() {
        return keySneak;
    }
    public void setKeySneak(boolean keySneak) {
        this.keySneak = keySneak;
    }
    public boolean isKeySprint() {
        return keySprint;
    }
    public void setKeySprint(boolean keySprint) {
        this.keySprint = keySprint;
    }
    public double getRotationYaw() {
        return rotationYaw;
    }
    public void setRotationYaw(double rotationYaw) {
        this.rotationYaw = rotationYaw;
    }
    public double getRotationPitch() {
        return rotationPitch;
    }
    public void setRotationPitch(double rotationPitch) {
        this.rotationPitch = rotationPitch;
    }
    public boolean isRotationExact() {
        return isRotationExact;
    }
    public void setRotationExact(boolean isRotationExact) {
        this.isRotationExact = isRotationExact;
    }
    /**
     * {@link net.minecraft.util.MovementInputFromOptions#updatePlayerMoveState()}
     */
    public float movementForward() {
        float r=0.0F;
        if (keyForward) ++r;
        if (keyBackward) --r;
        if (keySneak) r=(float)((double)r*0.3D);
        return r;
    }
    /**
     * {@link net.minecraft.util.MovementInputFromOptions#updatePlayerMoveState()}
     */
    public float movementStrafe() {
        float r=0.0F;
        if (keyLeft) ++r;
        if (keyRight) --r;
        if (keySneak) r=(float)((double)r*0.3D);
        return r;
    }
    /**
     * Creates a new {@link MinecraftTASEditorInput}
     * @param player
     * @param gameSettings
     * @return
     */
    public static MinecraftTASEditorInput from(EntityPlayerSP player,GameSettings gameSettings) {
        return new MinecraftTASEditorInput(
                gameSettings.field_74351_w.func_151470_d(),
                gameSettings.field_74370_x.func_151470_d(),
                gameSettings.field_74368_y.func_151470_d(),
                gameSettings.field_74366_z.func_151470_d(),
                gameSettings.field_74314_A.func_151470_d(),
                gameSettings.field_74311_E.func_151470_d(),
                gameSettings.field_151444_V.func_151470_d(),
                player.field_70177_z,
                player.field_70125_A,
                false);
    }
    /**
     * Creates a new {@link MinecraftTASEditorInput}
     * @param mc
     * @return
     */
    public static MinecraftTASEditorInput from(Minecraft mc) {
        return MinecraftTASEditorInput.from(mc.field_71439_g,mc.field_71474_y);
    }
    /**
     * Applies this to the player and settings provided.
     * @param player
     * @param gameSettings
     */
    public void apply(EntityPlayerSP player,GameSettings gameSettings) {
        KeyBinding.func_74510_a(gameSettings.field_74351_w.func_151463_i(),keyForward);
        KeyBinding.func_74510_a(gameSettings.field_74370_x.func_151463_i(),keyLeft);
        KeyBinding.func_74510_a(gameSettings.field_74368_y.func_151463_i(),keyBackward);
        KeyBinding.func_74510_a(gameSettings.field_74366_z.func_151463_i(),keyRight);
        KeyBinding.func_74510_a(gameSettings.field_74314_A.func_151463_i(),keyJump);
        KeyBinding.func_74510_a(gameSettings.field_74311_E.func_151463_i(),keySneak);
        KeyBinding.func_74510_a(gameSettings.field_151444_V.func_151463_i(),keySprint);
        player.field_70177_z=(float)rotationYaw;
        player.field_70125_A=(float)rotationPitch;
    }
}