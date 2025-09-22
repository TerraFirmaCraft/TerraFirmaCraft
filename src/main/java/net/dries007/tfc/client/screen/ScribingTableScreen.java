/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.List;
import java.util.stream.Stream;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.ScribingTableContainer;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.network.ScribingTablePacket;
import net.dries007.tfc.util.Helpers;

public class ScribingTableScreen extends ItemCombinerScreen<ScribingTableContainer>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/scribing_table.png");
    // Time in ticks
    private static final float ITEM_ROTATE_TIME = 50f;

    private EditBox name;
    private List<Item> valid;
    private int currentIndex = 0;
    private float currentTime = 0f;

    public ScribingTableScreen(ScribingTableContainer container, Inventory playerInv, Component name)
    {
        super(container, playerInv, name, TEXTURE);
        this.titleLabelX = 60;
        valid = List.of();
    }

    @Override
    protected void subInit()
    {
        name = new EditBox(font, leftPos + 62, topPos + 24, 103, 12, Component.translatable("container.repair"));
        name.setCanLoseFocus(false);
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setResponder(this::onNameChanged);
        name.setValue("");
        addWidget(name);
        setInitialFocus(name);
        name.setEditable(false);
        // Should this be done here?
        valid = Stream.concat(Helpers.allItems(TFCTags.Items.SCRIBING_INK), Helpers.allFluids(TFCTags.Fluids.USABLE_IN_SCRIBING_TABLE).map(Fluid::getBucket)).toList();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        String text = name.getValue();
        init(minecraft, width, height);
        name.setValue(text);
        if (menu.getSlot(0).hasItem())
        {
            setFocused(name);
            name.setEditable(true);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            minecraft.player.closeContainer();
        }
        return name.keyPressed(keyCode, scanCode, modifiers) || name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onNameChanged(String text)
    {
        if (!text.isEmpty())
        {
            Slot slot = menu.getSlot(AnvilMenu.INPUT_SLOT);
            if (slot.hasItem() && !slot.getItem().has(DataComponents.CUSTOM_NAME) && text.equals(slot.getItem().getHoverName().getString()))
            {
                text = "";
            }

            menu.setItemName(text);
            PacketDistributor.sendToServer(new ScribingTablePacket(text));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        RenderSystem.disableBlend();
        super.renderLabels(graphics, mouseX, mouseY);
        if (menu.getSlot(0).hasItem())
        {
            Component component = null;
            if (!menu.getSlot(1).hasItem())
            {
                component = Component.translatable("tfc.tooltip.scribing_table.missing_ink");
            }
            else if (!ScribingTableContainer.isInkInput(menu.getSlot(1).getItem()))
            {
                component = Component.translatable("tfc.tooltip.scribing_table.invalid_ink");
            }
            if (component != null)
            {
                int k = this.imageWidth - 8 - this.font.width(component) - 2;
                graphics.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                graphics.drawString(font, component, k, 69, 16736352, false);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        this.currentTime += partialTicks;
        if (this.currentTime > ITEM_ROTATE_TIME)
        {
            this.currentTime = this.currentTime % ITEM_ROTATE_TIME;
            this.currentIndex = (this.currentIndex + 1) % valid.size();
        }

        Slot itemSlot = menu.getSlot(0);
        Slot inkSlot = menu.getSlot(1);
        if (itemSlot.hasItem())
        {
            //graphics.blitSprite(TEXT_FIELD_SPRITE, this.leftPos + 59, this.topPos + 20, 110, 16);
            graphics.blit(TEXTURE, this.leftPos + 59, this.topPos + 20, 0, 166, 110, 16);
            if (!ScribingTableContainer.isInkInput(inkSlot.getItem()))
            {
                renderErrorIcon(graphics, mouseX, mouseY);
            }
        }
        else
        {
            graphics.blit(TEXTURE, this.leftPos + 59, this.topPos + 20, 0, 182, 110, 16);
        }

        if (!inkSlot.hasItem())
        {
            graphics.setColor(1f, 1f, 1f, 0.25f);
            graphics.renderItem(valid.get(this.currentIndex).getDefaultInstance(), this.leftPos + 76, this.topPos + 47);
            graphics.setColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        name.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void slotChanged(AbstractContainerMenu menu, int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            name.setValue(stack.isEmpty() ? "" : stack.getHoverName().getString());
            name.setEditable(!stack.isEmpty());
            setFocused(name);
        }
    }

    @Override
    protected void renderErrorIcon(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem())
        {
            // copied from anvil... we may not have the texture?
            graphics.blit(TEXTURE, getGuiLeft() + 99, getGuiTop() + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        super.renderTooltip(graphics, x, y);
        if (hoveredSlot != null && hoveredSlot.index == 1 && !hoveredSlot.hasItem())
        {
            ItemStack hintItem = valid.get(this.currentIndex).getDefaultInstance();
            graphics.renderTooltip(this.font, hintItem.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL), hintItem.getTooltipImage(), hintItem, x, y);
        }
    }
}
