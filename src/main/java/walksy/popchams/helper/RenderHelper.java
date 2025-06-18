package walksy.popchams.helper;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import walksy.popchams.capture.CapturedPlayer;

import static walksy.popchams.TotemPopChams.mc;

public class RenderHelper {

    private static Vec3d getOffset(CapturedPlayer player)
    {
        Vec3d camera = mc.getEntityRenderDispatcher().camera.getPos();
        double x = player.getX() - camera.x;
        double y = player.getY() - camera.y;
        double z = player.getZ() - camera.z;
        return new Vec3d(x, y, z);
    }

    public static void setupAndPush(MatrixStack stack, CapturedPlayer player, float correction)
    {
        stack.push();
        Vec3d translatableOffset = getOffset(player);
        stack.translate(translatableOffset.x, translatableOffset.y, translatableOffset.z);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - player.bodyYaw));

        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.scale(1.6f / correction, 1.8f / correction, 1.6f / correction);
        stack.translate(0.0F, -1.5F, 0.0F);
    }

    public static void applyFlyingTransforms(CapturedPlayer player, MatrixStack stack)
    {
        float i = player.statePitch;
        float j = player.glidingProcess;
        if (!player.state.usingRiptide) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (90.0F - i)));
        }

        if (player.state.applyFlyingRotation) {
            stack.multiply(RotationAxis.POSITIVE_Y.rotation(player.flyingRotation));
        }
    }
}
