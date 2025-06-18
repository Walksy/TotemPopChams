package walksy.popchams.helper;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import walksy.popchams.mixin.ModelPartAccessor;

import java.util.List;

public class WireframeHelper {

    public static void renderModelPartWireframe(ModelPart part, MatrixStack matrices, VertexConsumer buffer, int color, float correction) {
        matrices.push();
        part.rotate(matrices);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float expansion = correction / 16.0f;

        for (ModelPart.Cuboid cuboid : getCuboids(part)) {

            float minX = (cuboid.minX / 16.0f) - expansion;
            float minY = (cuboid.minY / 16.0f) - expansion;
            float minZ = (cuboid.minZ / 16.0f) - expansion;
            float maxX = (cuboid.maxX / 16.0f) + expansion;
            float maxY = (cuboid.maxY / 16.0f) + expansion;
            float maxZ = (cuboid.maxZ / 16.0f) + expansion;

            drawBoxLines(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, color);
        }

        matrices.pop();
    }

    private static void drawBoxLines(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        Vector3f[] vertices = new Vector3f[] {
            new Vector3f(x1, y1, z1), new Vector3f(x2, y1, z1), new Vector3f(x2, y2, z1), new Vector3f(x1, y2, z1),
            new Vector3f(x1, y1, z2), new Vector3f(x2, y1, z2), new Vector3f(x2, y2, z2), new Vector3f(x1, y2, z2)
        };

        int[][] edges = new int[][] {
            {0, 1}, {1, 2}, {2, 3}, {3, 0},
            {4, 5}, {5, 6}, {6, 7}, {7, 4},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        for (int[] edge : edges) {
            Vector3f from = vertices[edge[0]];
            Vector3f to = vertices[edge[1]];

            buffer.vertex(matrix, from.x, from.y, from.z).color(r, g, b, a).normal(1, 1, 0);
            buffer.vertex(matrix, to.x, to.y, to.z).color(r, g, b, a).normal(1, 1, 0);
        }
    }

    //Had to extract this into a separate method because the compiler kept crying, dk why
    private static List<ModelPart.Cuboid> getCuboids(ModelPart part) {
        return ((ModelPartAccessor)(Object) part).getCuboids();
    }
}
