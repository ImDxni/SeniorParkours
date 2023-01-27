package it.dani.seniorparkour.nms.v1_16_R3;


import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.DataWatcherSerializer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.ItemStack;

import java.util.Optional;

class DataWatcherKey<T> {

    private static final DataWatcherSerializer<Optional<IChatBaseComponent>> OPTIONAL_CHAT_COMPONENT_SERIALIZER = DataWatcherRegistry.f;
    static final DataWatcherKey<Optional<IChatBaseComponent>> CUSTOM_NAME = new DataWatcherKey<>(2, OPTIONAL_CHAT_COMPONENT_SERIALIZER);


    private final int index;
    private final DataWatcherSerializer<T> serializer;
    private final int serializerTypeID;

    private DataWatcherKey(int index, DataWatcherSerializer<T> serializer) {
        this.index = index;
        this.serializer = serializer;
        this.serializerTypeID = DataWatcherRegistry.b(serializer);
        if (serializerTypeID < 0) {
            throw new EncoderException("Could not find serializer ID of " + serializer);
        }
    }

    int getIndex() {
        return index;
    }

    DataWatcherSerializer<T> getSerializer() {
        return serializer;
    }

    int getSerializerTypeID() {
        return serializerTypeID;
    }

}