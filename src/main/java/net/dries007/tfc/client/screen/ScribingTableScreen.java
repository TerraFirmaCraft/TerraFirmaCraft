/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.container.ScribingTableContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScribingTablePacket;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ScribingTableScreen extends ItemCombinerScreen<ScribingTableContainer>
{
    private EditBox name;

    public ScribingTableScreen(ScribingTableContainer container, Inventory playerInv, Component name)
    {
        super(container, playerInv, name, new ResourceLocation(MOD_ID, "textures/gui/scribing_table.png"));
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
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        name = new EditBox(font, leftPos + 62, topPos + 24, 103, 12, Helpers.translatable("container.repair"));
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
    public void removed()
    {
        super.removed();
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
            Slot slot = menu.getSlot(ItemCombinerMenu.INPUT_SLOT);
            if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && text.equals(slot.getItem().getHoverName().getString()))
            {
                text = "";
            }

            menu.setItemName(text);
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScribingTablePacket(text));
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        RenderSystem.disableBlend();
        super.renderLabels(poseStack, mouseX, mouseY);
        if (menu.getSlot(0).hasItem())
        {
            Component component = null;
            if (!menu.getSlot(1).hasItem())
            {
                component = Helpers.translatable("tfc.tooltip.scribing_table.missingink");
            }
            else if (!ScribingTableContainer.isInkInput(menu.getSlot(1).getItem()))
            {
                component = Helpers.translatable("tfc.tooltip.scribing_table.invalidink");
            }
            if (component != null)
            {
                int k = this.imageWidth - 8 - this.font.width(component) - 2;
                fill(poseStack, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                this.font.drawShadow(poseStack, component, (float) k, 69.0F, 16736352);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
        if (menu.getSlot(0).hasItem() && !ScribingTableContainer.isInkInput(menu.getSlot(1).getItem()))
        {
            this.blit(poseStack, getGuiLeft() + 99, getGuiTop() + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void renderFg(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
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
}
