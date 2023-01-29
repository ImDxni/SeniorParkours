package it.dani.seniorparkour.nms.v1_19_R2;

import it.dani.seniorparkour.commons.HologramAdapter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Function;

public class HologramAdapterImpl extends HologramAdapter {

    @Override
    public void sendHologram(Function<Player,String> function) {
        for (Player player : Bukkit.getOnlinePlayers()) {

            String message = function.apply(player);
            PacketByteBuffer buffer = PacketByteBuffer.get();
            buffer.writeVarInt(getEntityID());
            buffer.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME, Optional.of(CraftChatMessage.fromString(message, false, true)[0]));
            buffer.writeDataWatcherEntriesEnd();

            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(buffer);
            sendPacket(player,packet);
        }
    }

    private void sendPacket(Player player,PacketPlayOutEntityMetadata packet){
        ((CraftPlayer)player).getHandle().b.a(packet);
    }
}
