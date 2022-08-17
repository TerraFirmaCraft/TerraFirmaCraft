/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.button.AnvilPlanSelectButton;
import net.dries007.tfc.client.screen.button.NextPageButton;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilPlanContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class AnvilPlanScreen extends BlockEntityScreen<AnvilBlockEntity, AnvilPlanContainer>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/anvil_plan.png");

    @Nullable private Button leftButton, rightButton;
    @Nullable private List<AnvilPlanSelectButton> recipeButtons;
    private int maxPageInclusive;
    private int currentPage;

    public AnvilPlanScreen(AnvilPlanContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);

        this.currentPage = 0;
        this.maxPageInclusive = 0;
    }

    @Override
    protected void init()
    {
        super.init();

        final int recipesPerPage = 18;
        final int guiLeft = getGuiLeft(), guiTop = getGuiTop();

        final ItemStack inputStack = blockEntity
            .getCapability(Capabilities.ITEM, null)
            .map(t -> t.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN))
            .orElse(ItemStack.EMPTY);
        final List<AnvilRecipe> recipes = AnvilRecipe.getAll(playerInventory.player.level, inputStack, blockEntity.getTier());

        recipeButtons = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++)
        {
            final int page = i / recipesPerPage;
            final int index = i % recipesPerPage;
            final int posX = 7 + (index % 9) * 18;
            final int posY = 25 + ((index % 18) / 9) * 18;

            final AnvilRecipe recipe = recipes.get(i);
            final AnvilPlanSelectButton button = new AnvilPlanSelectButton(guiLeft + posX, guiTop + posY, page, recipe, RenderHelpers.makeButtonTooltip(this, recipe.getResultItem().getDisplayName()));

            button.setCurrentPage(0);
            recipeButtons.add(button);
            addRenderableWidget(button);
        }

        maxPageInclusive = (recipes.size() - 1) % recipesPerPage;

        addRenderableWidget(leftButton = NextPageButton.left(guiLeft + 7, guiTop + 65, button -> {
            if (currentPage < maxPageInclusive)
            {
                currentPage++;
                updateCurrentPage();
            }
        }));
        addRenderableWidget(rightButton = NextPageButton.right(guiLeft + 7, guiTop + 154, button -> {
            if (currentPage > 0)
            {
                currentPage--;
                updateCurrentPage();
            }
        }));

        updateCurrentPage();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(poseStack);
    }

    private void updateCurrentPage()
    {
        assert recipeButtons != null && leftButton != null && rightButton != null;

        for (AnvilPlanSelectButton button : recipeButtons)
        {
            button.setCurrentPage(currentPage);
        }

        leftButton.active = leftButton.visible = currentPage < maxPageInclusive;
        rightButton.active = rightButton.visible = currentPage > 0;
    }
}
