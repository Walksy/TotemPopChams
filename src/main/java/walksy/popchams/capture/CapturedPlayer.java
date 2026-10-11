package walksy.popchams.capture;

import com.mojang.authlib.GameProfile;
import org.jspecify.annotations.Nullable;
import walksy.popchams.handler.PositionHandler;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CapturedPlayer extends RemotePlayer {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    public float statePitch;
    public float glidingProcess;
    public float flyingRotation;
    public float animPos;
    public PlayerModel model;
    public AvatarRenderState state;
    public boolean autoSpinAttack;
    public boolean applyFlyingYRot;

    public CapturedPlayer(final ClientLevel world, final Player original) {
        super(world, new GameProfile(UUID.randomUUID(), "Captured Player"));
        this.setId(NEXT_ID.decrementAndGet());
        this.copyPosition(original);
        this.xo = original.xo;
        this.yo = original.yo;
        this.zo = original.zo;
        this.yRotO = original.yRotO;
        this.xRotO = original.xRotO;
        this.walkAnimation.setSpeed(original.walkAnimation.speed());
        this.animPos = original.walkAnimation.position();
        Minecraft client = Minecraft.getInstance();
        this.state = (AvatarRenderState) client.getEntityRenderDispatcher().getRenderer(this).createRenderState(this, 0F);

        this.state.attackTime = original.attackAnim;
        this.state.isDiscrete = original.isShiftKeyDown();
        this.state.fallFlyingTimeInTicks = original.getFallFlyingTicks();
        this.flyingRotation = state.flyingYRot;
        this.statePitch = state.xRot;
        this.glidingProcess = state.fallFlyingScale();
        this.autoSpinAttack = state.isAutoSpinAttack;
        this.applyFlyingYRot = state.shouldApplyFlyingYRot;

        PlayerModel originalModel = (PlayerModel)
                ((LivingEntityRenderer<?, ?, ?>) client.getEntityRenderDispatcher().getRenderer(original)).getModel();
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
        this.state.showLeftPants = false;
        this.state.showRightPants = false;
        this.state.showLeftSleeve = false;
        this.state.showRightSleeve = false;
        this.state.showJacket = false;
        this.state.showHat = false;
        this.model.leftPants.visible = false;
        this.model.rightPants.visible = false;
        this.model.leftSleeve.visible = false;
        this.model.rightSleeve.visible = false;
        this.model.jacket.visible = false;
        this.model.hat.visible = false;
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
    public boolean canBeCollidedWith(@Nullable Entity entity) {
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