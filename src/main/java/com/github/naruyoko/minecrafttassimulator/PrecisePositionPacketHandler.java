package com.github.naruyoko.minecrafttassimulator;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PrecisePositionPacketHandler implements IMessageHandler<PrecisePositionPacket, IMessage> {
    @Override
    public IMessage onMessage(PrecisePositionPacket message, MessageContext ctx) {
        if (ctx.side==Side.CLIENT) PrecisePosition.add(message);
        return null;
    }
}
