package walksy.popchams.handler;

import walksy.popchams.capture.CapturedPlayer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class PositionHandler {

    private static final Set<CapturedPlayer> positions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void handleTotem(final Player player) {
        positions.add(new CapturedPlayer(Minecraft.getInstance().level, player));
    }

    public static Set<CapturedPlayer> getPositions() {
        return positions;
    }
}
