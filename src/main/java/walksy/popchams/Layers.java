package walksy.popchams;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.util.OptionalDouble;

public class Layers {

    private static final RenderPipeline TRANSLUCENT_ENTITY_HIGHLIGHT_PIPELINE = RenderPipeline.builder(
            RenderPipelines.POSITION_COLOR_SNIPPET)
            .withCull(true)
            .withLocation("translucent_entity_highlight")
            .build();

    public static RenderLayer TRANSLUCENT_ENTITY_HIGHLIGHT = RenderLayer.of(
            "translucent_entity_highlight",
            256,
            false,
            true,
            TRANSLUCENT_ENTITY_HIGHLIGHT_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    private static final RenderPipeline WIREFRAME_PIPELINE = RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withCull(true)
            .withLocation("wireframe_outline")
            .build();

    public static RenderLayer getWireFrame(double lineWidth){
        return RenderLayer.of(
                "wireframe_outline",
                256,
                false,
                true,
                WIREFRAME_PIPELINE,
                RenderLayer.MultiPhaseParameters.builder()
                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                        .build(true)
        );

    }
}
