/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Using two different methods to obtain a {@link VertexConsumer} require two different forms to manage the {@code end()}
 * <ul>
 *     <li>When using a {@link Tesselator}, the {@code getBuilder().begin(...)} is replaced with {@code begin(...)}, and the {@code end()}
 *     is replaced with {@code BufferUploader.drawWithShader(buffer.buildOrThrow())}</li>
 *     <li>When using a {@link MultiBufferSource}, the vertexes are ended and handled automatically</li>
 * </ul>
 */
@SuppressWarnings("unused")
public interface DocumentedVertexConsumer extends VertexConsumer
{
    /**
     * Begins a vertex with a provided position. Must be called first at the start of each vertex.
     * Replaces {@code vertex(x, y, z)} from 1.20.1
     */
    @Override
    VertexConsumer addVertex(float x, float y, float z);

    /**
     * Overload for {@link #addVertex(float, float, float)} that provides a {@code pos} position
     */
    @Override
    VertexConsumer addVertex(Vector3f pos);

    /**
     * Overload for {@link #addVertex(float, float, float)} that provides a {@code pose} and a {@code pos} position
     */
    @Override
    VertexConsumer addVertex(PoseStack.Pose pose, Vector3f pos);

    /**
     * Overload for {@link #addVertex(float, float, float)} that provides a {@code pose} and a {@code (x, y, z)} position
     */
    @Override
    VertexConsumer addVertex(PoseStack.Pose pose, float x, float y, float z);

    /**
     * Overload for {@link #addVertex(float, float, float)} that provides a {@code pose} and a {@code (x, y, z)} position
     */
    @Override
    VertexConsumer addVertex(Matrix4f pose, float x, float y, float z);

    /**
     * This is an overload for {@link #addVertex(float, float, float)} that also configures the following additional elements
     * <ul>
     *     <li>{@link #setColor(int)} with the provided color</li>
     *     <li>{@link #setUv(float, float)} with the sprite U, V coordinates</li>
     *     <li>{@link #setOverlay(int)} with the provided packed overlay value</li>
     *     <li>{@link #setLight(int)} with the provided packed light value</li>
     *     <li>{@link #setNormal(float, float, float)} with the provided normal value</li>
     * </ul>
     */
    @Override
    void addVertex(
        float x, float y, float z,
        int color,
        float u, float v,
        int packedOverlay,
        int packedLight,
        float normalX, float normalY, float normalZ
    );

    /**
     * Set the color of a vertex. Replaces {@code color(red, green, blue, alpha)} from 1.20.1
     */
    @Override
    VertexConsumer setColor(int red, int green, int blue, int alpha);

    /**
     * Set the UV of a vertex. The value of these will depend on the vertex format? Replaces {@code uv(float, float)} from 1.20.1
     * Typically this is {@code sprite.getU()} and {@code sprite.getV()}
     */
    @Override
    VertexConsumer setUv(float u, float v);

    /**
     * Sets the UV1 of a vertex. This is typically the overlay coordinates, which are sometimes packed into a single {@code int}.
     * Replaces {@code overlayCoords(int, int)} from 1.20.1
     * @see #setOverlay(int)
     */
    @Override
    VertexConsumer setUv1(int u, int v);

    /**
     * Sets the UV1 using a packed pair of {@code short} values. Replaces {@code overlayCoords(int)} from 1.20.1
     */
    @Override
    VertexConsumer setOverlay(int packedOverlay);

    /**
     * Sets the UV2 of a vertex. This is typically the light coordinates, which are sometimes packed into a single {@code int}.
     * Replaces {@code uv2(int)} from 1.20.1
     * @see #setLight(int)
     */
    @Override
    VertexConsumer setUv2(int u, int v);

    /**
     * Sets the UV2 using a packed pair of {@code short} values. Replaces {@code uv2(int)} from 1.20.1
     */
    @Override
    VertexConsumer setLight(int packedLight);

    /**
     * Sets the normal of a vertex. This is typically obtained via the pose unsealedStack, but may need transformations to be accurate.
     */
    @Override
    VertexConsumer setNormal(float normalX, float normalY, float normalZ);

    /**
     * Overload for {@link #setNormal(float, float, float)} that also takes a pose
     * @see #setNormal(float, float, float)
     */
    @Override
    VertexConsumer setNormal(PoseStack.Pose pose, float normalX, float normalY, float normalZ);
}
