package walksy.popchams;

import main.walksy.lib.api.event.WalksyLibEvents;
import net.fabricmc.api.ModInitializer;
import walksy.popchams.config.TotemPopChamsConfig;
import walksy.popchams.handler.PositionHandler;
import walksy.popchams.render.ChamExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;

public class TotemPopChams implements ModInitializer {

    private static ChamExtractor extractor;

    @Override
    public void onInitialize() {
        extractor = new ChamExtractor();
        WalksyLibEvents.START_LOCAL_PLAYER_TICK.register(player -> {
            if (TotemPopChamsConfig.modEnabled) {
                PositionHandler.getPositions().forEach(position -> position.tickAge(TotemPopChamsConfig.lifeTime));
            }
        });
        WalksyLibEvents.START_RECEIVE_PACKET_MAIN.register((packet, listener, cancellable) -> {
            if (packet instanceof final ClientboundEntityEventPacket event) {
                onEntityEvent(event);
            }
        });
        WalksyLibEvents.RENDER_3D.register(graphics -> extractor.extract(graphics.pose(), graphics.partialTick(), graphics));
    }

    private static void onEntityEvent(final ClientboundEntityEventPacket packet) {
        final Minecraft client = Minecraft.getInstance();
        if (!TotemPopChamsConfig.modEnabled || client.level == null || packet.getEventId() != EntityEvent.PROTECTED_FROM_DEATH) {
            return;
        }
        if (packet.getEntity(client.level) instanceof final Player player && (TotemPopChamsConfig.showOwnPops || player != client.player)) {
            PositionHandler.handleTotem(player);
        }
    }
}
