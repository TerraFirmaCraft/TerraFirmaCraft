/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonAnvil extends GuiButton
{
    public GuiButtonAnvil(int id, int x, int y, int width, int height, ResourceLocation texture)
    {
        super(id, x, y, width, height, "");
    }

    // todo: everything here
}
