/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

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

    public ScreenParticle(ResourceLocation texture, float x, float y, float dx, float dy, int width, int height, RandomSource random)
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

    public void render(GuiGraphics graphics)
    {
        final PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(x, y, 0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        poseStack.scale(scale, scale, 1f);

        graphics.blit(texture, 0, 0, 0, 0, 0, width, height, width, height);

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
