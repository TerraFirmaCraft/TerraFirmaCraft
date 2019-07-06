package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.ModelShieldTFC;

public class TEISRShield extends TileEntityItemStackRenderer
{
    private final ModelShieldTFC shieldModel;
    private final ResourceLocation shieldTexture;

    public TEISRShield(ResourceLocation textureLocation)
    {
        this.shieldModel = new ModelShieldTFC();
        this.shieldTexture = textureLocation;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks)
    {
        Item item = itemStackIn.getItem();
        Minecraft.getMinecraft().getTextureManager().bindTexture(shieldTexture);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.shieldModel.render();
        GlStateManager.popMatrix();
    }
}
