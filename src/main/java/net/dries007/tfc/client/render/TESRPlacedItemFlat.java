/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEPlacedItemFlat;

@SideOnly(Side.CLIENT)
public class TESRPlacedItemFlat extends TileEntitySpecialRenderer<TEPlacedItemFlat>
{
    @Override
    public void render(TEPlacedItemFlat te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        ItemStack stack = te.getStack();
        byte rotation = te.getRotation();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.03125D, z + 0.5D);
        GlStateManager.scale(.5f, .5f, .5f);
        GlStateManager.rotate(90f, 1f, 0f, 0f);
        GlStateManager.rotate(90f * (float) rotation, 0f, 0f, 1f);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
