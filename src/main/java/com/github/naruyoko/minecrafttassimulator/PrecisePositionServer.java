package com.github.naruyoko.minecrafttassimulator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class PrecisePositionServer {
    public static void sendPrecisePositionOfRidingEntities(WorldTickEvent event) {
        if (event.phase!=TickEvent.Phase.END||event.side!=Side.SERVER) return;
        EntityPlayerMP player=SimulatorUtil.getPlayerMP(Minecraft.getMinecraft());
        if (player==null) return;
        Entity entity=player;
        while (entity!=null&&entity.isRiding()) {
            Entity ridingEntity=entity.ridingEntity;
            MinecraftTASSimulatorMod.NETWORK.sendTo(new PrecisePositionPacket(ridingEntity), player);
            entity=ridingEntity;
        }
    }
}
