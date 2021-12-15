package com.github.naruyoko.minecrafttassimulator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PrecisePositionPacket implements IMessage {
    private int id;
    private double posX;
    private double posY;
    private double posZ;
    private float rotationYaw;
    private float rotationPitch;
    public PrecisePositionPacket(int id, double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
        super();
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
    public PrecisePositionPacket(Entity entity) {
        this(entity.getEntityId(),entity.posX,entity.posY,entity.posZ,entity.rotationYaw,entity.rotationPitch);
    }
    public PrecisePositionPacket() {
        this(0,0D,0D,0D,0F,0F);
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        id=buf.readInt();
        posX=buf.readDouble();
        posY=buf.readDouble();
        posZ=buf.readDouble();
        rotationYaw=buf.readFloat();
        rotationPitch=buf.readFloat();
    }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeFloat(rotationYaw);
        buf.writeFloat(rotationPitch);
    }
    public int getId() {
        return id;
    }
    public double getPosX() {
        return posX;
    }
    public double getPosY() {
        return posY;
    }
    public double getPosZ() {
        return posZ;
    }
    public float getRotationYaw() {
        return rotationYaw;
    }
    public float getRotationPitch() {
        return rotationPitch;
    }
}
