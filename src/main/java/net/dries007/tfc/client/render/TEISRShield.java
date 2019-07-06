package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.client.model.ModelShieldTFC;
import net.dries007.tfc.objects.items.metal.ItemMetalShield;

public class TEISRShield extends TileEntityItemStackRenderer
{
    private final Item shield;
    private final ModelShieldTFC shieldModel;

    public TEISRShield(ItemMetalShield itemShield, ModelShieldTFC modelShield)
    {
        this.shield = itemShield;
        this.shieldModel = modelShield;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks)
    {
        Item item = itemStackIn.getItem();
        Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.shieldModel.render();
        GlStateManager.popMatrix();
    }
}
