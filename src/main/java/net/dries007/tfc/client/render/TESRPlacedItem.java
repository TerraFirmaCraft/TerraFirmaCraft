/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.te.TEPlacedItem;

@SideOnly(Side.CLIENT)
public class TESRPlacedItem extends TileEntitySpecialRenderer<TEPlacedItem>
{
    @Override
    public void render(TEPlacedItem te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        World world = te.getWorld();
        //noinspection ConstantConditions
        if (world == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y, z);

        GlStateManager.pushMatrix();
        IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (cap != null)
        {
            float timeD = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            GlStateManager.translate(0.25, 0.25, 0.25);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.pushAttrib();

            if (te.holdingLargeItem())
            {
                ItemStack stack = cap.getStackInSlot(0);
                if (!stack.isEmpty())
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.25, 0, 0.25);
                    GlStateManager.rotate(timeD, 0, 1, 0);
                    renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    GlStateManager.popMatrix();
                }
            }
            else
            {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                for (int i = 0; i < cap.getSlots(); i++)
                {
                    ItemStack stack = cap.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
                    GlStateManager.rotate(timeD, 0, 1, 0);
                    renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    GlStateManager.popMatrix();
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();


            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }
}
