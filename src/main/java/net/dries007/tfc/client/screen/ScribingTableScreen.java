/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import net.dries007.tfc.common.container.ScribingTableContainer;
import net.dries007.tfc.network.ScribingTablePacket;
import net.dries007.tfc.util.Helpers;

public class ScribingTableScreen extends ItemCombinerScreen<ScribingTableContainer>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/scribing_table.png");

    private EditBox name;

    public ScribingTableScreen(ScribingTableContainer container, Inventory playerInv, Component name)
    {
        super(container, playerInv, name, TEXTURE);
        this.titleLabelX = 60;
    }

    @Override
    public void containerTick()
    {
        super.containerTick();
        name.tick();
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
            if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && text.equals(slot.getItem().getHoverName().getString()))
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
        if (menu.getSlot(0).hasItem() && !ScribingTableContainer.isInkInput(menu.getSlot(1).getItem()))
        {
            graphics.blit(TEXTURE, getGuiLeft() + 99, getGuiTop() + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void renderFg(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks)
    {
        name.render(poseStack, mouseX, mouseY, partialTicks);
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
            graphics.blit(TEXTURE, mouseX + 99, mouseY + 45, this.imageWidth, 0, 28, 21);
        }
    }
}
