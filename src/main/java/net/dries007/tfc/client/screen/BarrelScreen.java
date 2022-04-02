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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.BarrelSealButton;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class BarrelScreen extends BlockEntityScreen<BarrelBlockEntity, BarrelContainer>
{
    private static final Component SEAL = new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");

    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/barrel.png");

    public BarrelScreen(BarrelContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY -= 1;
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new BarrelSealButton(blockEntity, getGuiLeft(), getGuiTop(), new Button.OnTooltip() {
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
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
                // draw disabled texture over the slots
                menu.slots.stream().filter(slot -> slot.index <= BarrelBlockEntity.SLOT_ITEM).forEach(slot -> fillGradient(poseStack, slot.x, slot.y, slot.x + 16, slot.y + 16, 0x75FFFFFF, 0x75FFFFFF));
            });

            // Draw the text displaying both the seal date, and the recipe name
            boolean isLong = false;
            BarrelRecipe recipe = blockEntity.getOrUpdateRecipe();
            if (recipe != null)
            {
                String resultName = recipe.getTranslationComponent().getString();
                int recipeWidth = font.width(resultName);
                if (recipeWidth > 80)
                {
                    isLong = true;
                }
                font.draw(poseStack, resultName, isLong ? recipeWidth / 2f - 42 : 28, isLong ? 73 : 61, 0x404040);
            }
            String date = ICalendar.getTimeAndDate(blockEntity.getSealedTick(), Calendars.CLIENT.getCalendarDaysInMonth()).getString();
            font.draw(poseStack, date, isLong ? 58 : font.width(date) / 2f, isLong ? 19 : 73, 0x404040);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
        // todo: special jei button?
        /*if (Helpers.isJEIEnabled())
        {
            drawTexturedModalRect(guiLeft + 92, guiTop + 21, 227, 0, 9, 14);
        }*/
        blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (!fluidStack.isEmpty())
            {
                final TextureAtlasSprite sprite = getAndBindFluidSprite(fluidStack);
                final int startY = 20;
                final int endY = 69;
                final int fillHeight = (int) Math.ceil((float) (endY - startY) * fluidStack.getAmount() / (float) TFCConfig.SERVER.barrelCapacity.get());

                fillAreaWithSprite(sprite, poseStack, 8, 23, endY, fillHeight);

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
            blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                FluidStack fluid = fluidHandler.getFluidInTank(0);
                if (!fluid.isEmpty())
                {
                    Component units = new TranslatableComponent("tfc.tooltip.fluid_units_of", fluid.getAmount()).append(fluid.getDisplayName());
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