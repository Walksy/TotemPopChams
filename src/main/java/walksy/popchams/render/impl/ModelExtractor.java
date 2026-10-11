package walksy.popchams.render.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public interface ModelExtractor {
    void extractModelPart(final ModelPart part, final PoseStack matrices, final VertexConsumer buffer, final int color, final double lineWidth);
}
