package walksy.popchams;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.OptionalDouble;

public class Layers {

    public static final RenderLayer TRANSLUCENT_ENTITY_HIGHLIGHT = RenderLayer.of(
        "translucent_entity_highlight",
        VertexFormats.POSITION_COLOR,
        VertexFormat.DrawMode.QUADS,
        256,
        false,
        true,
        RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.POSITION_COLOR_PROGRAM)
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
            .cull(RenderPhase.ENABLE_CULLING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
    );

    public static RenderLayer getWireFrame(double lineWidth)
    {
        return RenderLayer.of(
            "wireframe_outline",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            256,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .program(RenderPhase.LINES_PROGRAM)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                .cull(RenderPhase.ENABLE_CULLING)
                .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                .build(true)
        );
    }
}
