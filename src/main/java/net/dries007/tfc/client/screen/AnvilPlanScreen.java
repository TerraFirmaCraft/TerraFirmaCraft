/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.screen.button.AnvilPlanSelectButton;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilPlanContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.util.Helpers;

public class AnvilPlanScreen extends BlockEntityScreen<AnvilBlockEntity, AnvilPlanContainer>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/anvil_plan.png");

    //private final Button leftButton, rightButton;

    private final int maxPage;
    private int currentPage;

    public AnvilPlanScreen(AnvilPlanContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);

        final ItemStack inputStack = container.getBlockEntity()
            .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            .map(t -> t.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN))
            .orElse(ItemStack.EMPTY);
        final List<AnvilRecipe> recipes = AnvilRecipe.getAll(playerInventory.player.level, inputStack, container.getBlockEntity().getTier());

        for (int i = 0; i < recipes.size(); i++)
        {
            final int page = i % 18;
            final int posX = 7 + (i % 9) * 18;
            final int posY = 25 + ((i % 18) / 9) * 18;

            final AnvilPlanSelectButton button = new AnvilPlanSelectButton(getGuiLeft() + posX, getGuiTop() + posY, page, recipes.get(i));

            button.setCurrentPage(0);
        }

        //this.leftButton = new Button(getGuiLeft() + 7, getGuiTop() + 65, )

        this.currentPage = 0;
        this.maxPage = 0;
    }

    @Override
    protected void init()
    {
        super.init();

        final int guiLeft = getGuiLeft(), guiTop = getGuiTop();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(poseStack);
    }

    private void updateCurrentPage()
    {

    }
}
