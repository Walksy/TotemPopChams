package walksy.popchams.handler;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import walksy.popchams.capture.CapturedPlayer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PositionHandler {

    private static final Set<CapturedPlayer> positions
        = Collections.newSetFromMap(new ConcurrentHashMap<>());


    public static void onTotemPop(AbstractClientPlayerEntity player)
    {
        positions.add(new CapturedPlayer(player));
    }


    public static Set<CapturedPlayer> getPositions()
    {
        return positions;
    }
}
