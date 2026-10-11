package walksy.popchams.render;

import main.walksy.lib.api.render.WLGraphics3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import walksy.popchams.Layers;
import walksy.popchams.capture.CapturedPlayer;
import walksy.popchams.handler.PositionHandler;
import walksy.popchams.render.impl.WireframePartExtractor;

public class ChamExtractor {
    private static final int LIGHT = 15728880;
    private final WireframePartExtractor wireframeExtractor;
    private final ChamConfigState state;

    public ChamExtractor() {
        this.wireframeExtractor = new WireframePartExtractor();
        this.state = new ChamConfigState();
    }

    public void extract(final PoseStack matrices, final float delta, final WLGraphics3D sink) {
        for (final CapturedPlayer player : PositionHandler.getPositions()) {
            this.state.updateConfigRenderState(player, delta);
            if (this.state.renderFillModel) {
                this.renderPlayerModel(matrices, player, sink);
            }
            if (this.state.renderWireframe) {
                this.renderWireframe(matrices, player, sink);
            }
        }
        sink.finish();
    }

    private void renderPlayerModel(final PoseStack matrices, final CapturedPlayer player, final WLGraphics3D sink) {
        this.setupAndPush(matrices, player);
        final PlayerModel model = player.model;
        player.hideOuterLayers();
        this.applyFlyingTransforms(player, matrices);
        final int color = this.alphaColor(this.state.filledColor, this.state.alpha);
        if (!this.state.disperse) {
            sink.submit(matrices, Layers.TRANSLUCENT_ENTITY_HIGHLIGHT, (stack, buffer) -> model.renderToBuffer(stack, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color));
        } else {
            for (final BodyPart part : BodyPart.values()) {
                final Vec3 pos = this.getPartDisplacement(player, part);
                matrices.pushPose();
                matrices.translate(pos.x(), pos.y(), pos.z());
                sink.submit(matrices, Layers.TRANSLUCENT_ENTITY_HIGHLIGHT, (stack, buffer) -> this.renderModelPart(model, part, stack, buffer, color));
                matrices.popPose();
            }
        }
        matrices.popPose();
    }

    private void renderWireframe(final PoseStack matrices, final CapturedPlayer player, final WLGraphics3D sink) {
        this.setupAndPush(matrices, player);
        final PlayerModel model = player.model;
        player.hideOuterLayers();
        this.applyFlyingTransforms(player, matrices);
        final int color = this.alphaColor(this.state.wireframeColor, this.state.alpha);
        final double thickness = this.state.wireframeThickness;
        for (final BodyPart part : BodyPart.values()) {
            final Vec3 disp = this.getPartDisplacement(player, part);
            matrices.pushPose();
            matrices.translate(disp.x(), disp.y(), disp.z());
            sink.submit(matrices, Layers.WIREFRAME, (stack, buffer) -> this.renderWireframePart(model, part, stack, buffer, color, thickness));
            matrices.popPose();
        }
        matrices.popPose();
    }

    private int alphaColor(final int baseColor, final float alphaMultiplier) {
        final int newAlpha = Mth.clamp((int) (((baseColor >> 24) & 0xFF) * alphaMultiplier), 0, 255);
        return (newAlpha << 24) | (baseColor & 0x00FFFFFF);
    }

    private void renderModelPart(final PlayerModel model, final BodyPart part, final PoseStack matrices, final VertexConsumer buffer, final int color) {
        switch (part) {
            case HEAD -> model.head.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
            case BODY -> model.body.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
            case LEFT_ARM -> model.leftArm.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
            case RIGHT_ARM -> model.rightArm.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
            case LEFT_LEG -> model.leftLeg.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
            case RIGHT_LEG -> model.rightLeg.render(matrices, buffer, LIGHT, OverlayTexture.NO_OVERLAY, color);
        }
    }

    private void renderWireframePart(final PlayerModel model, final BodyPart part, final PoseStack matrices, final VertexConsumer buffer, final int color, final double thickness) {
        switch (part) {
            case HEAD -> this.wireframeExtractor.extractModelPart(model.head, matrices, buffer, color, thickness);
            case BODY -> this.wireframeExtractor.extractModelPart(model.body, matrices, buffer, color, thickness);
            case LEFT_ARM -> this.wireframeExtractor.extractModelPart(model.leftArm, matrices, buffer, color, thickness);
            case RIGHT_ARM -> this.wireframeExtractor.extractModelPart(model.rightArm, matrices, buffer, color, thickness);
            case LEFT_LEG -> this.wireframeExtractor.extractModelPart(model.leftLeg, matrices, buffer, color, thickness);
            case RIGHT_LEG -> this.wireframeExtractor.extractModelPart(model.rightLeg, matrices, buffer, color, thickness);
        }
    }

    private Vec3 getPartDisplacement(final CapturedPlayer player, final BodyPart part) {
        if (!this.state.disperse) {
            return Vec3.ZERO;
        }
        final long seed = player.getUUID()
            .getMostSignificantBits() ^ player.getUUID()
            .getLeastSignificantBits() ^ part.ordinal();
        final RandomSource rand = RandomSource.create(seed);
        final Vec3 base = switch (part) {
            case HEAD -> new Vec3(0, -1, 0);
            case BODY -> Vec3.ZERO;
            case LEFT_ARM -> new Vec3(1, 0, 0);
            case RIGHT_ARM -> new Vec3(-1, 0, 0);
            case LEFT_LEG -> new Vec3(0.3, 1, 0);
            case RIGHT_LEG -> new Vec3(-0.3, 1, 0);
        };
        final Vec3 noise = new Vec3((rand.nextDouble() - 0.5) * 0.5, (rand.nextDouble() - 0.5) * 0.5, (rand.nextDouble() - 0.5) * 0.5);
        return base.add(noise).normalize().scale(this.state.disperseMaxDistance);
    }

    private void setupAndPush(final PoseStack stack, final CapturedPlayer player) {
        stack.pushPose();
        final Vec3 camera = Minecraft.getInstance().getEntityRenderDispatcher().camera.position();
        stack.translate(player.getX() - camera.x, player.getY() - camera.y, player.getZ() - camera.z);
        stack.mulPose(Axis.YP.rotationDegrees(180 - player.yBodyRot));
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.scale(1.6f / (float) 2, 1.8f / (float) 2, 1.6f / (float) 2);
        stack.translate(0.0F, -1.5F, 0.0F);
    }

    private void applyFlyingTransforms(final CapturedPlayer player, final PoseStack stack) {
        if (!player.autoSpinAttack) {
            stack.mulPose(Axis.XP.rotationDegrees(player.glidingProcess * (90.0F - player.statePitch)));
        }
        if (player.applyFlyingYRot) {
            stack.mulPose(Axis.YP.rotation(player.flyingRotation));
        }
    }

    private enum BodyPart {
        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
    }
}