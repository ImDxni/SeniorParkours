package it.dani.seniorparkour.nms.v1_16_R3;

import it.dani.seniorparkour.commons.HologramAdapter;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class HologramAdapterImpl extends HologramAdapter {

    @Override
    public void sendHologram(Function<Player,String> function) {
        for (Player player : Bukkit.getOnlinePlayers()) {

            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();

            PacketByteBuffer buffer = PacketByteBuffer.get();
            buffer.writeVarInt(getEntityID());
            buffer.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME, Optional.of(new ChatComponentText(function.apply(player))));
            buffer.writeDataWatcherEntriesEnd();

            try {
                packet.a(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sendPacket(player,packet);
        }
    }

    private void sendPacket(Player player,PacketPlayOutEntityMetadata packet){
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
