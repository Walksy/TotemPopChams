package walksy.popchams.config;

import main.walksy.lib.api.WalksyLibApi;
import main.walksy.lib.api.config.Config;
import main.walksy.lib.api.config.option.AvailabilityMode;
import main.walksy.lib.api.config.option.Category;
import main.walksy.lib.api.config.option.Option;
import main.walksy.lib.api.config.option.OptionGroup;
import main.walksy.lib.api.config.option.Options;
import main.walksy.lib.api.config.value.WLColor;
import main.walksy.lib.api.render.PreviewCanvas;
import main.walksy.lib.api.config.PathUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TotemPopChamsConfig implements WalksyLibApi {
    private static final WLColor DEFAULT_FILLED = new WLColor(137, 0, 255, 150);
    private static final WLColor DEFAULT_WIREFRAME = new WLColor(100, 0, 255, 150);
    private static final float TICKS_PER_SECOND = 20f;
    private static final float IDLE_LOOP_TICKS = 60f;
    private static final float REST_TICKS = 10f;
    private static final float FADE_START = 0.8f;
    private static final float PIXEL_WIDTH = 0.8f / 16f;
    private static final float PIXEL_HEIGHT = 0.9f / 16f;
    private static final float[][] PARTS = {
        {-4f, 24f, -4f, 4f, 32f, 4f, 0f, 1f, 0f},
        {-4f, 12f, -2f, 4f, 24f, 2f, 0f, 0f, 0f},
        {4f, 12f, -2f, 8f, 24f, 2f, 1f, 0f, 0f},
        {-8f, 12f, -2f, -4f, 24f, 2f, -1f, 0f, 0f},
        {0f, 0f, -2f, 4f, 12f, 2f, 0.3f, -1f, 0f},
        {-4f, 0f, -2f, 0f, 12f, 2f, -0.3f, -1f, 0f}
    };
    public static boolean modEnabled = true;
    public static boolean showOwnPops = false;
    public static boolean fadeOut = true;
    public static int lifeTime = 50;
    public static boolean disperse = false;
    public static double disperseSpeed = 1.5;
    public static double disperseMaxDistance = 1;
    public static boolean filledModelEnabled = true;
    public static WLColor filledColor = DEFAULT_FILLED.copy();
    public static boolean wireframeEnabled = true;
    public static WLColor wireframeColor = DEFAULT_WIREFRAME.copy();
    public static double wireframeThickness = 2.0;

    private static void drawPreview(final PreviewCanvas canvas) {
        canvas.frame(-8f * PIXEL_WIDTH, 0f, -4f * PIXEL_WIDTH, 8f * PIXEL_WIDTH, 32f * PIXEL_HEIGHT, 4f * PIXEL_WIDTH);
        final float loop = (fadeOut ? Math.max(1f, lifeTime) : IDLE_LOOP_TICKS) + REST_TICKS;
        final float ticks = (canvas.time() * TICKS_PER_SECOND) % loop;
        final float speed = (float) Math.pow(Math.max(1.0, Math.min(10.0, disperseSpeed)) / 10.0, 2) * 2f;
        final float spread = disperse ? (float) Math.min(ticks * speed, disperseMaxDistance) : 0f;
        float alpha = 1f;
        if (fadeOut) {
            final float fadeFrom = lifeTime * FADE_START;
            alpha = ticks >= fadeFrom ? Math.max(0f, 1f - (ticks - fadeFrom) / Math.max(1f, lifeTime - fadeFrom)) : 1f;
            if (disperse && disperseMaxDistance > 0) {
                alpha *= (float) (1f - spread / disperseMaxDistance);
            }
        }
        final int fill = fade(filledColor.getRGB(), alpha);
        final int wire = fade(wireframeColor.getRGB(), alpha);
        final float thickness = (float) wireframeThickness;
        for (final float[] part : PARTS) {
            final float length = (float) Math.sqrt(part[6] * part[6] + part[7] * part[7] + part[8] * part[8]);
            final float scale = length > 0f ? spread / length : 0f;
            final float dx = part[6] * scale;
            final float dy = part[7] * scale;
            final float dz = part[8] * scale;
            final float x0 = part[0] * PIXEL_WIDTH + dx;
            final float y0 = part[1] * PIXEL_HEIGHT + dy;
            final float z0 = part[2] * PIXEL_WIDTH + dz;
            final float x1 = part[3] * PIXEL_WIDTH + dx;
            final float y1 = part[4] * PIXEL_HEIGHT + dy;
            final float z1 = part[5] * PIXEL_WIDTH + dz;
            if (filledModelEnabled) {
                canvas.filledBox(x0, y0, z0, x1, y1, z1, fill, fill);
            }
            if (wireframeEnabled) {
                canvas.box(x0, y0, z0, x1, y1, z1, wire, wire, thickness);
            }
        }
    }

    private static int fade(final int argb, final float alpha) {
        return (Math.round(((argb >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, alpha))) << 24) | (argb & 0x00FFFFFF);
    }

    private static Option<Boolean> toggle(final String name, final String saveKey, final Supplier<Boolean> getter, final boolean defaultValue, final Consumer<Boolean> setter) {
        return toggle(name, saveKey, getter, defaultValue, setter, null);
    }

    private static Option<Boolean> toggle(final String name, final String saveKey, final Supplier<Boolean> getter, final boolean defaultValue, final Consumer<Boolean> setter, final String description) {
        return Options.ofBoolean(name, getter, defaultValue, setter).saveKey(saveKey).description(description).build();
    }

    @Override
    public Config define() {
        return Config.builder()
            .path(PathUtils.ofConfigDir("totempopchams"))
            .category(Category.builder("General")
                .group(OptionGroup.builder("Preview")
                    .option(Options.ofPreview("Preview", TotemPopChamsConfig::drawPreview).build())
                    .build())
                .group(OptionGroup.builder("General")
                    .option(toggle("Mod Enabled", "Mod Enabled", () -> modEnabled, true, v -> modEnabled = v))
                    .option(toggle("Show Own Pops", "Show Own Pops", () -> showOwnPops, false, v -> showOwnPops = v))
                    .build())
                .group(OptionGroup.builder("Fade")
                    .option(toggle("Fade Out", "Fade Over Time", () -> fadeOut, true, v -> fadeOut = v))
                    .option(Options.ofNumber("Fade Time", () -> lifeTime, 50, v -> lifeTime = v)
                        .range(0, 250, 1)
                        .saveKey("Fading Life Time")
                        .availability(() -> fadeOut, AvailabilityMode.HIDE)
                        .build())
                    .build())
                .group(OptionGroup.builder("Disperse")
                    .option(toggle("Disperse", "Disperse Enabled", () -> disperse, false, v -> disperse = v, "Body parts drift apart as the cham fades"))
                    .option(Options.ofNumber("Disperse Speed", () -> disperseSpeed, 1.5D, v -> disperseSpeed = v)
                        .range(0D, 10D, 0.5D)
                        .availability(() -> disperse, AvailabilityMode.HIDE)
                        .build())
                    .option(Options.ofNumber("Disperse Distance", () -> disperseMaxDistance, 1D, v -> disperseMaxDistance = v)
                        .range(0D, 8D, 0.1D)
                        .saveKey("Disperse Max Distance")
                        .availability(() -> disperse, AvailabilityMode.HIDE)
                        .build())
                    .build())
                .group(OptionGroup.builder("Fill")
                    .option(toggle("Filled Model", "Filled Model Enabled", () -> filledModelEnabled, true, v -> filledModelEnabled = v))
                    .option(Options.ofColor("Fill Color", () -> filledColor, DEFAULT_FILLED, v -> filledColor = v)
                        .saveKey("Filled Model Color")
                        .availability(() -> filledModelEnabled, AvailabilityMode.HIDE)
                        .build())
                    .build())
                .group(OptionGroup.builder("Wireframe")
                    .option(toggle("Wireframe", "Wireframe Enabled", () -> wireframeEnabled, true, v -> wireframeEnabled = v))
                    .option(Options.ofColor("Wireframe Color", () -> wireframeColor, DEFAULT_WIREFRAME, v -> wireframeColor = v)
                        .availability(() -> wireframeEnabled, AvailabilityMode.HIDE)
                        .build())
                    .option(Options.ofNumber("Wireframe Thickness", () -> wireframeThickness, 2D, v -> wireframeThickness = v)
                        .range(0D, 5D, 0.1D)
                        .availability(() -> wireframeEnabled, AvailabilityMode.HIDE)
                        .build())
                    .build())
                .build())
            .build();
    }
}
