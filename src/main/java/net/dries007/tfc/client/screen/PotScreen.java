/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Tooltips;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotScreen extends BlockEntityScreen<PotBlockEntity, PotContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_cooking_pot.png");

    public PotScreen(PotContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (blockEntity.shouldRenderAsBoiling())
        {
            drawDisabled(poseStack, PotBlockEntity.SLOT_EXTRA_INPUT_START, PotBlockEntity.SLOT_EXTRA_INPUT_END);
        }

        final String text;
        if (blockEntity.shouldRenderAsBoiling())
        {
            text = I18n.get("tfc.tooltip.pot_boiling");
        }
        else if (blockEntity.getOutput() != null && !blockEntity.getOutput().isEmpty())
        {
            text = I18n.get("tfc.tooltip.pot_finished");
        }
        else
        {
            text = I18n.get("tfc.tooltip.pot_ready");
        }

        final int x = 118 - font.width(text) / 2;
        font.draw(poseStack, text, x, 56, 0x404040);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderTooltip(poseStack, mouseX, mouseY);

        final int left = getGuiLeft(), top = getGuiTop();
        if (mouseX >= left + 54 && mouseY >= top + 48 && mouseX < left + 86 && mouseY < top + 74)
        {
            final FluidStack fluid = blockEntity.getCapability(Capabilities.FLUID)
                .map(c -> c.getFluidInTank(0))
                .orElse(FluidStack.EMPTY);
            if (!fluid.isEmpty())
            {
                renderTooltip(poseStack, Tooltips.fluidUnitsAndCapacityOf(fluid, FluidHelpers.BUCKET_VOLUME), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * blockEntity.getTemperature() / Heat.maxVisibleTemperature());
        if (temp > 0)
        {
            blit(poseStack, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }
    }
}
