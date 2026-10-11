package walksy.popchams;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class Layers {

    private static final RenderPipeline TRANSLUCENT_ENTITY_HIGHLIGHT_PIPELINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withCull(true)
            .withLocation("translucent_entity_highlight")
            .build();

    public static RenderType TRANSLUCENT_ENTITY_HIGHLIGHT = RenderType.create(
            "translucent_entity_highlight",
            RenderSetup.builder(TRANSLUCENT_ENTITY_HIGHLIGHT_PIPELINE)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    private static final RenderPipeline WIREFRAME_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withCull(false)
            .withLocation("wireframe_outline")
            .build();

    public static RenderType WIREFRAME = RenderType.create(
            "wireframe_outline",
            RenderSetup.builder(WIREFRAME_PIPELINE)
                    .sortOnUpload()
                    .createRenderSetup()
    );
}