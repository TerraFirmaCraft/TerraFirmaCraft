/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.Random;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.RenderHelpers;

public class ScreenParticle
{
    private float x;
    private float y;
    private float dx;
    private float dy;
    private float rotation;
    private int lifetime;


    private final ResourceLocation texture;
    private final int width;
    private final int height;
    private final int rotationSign;
    private final float scale;

    public ScreenParticle(ResourceLocation texture, float x, float y, float dx, float dy, int width, int height, Random random)
    {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.rotationSign = random.nextBoolean() ? 1 : -1;
        this.scale = Mth.nextFloat(random, 0.25f, 0.6f);
        this.lifetime = 35;

        this.width = width;
        this.height = height;
    }

    public void render(PoseStack poseStack)
    {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, this.texture);

        poseStack.translate(x, y, 0f);
        poseStack.mulPose(RenderHelpers.rotateDegreesZ(rotation));
        poseStack.scale(scale, scale, 1f);

        GuiComponent.blit(poseStack, 0, 0, 0, 0, 0, width, height, height, width);

        poseStack.popPose();
    }

    public void tick()
    {
        x += dx;
        y += dy;
        dx *= 0.97f;
        dy *= 1.03f;
        rotation += (2f * rotationSign);
        lifetime--;
    }

    public boolean shouldBeRemoved()
    {
        return lifetime <= 0;
    }
}
