package walksy.popchams.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.popchams.capture.CapturedPlayer;
import walksy.popchams.handler.PositionHandler;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        PositionHandler.getPositions().forEach(CapturedPlayer::tickAge);
    }
}
