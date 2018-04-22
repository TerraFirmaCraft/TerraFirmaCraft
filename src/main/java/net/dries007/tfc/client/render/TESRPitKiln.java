package net.dries007.tfc.client.render;

import net.dries007.tfc.objects.te.TEPitKiln;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRPitKiln extends TileEntitySpecialRenderer<TEPitKiln>
{
    @Override
    public void render(TEPitKiln te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        World world = te.getWorld();
        //noinspection ConstantConditions
        if (world == null) return;
        NonNullList<ItemStack> items = te.getItems();
        for (int i = 0; i < items.size(); i++)
        {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }
}
