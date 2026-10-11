package walksy.popchams.render;

import net.minecraft.util.Mth;
import walksy.popchams.capture.CapturedPlayer;
import walksy.popchams.config.TotemPopChamsConfig;

public class ChamConfigState {
    public boolean renderFillModel;
    public boolean renderWireframe;
    public boolean disperse;
    public boolean fadeOut;
    public double disperseSpeed;
    public double disperseMaxDistance;
    public double lifeTime;
    public double wireframeThickness;
    public int filledColor;
    public int wireframeColor;
    public float displacementAmount;
    public float alpha;

    public void updateConfigRenderState(final CapturedPlayer player, final float delta) {
        this.renderFillModel = TotemPopChamsConfig.filledModelEnabled;
        this.renderWireframe = TotemPopChamsConfig.wireframeEnabled;
        this.disperse = TotemPopChamsConfig.disperse;
        this.disperseSpeed = TotemPopChamsConfig.disperseSpeed;
        this.disperseMaxDistance = TotemPopChamsConfig.disperseMaxDistance;
        this.filledColor = TotemPopChamsConfig.filledColor.getRGB();
        this.wireframeColor = TotemPopChamsConfig.wireframeColor.getRGB();
        this.fadeOut = TotemPopChamsConfig.fadeOut;
        this.lifeTime = TotemPopChamsConfig.lifeTime;
        this.wireframeThickness = TotemPopChamsConfig.wireframeThickness;
        this.displacementAmount = this.displacement(player, alpha);
        this.alpha = this.alpha(player, delta);
    }

    private float displacement(final CapturedPlayer player, final float delta) {
        final float s = Math.max(0, player.tickCount - 1 + delta);
        final float ss = (float) Mth.clamp(this.disperseSpeed, 1f, 10f);
        final float sm = (float) Math.pow(ss / 10f, 2) * 2f;
        return this.disperse ? (float) Math.min(s * sm, this.disperseMaxDistance) : 0f;
    }

    private float alpha(final CapturedPlayer player, final float delta) {
        if (!this.fadeOut) {
            return 1f;
        }

        final float s = Math.max(0, player.tickCount - 1 + delta);
        final float f = (float) (this.lifeTime * 0.8f);
        final float l = s >= f ? (float) (1f - ((s - f) / (this.lifeTime - f))) : 1f;
        final float d = this.disperse ? (float) (1f - (this.displacementAmount / this.disperseMaxDistance)) : 1f;
        return Mth.clamp(l * d, 0f, 1f);
    }
}
