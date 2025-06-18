package walksy.popchams.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.popchams.render.ChamRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0),
        method = "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V")
    private void onRenderWorldHandRendering(RenderTickCounter tickCounter,
                                            CallbackInfo ci, @Local(ordinal = 2) Matrix4f matrix4f3,
                                            @Local(ordinal = 1) float tickDelta)
    {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiplyPositionMatrix(matrix4f3);
        ChamRenderer.render(matrixStack, tickDelta);
    }
}
