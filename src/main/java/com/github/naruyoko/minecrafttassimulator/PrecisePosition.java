package com.github.naruyoko.minecrafttassimulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrecisePosition {
    private static Map<Integer,PrecisePositionPacket> recieved=new HashMap<Integer,PrecisePositionPacket>();
    public static boolean has(int id) {
        return recieved.containsKey(id);
    }
    public static PrecisePositionPacket get(int id) {
        return recieved.get(id);
    }
    public static void retainAll(Collection<Integer> ids) {
        recieved.keySet().retainAll(ids);
    }
    public static void add(PrecisePositionPacket packet) {
        recieved.put(packet.getId(),packet);
    }
}
