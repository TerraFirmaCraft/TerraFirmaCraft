package net.dries007.tfc.client;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderHelpers
{
    public static void setShaderColor(int color)
    {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
    }
}
