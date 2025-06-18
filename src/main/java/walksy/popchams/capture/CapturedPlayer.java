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
        this.prevX = original.prevX;
        this.prevY = original.prevY;
        this.prevZ = original.prevZ;
        this.prevYaw = original.prevYaw;
        this.prevPitch = original.prevPitch;

        this.limbAnimator.setSpeed(original.limbAnimator.getSpeed());
        this.animPos = original.limbAnimator.getPos();


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


        this.model.head.copyTransform(originalModel.head);
        this.model.hat.copyTransform(originalModel.hat);
        this.model.body.copyTransform(originalModel.body);
        this.model.leftArm.copyTransform(originalModel.leftArm);
        this.model.rightArm.copyTransform(originalModel.rightArm);
        this.model.leftLeg.copyTransform(originalModel.leftLeg);
        this.model.rightLeg.copyTransform(originalModel.rightLeg);


        this.bodyYaw = original.bodyYaw;
        this.prevBodyYaw = original.prevBodyYaw;
        this.headYaw = original.headYaw;
        this.prevHeadYaw = original.prevHeadYaw;
        this.handSwingProgress = original.handSwingProgress;
        this.handSwingTicks = original.handSwingTicks;

        this.setSneaking(original.isSneaking());

        this.setPose(original.getPose());
        Byte playerModel = original.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
    }

    public void tickAge()
    {
        this.age++;
        if (age >= Config.CONFIG.instance().lifeTime)
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
    public boolean isCollidable() {
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
