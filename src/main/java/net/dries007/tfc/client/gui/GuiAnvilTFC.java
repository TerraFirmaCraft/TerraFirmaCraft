/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.objects.te.TEAnvilTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiAnvilTFC extends GuiContainerTFC
{
    private static final ResourceLocation ANVIL_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil.png");

    private final TEAnvilTFC te;

    public GuiAnvilTFC(Container container, InventoryPlayer playerInv, TEAnvilTFC te)
    {
        super(container, playerInv, ANVIL_BACKGROUND);

        this.te = te;

        // todo: everything
    }
}
