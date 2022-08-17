/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class BarrelScreen extends BlockEntityScreen<BarrelBlockEntity, BarrelContainer>
{
    private static final Component SEAL = Helpers.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = Helpers.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");
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
        addRenderableWidget(new BarrelSealButton(blockEntity, getGuiLeft(), getGuiTop(), new Button.OnTooltip()
        {
            @Override
            public void onTooltip(Button button, PoseStack poseStack, int x, int y)
            {
                renderTooltip(poseStack, isSealed() ? UNSEAL : SEAL, x, y);
            }

            @Override
            public void narrateTooltip(Consumer<Component> consumer)
            {
                consumer.accept(isSealed() ? UNSEAL : SEAL);
            }
        }));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (isSealed())
        {
            drawDisabled(poseStack, BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, BarrelBlockEntity.SLOT_ITEM);

            // Draw the text displaying both the seal date, and the recipe name
            BarrelRecipe recipe = blockEntity.getRecipe();
            if (recipe != null)
            {
                FormattedText resultText = recipe.getTranslationComponent();
                if (font.width(resultText) > MAX_RECIPE_NAME_LENGTH)
                {
                    int line = 0;
                    for (FormattedCharSequence text : font.split(resultText, MAX_RECIPE_NAME_LENGTH))
                    {
                        font.draw(poseStack, text, 70 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(text), 2), titleLabelY + (line * font.lineHeight), 0x404040);
                        line++;
                    }
                }
                else
                {
                    font.draw(poseStack, resultText.getString(), 70 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(resultText), 2), 61, 0x404040);
                }
            }
            String date = ICalendar.getTimeAndDate(Calendars.CLIENT.ticksToCalendarTicks(blockEntity.getSealedTick()), Calendars.CLIENT.getCalendarDaysInMonth()).getString();
            font.draw(poseStack, date, imageWidth / 2f - font.width(date) / 2f, 74, 0x404040);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
        blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (!fluidStack.isEmpty())
            {
                final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluidStack);
                final int startY = 20;
                final int endY = 70;
                final int fillHeight = (int) Math.ceil((float) (endY - startY) * fluidStack.getAmount() / (float) TFCConfig.SERVER.barrelCapacity.get());

                RenderHelpers.fillAreaWithSprite(leftPos, topPos, sprite, poseStack, 8, 24, endY, fillHeight);

                resetToBackgroundSprite();
            }
        });

        blit(poseStack, getGuiLeft() + 7, getGuiTop() + 19, 176, 0, 18, 52);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderTooltip(poseStack, mouseX, mouseY);
        final int relX = mouseX - getGuiLeft();
        final int relY = mouseY - getGuiTop();

        if (relX >= 7 && relY >= 19 && relX < 25 && relY < 71)
        {
            blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
                FluidStack fluid = fluidHandler.getFluidInTank(0);
                if (!fluid.isEmpty())
                {
                    Component units = Helpers.translatable("tfc.tooltip.fluid_units_of", fluid.getAmount()).append(fluid.getDisplayName());
                    renderTooltip(poseStack, units, mouseX, mouseY);
                }
            });
        }
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(BarrelBlock.SEALED);
    }

}
