package net.dries007.tfc.compat.emi.recipe;

import java.util.Arrays;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBlastFurnaceRecipe extends GenericRecipe<BlastFurnaceRecipe>
{
    public EmiBlastFurnaceRecipe(ResourceLocation id, BlastFurnaceRecipe recipe)
    {
        super(EmiIntegration.BLAST_FURNACE, id, recipe, 50, 30);
        SizedFluidIngredient fluid = recipe.inputFluid();
        inputs.add(EmiIngredient.of(Arrays.stream(fluid.getFluids()).map(s -> EmiStack.of(s.getFluid())).toList(), fluid.amount()));
        outputs.add(EmiStack.of(recipe.outputFluid().getFluid(), recipe.outputFluid().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 0, 0);
        widgets.addSlot(outputs.getFirst(), 20, 0).recipeContext(this);
    }
}
