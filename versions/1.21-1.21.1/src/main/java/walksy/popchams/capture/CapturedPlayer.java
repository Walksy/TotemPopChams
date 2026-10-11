package walksy.popchams.capture;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import walksy.popchams.handler.PositionHandler;

import java.util.UUID;

public class CapturedPlayer extends RemotePlayer {

    public float statePitch;
    public float glidingProcess;
    public float flyingRotation;
    public float animPos;
    public PlayerModel model;
    public boolean autoSpinAttack;
    public boolean applyFlyingYRot;

    public CapturedPlayer(final ClientLevel world, final Player original) {
        super(world, new GameProfile(UUID.randomUUID(), "Captured Player"));
        this.copyPosition(original);
        this.xo = original.xo;
        this.yo = original.yo;
        this.zo = original.zo;
        this.yRotO = original.yRotO;
        this.xRotO = original.xRotO;
        this.walkAnimation.setSpeed(original.walkAnimation.speed());
        this.animPos = original.walkAnimation.position();
        final Minecraft client = Minecraft.getInstance();

        this.statePitch = original.getXRot();
        final float fallFlyingTicks = original.getFallFlyingTicks();
        this.glidingProcess = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        this.autoSpinAttack = original.isAutoSpinAttack();
        this.setupFlyingRotation(original);

        final PlayerModel originalModel = (PlayerModel)
                ((LivingEntityRenderer<?, ?>) client.getEntityRenderDispatcher().getRenderer(original)).getModel();
        this.model = new PlayerModel(client.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        this.copyModelPartTransform(this.model.head, originalModel.head);
        this.copyModelPartTransform(this.model.hat, originalModel.hat);
        this.copyModelPartTransform(this.model.body, originalModel.body);
        this.copyModelPartTransform(this.model.leftArm, originalModel.leftArm);
        this.copyModelPartTransform(this.model.rightArm, originalModel.rightArm);
        this.copyModelPartTransform(this.model.leftLeg, originalModel.leftLeg);
        this.copyModelPartTransform(this.model.rightLeg, originalModel.rightLeg);

        this.yBodyRot = original.yBodyRot;
        this.yBodyRotO = original.yBodyRotO;
        this.yHeadRot = original.yHeadRot;
        this.yHeadRotO = original.yHeadRotO;
        this.attackAnim = original.attackAnim;
        this.swingTime = original.swingTime;

        this.setShiftKeyDown(original.isShiftKeyDown());
        this.setPose(original.getPose());
    }

    public void hideOuterLayers() {
        this.model.leftPants.visible = false;
        this.model.rightPants.visible = false;
        this.model.leftSleeve.visible = false;
        this.model.rightSleeve.visible = false;
        this.model.jacket.visible = false;
        this.model.hat.visible = false;
    }

    private void setupFlyingRotation(final Player original) {
        if (!original.isFallFlying()) {
            return;
        }
        final Vec3 view = original.getViewVector(1.0F);
        final Vec3 movement = original.getDeltaMovement();
        final double movementLength = movement.horizontalDistanceSqr();
        final double viewLength = view.horizontalDistanceSqr();
        if (movementLength <= 0.0D || viewLength <= 0.0D) {
            return;
        }
        final double dot = (movement.x * view.x + movement.z * view.z) / Math.sqrt(movementLength * viewLength);
        final double cross = movement.x * view.z - movement.z * view.x;
        this.flyingRotation = (float) (Math.signum(cross) * Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
        this.applyFlyingYRot = true;
    }

    private void copyModelPartTransform(final ModelPart target, final ModelPart source) {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
        target.xScale = source.xScale;
        target.yScale = source.yScale;
        target.zScale = source.zScale;
    }

    public void tickAge(final int lifeTime) {
        this.tickCount++;
        if (this.tickCount >= lifeTime) {
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
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }
}
