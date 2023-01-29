package it.dani.seniorparkour.nms.v1_19_R2;


import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;

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

    <T> void writeDataWatcherEntry(DataWatcherKey<T> key, T value) {
        writeByte(key.getIndex());
        writeVarInt(key.getSerializerTypeID());
        key.getSerializer().a(this, value);
    }

    void writeDataWatcherEntriesEnd() {
        writeByte(0xFF);
    }

}
