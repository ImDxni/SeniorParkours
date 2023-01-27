package it.dani.seniorparkour.nms.v1_16_R3;


import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R3.PacketDataSerializer;

import java.util.UUID;

//thx filoghost
class PacketByteBuffer extends PacketDataSerializer {

    private static final PacketByteBuffer INSTANCE = new PacketByteBuffer();

    static PacketByteBuffer get() {
        INSTANCE.clear();
        return INSTANCE;
    }

    private PacketByteBuffer() {
        super(Unpooled.buffer());
    }

    void writeVarInt(int i) {
        super.d(i);
    }

    void writeVarIntArray(int i1) {
        writeVarInt(1);
        writeVarInt(i1);
    }

    void writeUUID(UUID uuid) {
        super.a(uuid);
    }

    <T> void writeDataWatcherEntry(DataWatcherKey<T> key, T value) {
        writeByte(key.getIndex());
        writeVarInt(key.getSerializerTypeID());
        key.getSerializer().a(this, value);
    }

    void writeDataWatcherEntriesEnd() {
        writeByte(0xFF);
    }

}
