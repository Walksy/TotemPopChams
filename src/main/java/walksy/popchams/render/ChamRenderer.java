package walksy.popchams.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import walksy.popchams.Layers;
import walksy.popchams.capture.CapturedPlayer;
import walksy.popchams.config.Config;
import walksy.popchams.handler.PositionHandler;
import walksy.popchams.helper.RenderHelper;
import walksy.popchams.helper.WireframeHelper;

import static walksy.popchams.TotemPopChams.mc;

public class ChamRenderer {

    private static final int LIGHT = 15728880;

    private static float displacementAmount;

    private enum BodyPart {
        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;

        public static BodyPart fromString(String s) {
            return switch (s) {
                case "head" -> HEAD;
                case "body" -> BODY;
                case "leftArm" -> LEFT_ARM;
                case "rightArm" -> RIGHT_ARM;
                case "leftLeg" -> LEFT_LEG;
                case "rightLeg" -> RIGHT_LEG;
                default -> BODY;
            };
        }
    }

    public static void render(MatrixStack matrices, float delta) {
        var config = Config.CONFIG.instance();
        if (!config.modEnabled) return;

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

        for (CapturedPlayer player : PositionHandler.getPositions()) {
            float smoothAge = Math.max(0, player.age - 1 + delta);
            float speedScale = (float) MathHelper.clamp(config.disperseSpeed, 1f, 10f);
            float speedMultiplier = (float) Math.pow(speedScale / 10f, 2) * 2f;
            displacementAmount = config.disperse
                ? (float) Math.min(smoothAge * speedMultiplier, config.disperseMaxDistance)
                : 0f;

            if (config.filledModelEnabled) {
                renderPlayerModel(matrices, player, delta, immediate, config);
            }

            if (config.wireframeEnabled) {
                renderWireframe(matrices, player, delta, immediate, config);
            }
        }

        immediate.draw();
    }

    private static void renderPlayerModel(MatrixStack matrices, CapturedPlayer player, float delta,
                                          VertexConsumerProvider.Immediate immediate, Config config) {
        RenderHelper.setupAndPush(matrices, player, 2);

        VertexConsumer buffer = immediate.getBuffer(Layers.TRANSLUCENT_ENTITY_HIGHLIGHT);
        PlayerEntityModel model = player.model;
        setupRenderState(player.state, model);
        RenderHelper.applyFlyingTransforms(player, matrices);

        if (!config.disperse) {
            //No displacement, one draw call
            renderWholeModel(model, matrices, buffer, config.filledColor.getRGB(), computeAlpha(player, delta, config));
        } else {
            for (BodyPart part : BodyPart.values()) {
                Vec3d disp = getPartDisplacement(player, part, displacementAmount);
                matrices.push();
                matrices.translate(disp.getX(), disp.getY(), disp.getZ());

                float alpha = computeAlpha(player, delta, config);
                if (alpha < 0.01f) {
                    matrices.pop();
                    continue;
                }

                int color = applyAlphaToColor(config.filledColor.getRGB(), alpha);
                renderModelPart(model, part, matrices, buffer, color);

                matrices.pop();
            }
        }

        matrices.pop();
    }

    private static void renderWireframe(MatrixStack matrices, CapturedPlayer player, float delta,
                                        VertexConsumerProvider.Immediate immediate, Config config) {
        RenderHelper.setupAndPush(matrices, player, 2);

        VertexConsumer buffer = immediate.getBuffer(Layers.getWireFrame(config.wireframeThickness));
        PlayerEntityModel model = player.model;
        setupRenderState(player.state, model);
        RenderHelper.applyFlyingTransforms(player, matrices);

        for (BodyPart part : BodyPart.values()) {
            Vec3d disp = getPartDisplacement(player, part, displacementAmount);
            matrices.push();
            matrices.translate(disp.getX(), disp.getY(), disp.getZ());

            float alpha = computeAlpha(player, delta, config);
            if (alpha < 0.01f) {
                matrices.pop();
                continue;
            }

            int color = applyAlphaToColor(config.wireframeColor.getRGB(), alpha);
            renderWireframePart(model, part, matrices, buffer, color);

            matrices.pop();
        }

        matrices.pop();
    }

