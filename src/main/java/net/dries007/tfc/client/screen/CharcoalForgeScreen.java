/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.CharcoalForgeContainer;
import net.dries007.tfc.common.tileentity.CharcoalForgeTileEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CharcoalForgeScreen extends TileEntityScreen<CharcoalForgeTileEntity, CharcoalForgeContainer>
{
    private static final ResourceLocation FORGE = new ResourceLocation(MOD_ID, "textures/gui/charcoal_forge.png");

    public CharcoalForgeScreen(CharcoalForgeContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, FORGE);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * tile.getSyncableData().get(CharcoalForgeTileEntity.DATA_SLOT_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temp > 0)
        {
            blit(matrixStack, leftPos + 8, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }
    }
}
