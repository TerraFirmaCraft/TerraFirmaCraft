/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.container.BlastFurnaceContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class BlastFurnaceScreen extends BlockEntityScreen<BlastFurnaceBlockEntity, BlastFurnaceContainer>
{
    private static final ResourceLocation BLAST_FURNACE = Helpers.identifier("textures/gui/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BLAST_FURNACE);

        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        final int capacity = blockEntity.getCapacity();
        final int fuelCount = blockEntity.getFuelCount();
        final int inputCount = blockEntity.getInputCount();

        final int maximumCapacity = TFCConfig.SERVER.blastFurnaceCapacity.get() * TFCConfig.SERVER.blastFurnaceMaxChimneyHeight.get();

        final boolean lit = blockEntity.getBlockState().getValue(BlastFurnaceBlock.LIT);

        // Render the two meters: ore and fuel
        renderCapacityLimitedFillMeter(graphics, 42, 22, lit ? 216 : 226, maximumCapacity, capacity, inputCount);
        renderCapacityLimitedFillMeter(graphics, 124, 22, 206, maximumCapacity, capacity, fuelCount);

        // Render temperature indicator
        final int temperature = Heat.scaleTemperatureForGui(blockEntity.getTemperature());
        if (temperature > 0)
        {
            graphics.blit(texture, leftPos + 8, topPos + 76 - Math.min(51, temperature), 176, 0, 15, 5);
        }

        // Render output fluid tank
        final FluidStack fluid = blockEntity.getInventory().getFluidInTank(0);
        if (!fluid.isEmpty())
        {
            final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluid);
            final int fillHeight = (int) Math.ceil((float) 31 * fluid.getAmount() / TFCConfig.SERVER.blastFurnaceFluidCapacity.get());

            RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 70, topPos + 84 - fillHeight, 36, fillHeight, 16, 16);
        }

        resetToBackgroundSprite();
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        final int capacity = blockEntity.getCapacity();
        final int fuelCount = blockEntity.getFuelCount();
        final int inputCount = blockEntity.getInputCount();

        final FluidStack fluid = blockEntity.getInventory().getFluidInTank(0);

        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 42, topPos + 22, 10, 66))
        {
            graphics.renderTooltip(font, Component.translatable("tfc.tooltip.blast_furnace_ore", inputCount, capacity), mouseX, mouseY);
        }
        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 124, topPos + 22, 10, 66))
        {
            graphics.renderTooltip(font, Component.translatable("tfc.tooltip.blast_furnace_fuel", fuelCount, capacity), mouseX, mouseY);
        }
        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 70, topPos + 54, 36, 31) && !fluid.isEmpty())
        {
            graphics.renderTooltip(font, Tooltips.fluidUnitsOf(fluid), mouseX, mouseY);
        }
        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 8, topPos + 76 - 51, 15, 51))
        {
            final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
            if (text != null)
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }
    }

    private void renderCapacityLimitedFillMeter(GuiGraphics graphics, int x, int y, int fillU, int maximum, int capacity, int content)
    {
        if (capacity == 0)
        {
            // No capacity, so render a full dotted region.
            graphics.blit(texture, leftPos + x, topPos + y, 246, 0, 10, 66);
        }
        else if (content == 0)
        {
            // If we have capacity but no content, we render just an top section of the empty content bar.
            final int emptyHeight = (64 * capacity) / maximum;
            graphics.blit(texture, leftPos + x, topPos + y + 64 - emptyHeight, 236, 0, 10, 1 + emptyHeight);
        }
        else
        {
            // Both capacity and content are > 0, so we render the top section of an empty content bar, and the bottom section of a full content bar
            final int emptyHeight = (64 * capacity) / maximum;
            final int fillHeight = (64 * content) / maximum;

            graphics.blit(texture, leftPos + x, topPos + y + 64 - emptyHeight, 236, 0, 10, 1 + emptyHeight - fillHeight);
            graphics.blit(texture, leftPos + x, topPos + y + 65 - fillHeight, fillU, 1, 10, fillHeight);
        }
    }

}
