package walksy.popchams.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public interface PartRenderer {
    void render(MatrixStack matrices, PlayerEntityModel model, String partName, VertexConsumer buffer);
}
