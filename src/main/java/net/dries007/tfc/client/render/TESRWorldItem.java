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

import net.dries007.tfc.objects.te.TEWorldItem;

public class TESRWorldItem extends TileEntitySpecialRenderer<TEWorldItem>
{
    @Override
    public void render(TEWorldItem te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        ItemStack stack = te.inventory.getStackInSlot(0);
        byte rotation = te.getRotation();
        GlStateManager.pushMatrix();
        //GlStateManager.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
        //GlStateManager.rotate(timeD, 0, 1, 0);
        GlStateManager.translate(x + 0.5D, y + 0.03125D, z + 0.5D);
        GlStateManager.scale(.5d,.5d,.5d); //I used doubles here because I think they will cast to that anyway
        GlStateManager.rotate(90f,1f,0f,0f);
        GlStateManager.rotate(90f * (float) rotation, 0f, 0f, 1f);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
