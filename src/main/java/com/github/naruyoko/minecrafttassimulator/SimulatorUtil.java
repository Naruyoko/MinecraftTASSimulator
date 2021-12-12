package com.github.naruyoko.minecrafttassimulator;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.lwjgl.input.Mouse;

import com.github.naruyoko.minecrafttassimulator.Input.MouseButtonInputEnum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class SimulatorUtil {
    public static boolean isInteger(String value) {
        return Pattern.matches("^-?\\d+$",value);
    }
    /**
     * @param entity The entity to get the vector from.
     * @return A {@link Vec3} representing the position vector of the entity.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static Vec3 getPositionVector(Entity entity) throws IllegalArgumentException, IllegalAccessException {
        if (entity instanceof EntityLivingBase&&getNewPosRotationIncrements((EntityLivingBase)entity)>0) return new Vec3(getNewPosX((EntityLivingBase)entity),getNewPosY((EntityLivingBase)entity),getNewPosZ((EntityLivingBase)entity));
        else return entity.getPositionVector();
    }
    /**
     * @param entity The entity to get the vector from.
     * @return A {@link Vec3} representing the motion vector of the entity.
     */
    public static Vec3 getMotionVector(Entity entity) {
        return new Vec3(entity.motionX,entity.motionY,entity.motionZ);
    }
    /**
     * @param vector
     * @return A {@link String} in form of "(x coord),(y coord),(z coord)".
     */
    public static String stringifyVec3(Vec3 vector) {
        return vector.xCoord+","+vector.yCoord+","+vector.zCoord;
    }
    /**
     * @param vector
     * @param precision
     * @return A {@link String} in form of "(x coord),(y coord),(z coord)", where each of the coordinates are fixed to the precision.
     */
    public static String stringifyVec3(Vec3 vector,int precision) {
        return String.format("%."+precision+"f",vector.xCoord)+","+
                String.format("%."+precision+"f",vector.yCoord)+","+
                String.format("%."+precision+"f",vector.zCoord);
    }
    /**
     * Whether or not the {@link String} can be parsed as a vector.
     * @param str
     * @return
     */
    public static boolean isStringParsableAsVec3(String str) {
        String[] args=str.split(",");
        if (args.length!=3) return false;
        try {
            Double.parseDouble(args[0]);
            Double.parseDouble(args[1]);
            Double.parseDouble(args[2]);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    /**
     * Creates a vector from string.
     * @param str
     * @return
     */
    public static Vec3 stringToVec3(String str) {
        String[] args=str.split(",");
        return new Vec3(Double.parseDouble(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]));
    }
    public static String stringifyKeys(Input input) {
        return (input.isKeyForward()?"W":".")+
                (input.isKeyLeft()?"A":".")+
                (input.isKeyBackward()?"S":".")+
                (input.isKeyRight()?"D":".")+
                (input.isKeyJump()?"_":".")+
                (input.isKeySneak()?"+":".")+
                (input.isKeySprint()?"^":".");
    }
    public static String stringifyMouse(Input input) {
        return input.getRotationYaw()+","+input.getRotationPitch();
    }
    public static String stringifyMouse(Input input,int precision) {
        return String.format("%."+precision+"f",input.getRotationYaw())+","+String.format("%."+precision+"f",input.getRotationPitch());
    }
    public static String stringifyMouse(EntityMovementInfo playerState) {
        return playerState.getRotationYaw()+","+playerState.getRotationPitch();
    }
    public static String stringifyMouse(EntityMovementInfo playerState,int precision) {
        return String.format("%."+precision+"f",playerState.getRotationYaw())+","+String.format("%."+precision+"f",playerState.getRotationPitch());
    }
    public static String stringifyMouseButtonInputs(List<MouseButtonInputEnum> list) {
        StringBuffer buffer=new StringBuffer(list.size());
        for (MouseButtonInputEnum v:list) buffer.append(v.getCode());
        return buffer.toString();
    }
    public static String stringifyMouseButtonInputs(Input input) {
        return stringifyMouseButtonInputs(input.getMouseButtonInputs());
    }
    public static ArrayList<MouseButtonInputEnum> parseMouseButtonInputs(String str) {
        ArrayList<MouseButtonInputEnum> list=new ArrayList<MouseButtonInputEnum>(str.length());
        for (char c:str.toCharArray()) {
            MouseButtonInputEnum v=MouseButtonInputEnum.get(c);
            if (v==null) throw new IllegalArgumentException("Unknown mouse button input by "+c);
            list.add(v);
        }
        return list;
    }
    public static String stringifyPotionEffects(Collection<PotionEffect> potionEffects) {
        if (potionEffects==null) return "null";
        String r="";
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            if (!r.isEmpty()) r+=",";
            PotionEffect potionEffect=iterator.next();
            r+=potionEffect.getPotionID()+"*"+potionEffect.getAmplifier()+":"+potionEffect.getDuration();
        }
        return r;
    }
    public static String stringifyGameType(GameType gametype) {
        if (gametype==null||gametype.equals(GameType.NOT_SET)) return "NOT_SET";
        else if (gametype.equals(GameType.SURVIVAL)) return "SURVIVAL";
        else if (gametype.equals(GameType.CREATIVE)) return "CREATIVE";
        else if (gametype.equals(GameType.ADVENTURE)) return "ADVENTURE";
        else if (gametype.equals(GameType.SPECTATOR)) return "SPECTATOR";
        else throw new IllegalArgumentException("Unsupported game mode");
    }
    public static GameType toGameType(String value) {
        if (value==null||value.equals("")||value.equals("-1")||value.equalsIgnoreCase("NOT_SET")) return GameType.NOT_SET;
        else if (value.equals("0")||value.equalsIgnoreCase("SURVIVAL")) return GameType.SURVIVAL;
        else if (value.equals("1")||value.equalsIgnoreCase("CREATIVE")) return GameType.CREATIVE;
        else if (value.equals("2")||value.equalsIgnoreCase("ADVENTURE")) return GameType.ADVENTURE;
        else if (value.equals("3")||value.equalsIgnoreCase("SPECTATOR")) return GameType.SPECTATOR;
        else throw new IllegalArgumentException("Unsupported game mode");
    }
    /**
     * Calculates how much the camera rotation is multiplied according to {@link new.minecraft.client.renderer.EntityRenderer#updateCameraAndRender()}.
     * @param mouseSensitivity
     * @return Rotation in degrees per pixel
     */
    public static float getRotationMult(float mouseSensitivity) {
        float f = mouseSensitivity * 0.6F + 0.2F;
        float mult = f * f * f * 8.0F;
        return mult;
    }
    /**
     * Calculates how much the camera is rotated by pixel of mouse movement according to {@link new.minecraft.client.renderer.EntityRenderer#updateCameraAndRender()}.
     * @param mouseSensitivity
     * @return Rotation in degrees per pixel
     */
    public static double getRotationPerPixel(float mouseSensitivity) {
        return getRotationMult(mouseSensitivity)*0.15D;
    }
    public static Vec3 toVec3(BlockPos blockpos) {
        return new Vec3(blockpos.getX(),blockpos.getY(),blockpos.getZ());
    }
    public static Vec3 clone(Vec3 vector) {
        return new Vec3(vector.xCoord,vector.yCoord,vector.zCoord);
    }
    public static PotionEffect clone(PotionEffect potionEffect) {
        return new PotionEffect(potionEffect);
    }
    public static Collection<PotionEffect> clone(Collection<PotionEffect> potionEffects) {
        Map<Integer,PotionEffect> map=new HashMap<Integer,PotionEffect>();
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect=iterator.next();
            map.put(potionEffect.getPotionID(),clone(potionEffect));
        }
        return map.values();
    }
    /**
     * Replaces the potion effect.
     */
    public static void apply(EntityLivingBase entity,Collection<PotionEffect> potionEffects) {
        removePotionEffects(entity);
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            entity.addPotionEffect(clone(iterator.next()));
        }
    }
    /**
     * Removes all effects.
     */
    public static void removePotionEffects(EntityLivingBase entity) {
        Iterator<PotionEffect> iterator=entity.getActivePotionEffects().iterator();
        ArrayList<PotionEffect> effectList=new ArrayList<PotionEffect>();
        while (iterator.hasNext()) {
            effectList.add(iterator.next());
        }
        for (int i=0;i<effectList.size();i++) {
            PotionEffect potionEffect=effectList.get(i);
            entity.removePotionEffect(potionEffect.getPotionID());
        }
    }
    /**
     * Clone and increase or decrease the duration.
     */
    public static PotionEffect changeDuration(PotionEffect potionEffect,int value) {
        return new PotionEffect(potionEffect.getPotionID(),
                potionEffect.getDuration()+value,
                potionEffect.getAmplifier(),
                potionEffect.getIsAmbient(),
                potionEffect.getIsShowParticles());
    }
    /**
     * Clone and increase or decrease the duration of each effect.
     */
    public static Collection<PotionEffect> changeDurations(Collection<PotionEffect> potionEffects,int value) {
        Map<Integer,PotionEffect> map=new HashMap<Integer,PotionEffect>();
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect=iterator.next();
            map.put(potionEffect.getPotionID(),changeDuration(potionEffect,value));
        }
        return map.values();
    }
    public static EntityPlayerMP getPlayerMP(Minecraft mc) {
        return mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(mc.thePlayer.getUniqueID());
    }
    public static InputSideMenu inputSideMenuFromStartMotions(Vec3 startPosition,Vec3 startMotion,int startInvulnerabilityFrames) {
        InputSideMenu inputSideMenu=new InputSideMenu();
        inputSideMenu.setStartPosition(startPosition);
        inputSideMenu.setStartMotion(startMotion);
        inputSideMenu.setStartInvulnerabilityFrames(startInvulnerabilityFrames);
        return inputSideMenu;
    }
    public static InputSideMenu inputSideMenuFromPlayer(EntityPlayer player,Minecraft mc) throws IllegalArgumentException, IllegalAccessException {
        return inputSideMenuFromStartMotions(
                getPositionVector(player),
                getMotionVector(player),
                getRespawnInvulnerabilityTicks(getPlayerMP(mc)));
    }
    

    private static Field EntityLivingBase$newPosX=ReflectionHelper.findField(EntityLivingBase.class,"newPosX","field_70709_bj");
    public static double getNewPosX(EntityLivingBase entity) throws IllegalArgumentException, IllegalAccessException {
        return EntityLivingBase$newPosX.getDouble(entity);
    }
    private static Field EntityLivingBase$newPosY=ReflectionHelper.findField(EntityLivingBase.class,"newPosY","field_70710_bk");
    public static double getNewPosY(EntityLivingBase entity) throws IllegalArgumentException, IllegalAccessException {
        return EntityLivingBase$newPosY.getDouble(entity);
    }
    private static Field EntityLivingBase$newPosZ=ReflectionHelper.findField(EntityLivingBase.class,"newPosZ","field_110152_bk");
    public static double getNewPosZ(EntityLivingBase entity) throws IllegalArgumentException, IllegalAccessException {
        return EntityLivingBase$newPosZ.getDouble(entity);
    }
    private static Field EntityLivingBase$newPosRotationIncrements=ReflectionHelper.findField(EntityLivingBase.class,"newPosRotationIncrements","field_70716_bi");
    public static int getNewPosRotationIncrements(EntityLivingBase entity) throws IllegalArgumentException, IllegalAccessException {
        return EntityLivingBase$newPosRotationIncrements.getInt(entity);
    }
    private static Field EntityLivingBase$jumpTicksField=ReflectionHelper.findField(EntityLivingBase.class,"jumpTicks","field_70773_bE");
    public static int getJumpTicks(EntityLivingBase entity) throws IllegalArgumentException, IllegalAccessException {
        return EntityLivingBase$jumpTicksField.getInt(entity);
    }
    private static Field EntityPlayerSP$sprintingTicksLeft=ReflectionHelper.findField(EntityPlayerSP.class,"sprintingTicksLeft","field_71157_e");
    public static int getSprintingTicksLeft(EntityPlayerSP player) throws IllegalArgumentException, IllegalAccessException {
        return EntityPlayerSP$sprintingTicksLeft.getInt(player);
    }
    private static Field EntityPlayerSP$sprintToggleTimer=ReflectionHelper.findField(EntityPlayerSP.class,"sprintToggleTimer","field_71156_d");
    public static int getSprintToggleTimer(EntityPlayerSP player) throws IllegalArgumentException, IllegalAccessException {
        return EntityPlayerSP$sprintToggleTimer.getInt(player);
    }
    private static Field EntityPlayerMP$respawnInvulnerabilityTicks=ReflectionHelper.findField(EntityPlayerMP.class,"respawnInvulnerabilityTicks","field_147101_bU");
    public static int getRespawnInvulnerabilityTicks(EntityPlayerMP player) throws IllegalArgumentException, IllegalAccessException {
        return EntityPlayerMP$respawnInvulnerabilityTicks.getInt(player);
    }
    public static void setRespawnInvulnerabilityTicks(EntityPlayerMP player,int respawnInvulnerabilityTicks) throws IllegalArgumentException, IllegalAccessException {
        EntityPlayerMP$respawnInvulnerabilityTicks.setInt(player,respawnInvulnerabilityTicks);
    }
    private static Field Mouse$readBuffer=ReflectionHelper.findField(Mouse.class,"readBuffer");
    public static ByteBuffer getMouseByteBuffer() throws IllegalArgumentException, IllegalAccessException {
        return (ByteBuffer)Mouse$readBuffer.get(null);
    }
}
