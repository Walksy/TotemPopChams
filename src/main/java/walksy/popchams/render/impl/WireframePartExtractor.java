package walksy.popchams.render.impl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import walksy.popchams.mixin.ModelPartAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;

public class WireframePartExtractor implements ModelExtractor {

    @Override
    public void extractModelPart(final ModelPart part, final PoseStack matrices, final VertexConsumer buffer, final int color, final double lineWidth) {
        matrices.pushPose();
        part.translateAndRotate(matrices);
        final Matrix4f matrix = matrices.last().pose();
        for (ModelPart.Cube cuboid : this.getCuboids(part)) {
            final float minX = (cuboid.minX / 16.0f);
            final float minY = (cuboid.minY / 16.0f);
            final float minZ = (cuboid.minZ / 16.0f);
            final float maxX = (cuboid.maxX / 16.0f);
            final float maxY = (cuboid.maxY / 16.0f);
            final float maxZ = (cuboid.maxZ / 16.0f);
            this.boxLines(buffer, matrix, (float) lineWidth, minX, minY, minZ, maxX, maxY, maxZ, color);
        }
        matrices.popPose();
    }

    private void boxLines(final VertexConsumer buffer, final Matrix4f matrix, final float lineWidth, final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final int color) {
        final Vector3f[] vertices = new Vector3f[] {
            new Vector3f(x1, y1, z1), new Vector3f(x2, y1, z1), new Vector3f(x2, y2, z1), new Vector3f(x1, y2, z1),
            new Vector3f(x1, y1, z2), new Vector3f(x2, y1, z2), new Vector3f(x2, y2, z2), new Vector3f(x1, y2, z2)
        };

        final int[][] edges = new int[][] {
            {0, 1}, {1, 2}, {2, 3}, {3, 0},
            {4, 5}, {5, 6}, {6, 7}, {7, 4},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        final int a = (color >> 24) & 0xFF;
        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = color & 0xFF;

        for (final int[] edge : edges) {
            final Vector3f from = vertices[edge[0]];
            final Vector3f to = vertices[edge[1]];
            final Vector3f normal = new Vector3f(to).sub(from).normalize();

            buffer.addVertex(matrix, from.x, from.y, from.z)
                .setColor(r, g, b, a)
                .setLineWidth(lineWidth)
                .setNormal(normal.x, normal.y, normal.z);
            buffer.addVertex(matrix, to.x, to.y, to.z)
                .setColor(r, g, b, a)
                .setLineWidth(lineWidth)
                .setNormal(normal.x, normal.y, normal.z);
        }
    }

    private List<ModelPart.Cube> getCuboids(final ModelPart part) {
        return ((ModelPartAccessor)(Object) part).getCuboids();
    }
}
