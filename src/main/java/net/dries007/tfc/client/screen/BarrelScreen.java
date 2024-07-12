/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.button.BarrelSealButton;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class BarrelScreen extends BlockEntityScreen<BarrelBlockEntity, BarrelContainer>
{
    private static final Component SEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");
    private static final int MAX_RECIPE_NAME_LENGTH = 100;

    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/barrel.png");

    public BarrelScreen(BarrelContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 12;
        imageHeight += 12;
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new BarrelSealButton(blockEntity, getGuiLeft(), getGuiTop(), isSealed() ? UNSEAL : SEAL));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (isSealed())
        {
            drawDisabled(graphics, BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, BarrelBlockEntity.SLOT_ITEM);

            // Draw the text displaying both the seal date, and the recipe name
            final @Nullable BarrelRecipe recipe = blockEntity.getRecipe();
            if (recipe != null)
            {
                FormattedText resultText = recipe.getTranslationComponent();
                if (font.width(resultText) > MAX_RECIPE_NAME_LENGTH)
                {
                    int line = 0;
                    for (FormattedCharSequence text : font.split(resultText, MAX_RECIPE_NAME_LENGTH))
                    {
                        graphics.drawString(font, text, 70 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(text), 2), titleLabelY + (line * font.lineHeight), 0x404040, false);
                        line++;
                    }
                }
                else
                {
                    graphics.drawString(font, resultText.getString(), 70 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(resultText), 2), 61, 0x404040, false);
                }
            }
            String date = ICalendar.getTimeAndDate(Calendars.CLIENT.ticksToCalendarTicks(blockEntity.getSealedTick()), Calendars.CLIENT.getCalendarDaysInMonth()).getString();
            graphics.drawString(font, date, imageWidth / 2 - font.width(date) / 2, 74, 0x404040, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        if (Helpers.isJEIEnabled())
        {
            graphics.blit(texture, getGuiLeft() + 92, getGuiTop() + 21, 227, 0, 9, 14);
        }

        blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (!fluidStack.isEmpty())
            {
                final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluidStack);
                final int fillHeight = (int) Math.ceil((float) 50 * fluidStack.getAmount() / (float) TFCConfig.SERVER.barrelCapacity.get());

                RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 8, topPos + 70 - fillHeight, 16, fillHeight, 16, 16);

                resetToBackgroundSprite();
            }
        });

        graphics.blit(texture, getGuiLeft() + 7, getGuiTop() + 19, 176, 0, 18, 52);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        final int relX = mouseX - getGuiLeft();
        final int relY = mouseY - getGuiTop();

        if (relX >= 7 && relY >= 19 && relX < 25 && relY < 71)
        {
            blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
                FluidStack fluid = fluidHandler.getFluidInTank(0);
                if (!fluid.isEmpty())
                {
                    graphics.renderTooltip(font, Tooltips.fluidUnitsOf(fluid), mouseX, mouseY);
                }
            });
        }
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(BarrelBlock.SEALED);
    }

}
