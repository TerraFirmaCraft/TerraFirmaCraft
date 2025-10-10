package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.FluidAlloy;

//TODO: look into making the input fluid amounts be valid for the recipe, so that recipe trees that include alloying work
// or implement a range slot (if possible)
public class EmiAlloyingRecipe extends BasicRecipe<AlloyRecipe>
{
    // Make sure this is an even number
    private static final int MAX_HEIGHT = 82;
    // Determines where the input columns start
    private static final int FIRST_COLUMN_X = 4;
    private static final int SECOND_COLUMN_X = 70;
    private static final int SLOT_WIDTH = 18;
    private static final int SLOT_HEIGHT = 18;

    public EmiAlloyingRecipe(ResourceLocation id, AlloyRecipe recipe)
    {
        super(EmiIntegration.ALLOYING, id, recipe, 170, MAX_HEIGHT);

        outputs.add(EmiStack.of(recipe.result()));
        for (AlloyRange range : recipe.contents())
        {
            inputs.add(EmiStack.of(range.fluid()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        // Logic taken from JEI AlloyRecipeCategory
        int fontHeight = Minecraft.getInstance().font.lineHeight;
        int[] positions = getPositions(recipe.contents().size());
        int iteration = 0;
        for (AlloyRange range : recipe.contents())
        {
            int x = (iteration % 2 == 0 ? FIRST_COLUMN_X : SECOND_COLUMN_X) + 1;
            int y = positions[Math.floorDiv(iteration, 2)] + 1;

            int textYOffset = positions[Math.floorDiv(iteration, 2)] + SLOT_HEIGHT / 2 - Math.floorDiv(fontHeight, 2) + 1;
            widgets.addSlot(EmiStack.of(range.fluid()), x, y);
            widgets.addText(formatRange(range), x + SLOT_WIDTH + 2, textYOffset, 0xFFFFFF, false);
            iteration++;
        }
        widgets.addSlot(outputs.getFirst(), 149, MAX_HEIGHT / 2 - SLOT_HEIGHT / 2 + 1).recipeContext(this);

    }

    protected int[] getPositions(int rangesSize)
    {
        int rows = (int) Math.ceil(rangesSize / 2d);
        int spacing = 2;
        int[] positions = new int[rows];
        int totalHeight = SLOT_HEIGHT * rows + spacing * (rows - 1);
        int currentHeight = (MAX_HEIGHT - totalHeight) / 2;
        for (int i = 0; i < rows; i++)
        {
            positions[i] = currentHeight;
            currentHeight += SLOT_HEIGHT + spacing;
        }

        return positions;
    }

    protected Component formatRange(AlloyRange range)
    {
        // Min and max are (roughly) equal, so just so one number to display
        if (Math.abs(range.max() - range.min()) < FluidAlloy.EPSILON)
        {
            return Component.literal(String.format("%.0f%%", range.max() * 100)).withStyle(ChatFormatting.BLACK);
        }
        return Component.literal(String.format("%.0f-%.0f%%", range.min() * 100, range.max() * 100)).withStyle(ChatFormatting.BLACK);
    }
}
