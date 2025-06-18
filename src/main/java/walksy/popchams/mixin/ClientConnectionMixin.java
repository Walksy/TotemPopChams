package walksy.popchams.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.popchams.config.Config;
import walksy.popchams.handler.PositionHandler;

import static walksy.popchams.TotemPopChams.mc;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void onReceive(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci)
    {
        if (mc.world == null) return;
        if (packet instanceof EntityStatusS2CPacket p)
        {
            Entity entity = p.getEntity(mc.world);
            if (entity instanceof AbstractClientPlayerEntity player) {

                if (!Config.CONFIG.instance().showOwnPops)
                {
                    if (player == mc.player) return;
                }

                if (p.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                    PositionHandler.onTotemPop(player);
                }
            }
        }
    }
}
