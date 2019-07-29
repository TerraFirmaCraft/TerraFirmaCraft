/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonTFC extends GuiButton
{
    public GuiButtonTFC(int buttonId, int x, int y, String buttonText)
    {
        super(buttonId, x, y, buttonText);
    }

    public GuiButtonTFC(int buttonId, int x, int y, int w, int h, String buttonText)
    {
        super(buttonId, x, y, w, h, buttonText);
    }

    protected void drawItemStack(ItemStack stack, int x, int y)
    {
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        //FontRenderer font = stack.getItem().getFontRenderer(stack);
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        //itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

}
