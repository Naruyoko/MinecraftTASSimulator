package com.github.naruyoko.minecrafttaseditor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MinecraftTASEditorUtil {
    /**
     * @param player The player to get the vector from.
     * @return A {@link Vec3} representing the position vector of the player.
     */
    public static Vec3 getPositionVector(EntityPlayer player) {
        return player.func_174791_d();
    }
    /**
     * @param player The player to get the vector from.
     * @return A {@link Vec3} representing the motion vector of the player.
     */
    public static Vec3 getMotionVector(EntityPlayer player) {
        return new Vec3(player.field_70159_w,player.field_70181_x,player.field_70179_y);
    }
    /**
     * @param vector
     * @return A {@link String} in form of "(x coord),(y coord),(z coord)".
     */
    public static String stringifyVector(Vec3 vector) {
        return vector.field_72450_a+","+vector.field_72448_b+","+vector.field_72449_c;
    }
    /**
     * @param vector
     * @param precision
     * @return A {@link String} in form of "(x coord),(y coord),(z coord)", where each of the coordinates are fixed to the precision.
     */
    public static String stringifyVector(Vec3 vector,int precision) {
        return String.format("%."+precision+"f",vector.field_72450_a)+","+
                String.format("%."+precision+"f",vector.field_72448_b)+","+
                String.format("%."+precision+"f",vector.field_72449_c);
    }
    /**
     * Whether or not the {@link String} can be parsed as a vector.
     * @param str
     * @return
     */
    public static boolean isStringParsableAsVector(String str) {
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
    public static Vec3 stringToVector(String str) {
        String[] args=str.split(",");
        return new Vec3(Double.parseDouble(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]));
    }
    public static String stringifyKeys(MinecraftTASEditorInput input) {
        return (input.isKeyForward()?"W":".")+
                (input.isKeyLeft()?"A":".")+
                (input.isKeyBackward()?"S":".")+
                (input.isKeyRight()?"D":".")+
                (input.isKeyJump()?"_":".")+
                (input.isKeySneak()?"+":".")+
                (input.isKeySprint()?"^":".");
    }
    public static String stringifyMouse(MinecraftTASEditorInput input) {
        return input.getRotationYaw()+","+input.getRotationPitch();
    }
    public static String stringifyMouse(MinecraftTASEditorInput input,int precision) {
        return String.format("%."+precision+"f",input.getRotationYaw())+","+String.format("%."+precision+"f",input.getRotationPitch());
    }
    public static String stringifyPotionEffects(Collection<PotionEffect> potionEffects) {
        String r="";
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            if (!r.isEmpty()) r+=",";
            PotionEffect potionEffect=iterator.next();
            r+=potionEffect.func_76456_a()+"*"+potionEffect.func_76458_c()+":"+potionEffect.func_76459_b();
        }
        return r;
    }
    public static Vec3 toVector(BlockPos blockpos) {
        return new Vec3(blockpos.func_177958_n(),blockpos.func_177956_o(),blockpos.func_177952_p());
    }
    public static Vec3 clone(Vec3 vector) {
        return new Vec3(vector.field_72450_a,vector.field_72448_b,vector.field_72449_c);
    }
    public static PotionEffect clone(PotionEffect potionEffect) {
        return new PotionEffect(potionEffect);
    }
    public static Collection<PotionEffect> clone(Collection<PotionEffect> potionEffects) {
        Map<Integer,PotionEffect> map=new HashMap<Integer,PotionEffect>();
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect=iterator.next();
            map.put(potionEffect.func_76456_a(),clone(potionEffect));
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
            entity.func_70690_d(clone(iterator.next()));
        }
    }
    /**
     * Removes all effects.
     */
    public static void removePotionEffects(EntityLivingBase entity) {
        Iterator<PotionEffect> iterator=entity.func_70651_bq().iterator();
        ArrayList<PotionEffect> effectList=new ArrayList<PotionEffect>();
        while (iterator.hasNext()) {
            effectList.add(iterator.next());
        }
        for (int i=0;i<effectList.size();i++) {
            PotionEffect potionEffect=effectList.get(i);
            entity.func_82170_o(potionEffect.func_76456_a());
        }
    }
    /**
     * Clone and increase or decrease the duration.
     */
    public static PotionEffect changeDuration(PotionEffect potionEffect,int value) {
        return new PotionEffect(potionEffect.func_76456_a(),
                potionEffect.func_76459_b()+value,
                potionEffect.func_76458_c(),
                potionEffect.func_82720_e(),
                potionEffect.func_180154_f());
    }
    /**
     * Clone and increase or decrease the duration of each effect.
     */
    public static Collection<PotionEffect> changeDurations(Collection<PotionEffect> potionEffects,int value) {
        Map<Integer,PotionEffect> map=new HashMap<Integer,PotionEffect>();
        Iterator<PotionEffect> iterator=potionEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect=iterator.next();
            map.put(potionEffect.func_76456_a(),changeDuration(potionEffect,value));
        }
        return map.values();
    }
    public static EntityPlayerMP getPlayerMP(Minecraft mc) {
        return mc.func_71401_C().func_71203_ab().func_177451_a(mc.field_71439_g.func_110124_au());
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
}
