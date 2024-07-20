/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class PotScreen extends BlockEntityScreen<PotBlockEntity, PotContainer>
{
    private static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/fire_pit_cooking_pot.png");

    public PotScreen(PotContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (blockEntity.shouldRenderAsBoiling())
        {
            drawDisabled(graphics, PotBlockEntity.SLOT_EXTRA_INPUT_START, PotBlockEntity.SLOT_EXTRA_INPUT_END);
        }

        final MutableComponent text = Component.empty();
        if (blockEntity.shouldRenderAsBoiling())
        {
            text.append(Component.translatable("tfc.tooltip.pot_boiling"));
        }
        else
        {
            if (blockEntity.getOutput() != null && !blockEntity.getOutput().isEmpty())
            {
                final BlockEntityTooltip tooltip = blockEntity.getOutput().getTooltip();
                if (tooltip != null && blockEntity.getLevel() != null)
                {
                    final List<Component> fakeTooltip = new ArrayList<>();
                    tooltip.display(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity, fakeTooltip::add);
                    text.append(fakeTooltip.get(0));
                }
                else
                {
                    text.append(Component.translatable("tfc.tooltip.pot_finished"));
                }
            }
        }

        final int x = 118 - font.width(text) / 2;
        graphics.drawString(font, text, x, 80, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (RenderHelpers.isInside(mouseX, mouseY, getGuiLeft() + 121, getGuiTop() + 30, 162 - 121, 58 - 30))
        {
            final FluidStack fluid = blockEntity.getInventory().getFluidInTank(0);
            if (!fluid.isEmpty())
            {
                graphics.renderTooltip(font, Tooltips.fluidUnitsAndCapacityOf(fluid, FluidHelpers.BUCKET_VOLUME), mouseX, mouseY);
            }
        }

        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 30, topPos + 76 - 51, 15, 51))
        {
            final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
            if (text != null)
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        if (Helpers.isJEIEnabled())
        {
            graphics.blit(texture, getGuiLeft() + 77, getGuiTop() + 6, 247, 0, 9, 14);
        }

        int temp = Heat.scaleTemperatureForGui(blockEntity.getTemperature());
        if (temp > 0)
        {
            graphics.blit(texture, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }

        if (blockEntity.getTemperature() > 0)
        {
            graphics.blit(BACKGROUND, leftPos + 121, topPos + 58, 192, 0, 13, 13);
            graphics.blit(BACKGROUND, leftPos + 136, topPos + 58, 192, 0, 13, 13);
            graphics.blit(BACKGROUND, leftPos + 151, topPos + 58, 192, 0, 13, 13);
        }

        if (blockEntity.shouldRenderAsBoiling())
        {
            final int ticks = blockEntity.getBoilingTicks() % 35;
            final int vHeight = Mth.ceil(ticks / 35f * 20f);
            graphics.blit(BACKGROUND, leftPos + 131, topPos + 10 + 20 - vHeight, 193, 16 + 21 - vHeight, 11, vHeight);
            graphics.blit(BACKGROUND, leftPos + 144, topPos + 10 + 20 - vHeight, 193, 16 + 21 - vHeight, 11, vHeight);
        }
        int fluidColor = -1;
        final PotOutput output = blockEntity.getOutput();
        if (output != null && !output.isEmpty())
        {
            if (output.getRenderTexture() != null)
            {
                final TextureAtlasSprite sprite = RenderHelpers.blockTexture(output.getRenderTexture());
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 133, topPos + 33, 20, 6, 16, 16);
                RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 131, topPos + 35, 2, 2, 16, 16);
                RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 153, topPos + 35, 2, 2, 16, 16);
                return;
            }
            fluidColor = output.getFluidColor();
        }
        if (fluidColor == -1)
        {
            final FluidStack fluid = blockEntity.getInventory().getFluidInTank(0);
            if (!fluid.isEmpty())
            {
                fluidColor = RenderHelpers.getFluidColor(fluid);
            }
        }
        if (fluidColor != -1)
        {
            RenderHelpers.setShaderColor(graphics, fluidColor);
            graphics.blit(BACKGROUND, leftPos + 131, topPos + 33, 208, 0, 24, 6);
            resetToBackgroundSprite();
        }
    }
}
