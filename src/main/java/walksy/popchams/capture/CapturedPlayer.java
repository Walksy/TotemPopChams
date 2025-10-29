package walksy.popchams.capture;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import walksy.popchams.config.Config;

import walksy.popchams.handler.PositionHandler;

import java.util.UUID;

import static walksy.popchams.TotemPopChams.mc;

public class CapturedPlayer extends OtherClientPlayerEntity {

    public float animPos;
    public float statePitch;
    public float glidingProcess;
    public float flyingRotation;
    public PlayerEntityModel model;
    public PlayerEntityRenderState state;



    public CapturedPlayer(AbstractClientPlayerEntity original) {
        super(MinecraftClient.getInstance().world, new GameProfile(UUID.randomUUID(), "Captured Player"));

        this.copyPositionAndRotation(original);
        this.lastX = original.lastX;
        this.lastY = original.lastY;
        this.lastZ = original.lastZ;
        this.lastYaw = original.lastYaw;
        this.lastPitch = original.lastPitch;

        this.limbAnimator.setSpeed(original.limbAnimator.getSpeed());
        this.animPos = original.limbAnimator.getAnimationProgress();


        state = (PlayerEntityRenderState) mc.getEntityRenderDispatcher().getRenderer(this).getAndUpdateRenderState(this, 0F);
        state.handSwingProgress = original.handSwingProgress;
        state.sneaking = original.isSneaking();
        state.glidingTicks = original.getGlidingTicks();

        flyingRotation = state.flyingRotation;
        statePitch = state.pitch;
        glidingProcess = state.getGlidingProgress();

        PlayerEntityModel originalModel = (PlayerEntityModel)
            ((LivingEntityRenderer<?, ?, ?>) mc.getEntityRenderDispatcher().getRenderer(original)).getModel();

        this.model = new PlayerEntityModel(
            mc.getLoadedEntityModels().getModelPart(EntityModelLayers.PLAYER),
            false
        );


        this.model.head.setTransform(originalModel.head.getTransform());
        this.model.hat.setTransform(originalModel.hat.getTransform());
        this.model.body.setTransform(originalModel.body.getTransform());
        this.model.leftArm.setTransform(originalModel.leftArm.getTransform());
        this.model.rightArm.setTransform(originalModel.rightArm.getTransform());
        this.model.leftLeg.setTransform(originalModel.leftLeg.getTransform());
        this.model.rightLeg.setTransform(originalModel.rightLeg.getTransform());


        this.bodyYaw = original.bodyYaw;
        this.lastBodyYaw = original.lastBodyYaw;
        this.headYaw = original.headYaw;
        this.lastHeadYaw = original.lastHeadYaw;
        this.handSwingProgress = original.handSwingProgress;
        this.handSwingTicks = original.handSwingTicks;

        this.setSneaking(original.isSneaking());

        this.setPose(original.getPose());
        Byte playerModel = original.getDataTracker().get(PlayerEntity.PLAYER_MODE_CUSTOMIZATION_ID);
        dataTracker.set(PlayerEntity.PLAYER_MODE_CUSTOMIZATION_ID, playerModel);
    }

    public void tickAge()
    {
        this.age++;
        if (age >= walksy.popchams.config.Config.CONFIG.instance().lifeTime)
        {
            this.remove(RemovalReason.DISCARDED);
            PositionHandler.getPositions().remove(this);
        }
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable(Entity entity) {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
    }
}