    private static float computeAlpha(CapturedPlayer player, float delta, Config config) {
        if (!config.fadeOut) return 1f;

        float smoothAge = Math.max(0, player.age - 1 + delta);
        float fadeStart = (float) (config.lifeTime * 0.8f);
        float lifetimeFade = smoothAge >= fadeStart
            ? (float) (1f - ((smoothAge - fadeStart) / (config.lifeTime - fadeStart)))
            : 1f;

        float displacementFade = config.disperse
            ? (float) (1f - (displacementAmount / config.disperseMaxDistance))
            : 1f;

        return MathHelper.clamp(lifetimeFade * displacementFade, 0f, 1f);
    }

    private static int applyAlphaToColor(int baseColor, float alphaMultiplier) {
        int baseAlpha = (baseColor >> 24) & 0xFF;
        int newAlpha = MathHelper.clamp((int) (baseAlpha * alphaMultiplier), 0, 255);
        return (newAlpha << 24) | (baseColor & 0x00FFFFFF);
    }

    private static void renderModelPart(PlayerEntityModel model, BodyPart part,
                                        MatrixStack matrices, VertexConsumer buffer, int color) {
        switch (part) {
            case HEAD -> model.head.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
            case BODY -> model.body.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
            case LEFT_ARM -> model.leftArm.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
            case RIGHT_ARM -> model.rightArm.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
            case LEFT_LEG -> model.leftLeg.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
            case RIGHT_LEG -> model.rightLeg.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
        }
    }

    private static void renderWireframePart(PlayerEntityModel model, BodyPart part,
                                            MatrixStack matrices, VertexConsumer buffer, int color) {
        switch (part) {
            case HEAD -> WireframeHelper.renderModelPartWireframe(model.head, matrices, buffer, color, 0);
            case BODY -> WireframeHelper.renderModelPartWireframe(model.body, matrices, buffer, color, 0);
            case LEFT_ARM -> WireframeHelper.renderModelPartWireframe(model.leftArm, matrices, buffer, color, 0);
            case RIGHT_ARM -> WireframeHelper.renderModelPartWireframe(model.rightArm, matrices, buffer, color, 0);
            case LEFT_LEG -> WireframeHelper.renderModelPartWireframe(model.leftLeg, matrices, buffer, color, 0);
            case RIGHT_LEG -> WireframeHelper.renderModelPartWireframe(model.rightLeg, matrices, buffer, color, 0);
        }
    }

    private static void renderWholeModel(PlayerEntityModel model, MatrixStack matrices,
                                         VertexConsumer buffer, int baseColor, float alpha) {
        int color = applyAlphaToColor(baseColor, alpha);
        model.render(matrices, buffer, LIGHT, OverlayTexture.DEFAULT_UV, color);
    }

    private static Vec3d getPartDisplacement(CapturedPlayer player, BodyPart part, float maxDisp) {
        long seed = player.getUuid().getMostSignificantBits()
            ^ player.getUuid().getLeastSignificantBits()
            ^ part.ordinal();
        Random rand = Random.create(seed);

        Vec3d base = switch (part) {
            case HEAD -> new Vec3d(0, -1, 0);
            case BODY -> Vec3d.ZERO;
            case LEFT_ARM -> new Vec3d(1, 0, 0);
            case RIGHT_ARM -> new Vec3d(-1, 0, 0);
            case LEFT_LEG -> new Vec3d(0.3, 1, 0);
            case RIGHT_LEG -> new Vec3d(-0.3, 1, 0);
        };

        Vec3d noise = new Vec3d(
            (rand.nextDouble() - 0.5) * 0.5,
            (rand.nextDouble() - 0.5) * 0.5,
            (rand.nextDouble() - 0.5) * 0.5
        );

        return base.add(noise).normalize().multiply(maxDisp);
    }

    private static void setupRenderState(PlayerEntityRenderState state, PlayerEntityModel model) {
        state.leftPantsLegVisible = false;
        state.rightPantsLegVisible = false;
        state.leftSleeveVisible = false;
        state.rightSleeveVisible = false;
        state.jacketVisible = false;
        state.hatVisible = false;

        model.leftPants.visible = false;
        model.rightPants.visible = false;
        model.leftSleeve.visible = false;
        model.rightSleeve.visible = false;
        model.jacket.visible = false;
        model.hat.visible = false;
    }
}
