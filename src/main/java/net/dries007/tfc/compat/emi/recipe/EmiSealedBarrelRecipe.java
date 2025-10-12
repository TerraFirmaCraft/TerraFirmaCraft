package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiSealedBarrelRecipe extends BasicRecipe<SealedBarrelRecipe>
{
    private static final int SEED_UNIQUE = 16777216;
    private @Nullable ItemStackProvider outputProvider;
    private @Nullable ItemStackProvider onSeal;
    private @Nullable ItemStackProvider onUnseal;

    public EmiSealedBarrelRecipe(ResourceLocation id, SealedBarrelRecipe recipe)
    {
        super(EmiIntegration.BARREL, id, recipe, 170, 60);
        inputs.add(new EmiSizedIngredient(recipe.getInputItem()));
        inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));
        onSeal = recipe.onSeal();
        onUnseal = recipe.onUnseal();

        ItemStackProvider output = recipe.getOutputItem();
        if (output.dependsOnInput())
        {
            outputProvider = output;
        }
        else
        {
            ItemStack stack = output.stack();
            if (!stack.isEmpty())
            {
                outputs.add(EmiStack.of(stack));
            }
        }

        FluidStack fluid = recipe.getOutputFluid();
        if (!fluid.isEmpty())
        {
            outputs.add(EmiStack.of(fluid.getFluid(), fluid.getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addText(Calendars.CLIENT.getTimeDelta(recipe.getDuration()), getDisplayWidth() / 2, getDisplayHeight() - 6, 0xffffffff, true).verticalAlign(TextWidget.Alignment.END).horizontalAlign(TextWidget.Alignment.CENTER);
        int x = 6;
        SlotWidget itemInputSlot;
        if (inputs.getFirst().getEmiStacks().size() > 1 && (onSeal != null || outputProvider != null))
        {
            itemInputSlot = widgets.addGeneratedSlot(r -> {
                List<EmiStack> stacks = inputs.getFirst().getEmiStacks();
                return stacks.get(r.nextInt(stacks.size()));
            }, SEED_UNIQUE, x, 6);
        }
        else
        {
            itemInputSlot = widgets.addSlot(inputs.getFirst(), x, 6);
        }
        x = itemInputSlot.getBounds().right() + 3;
        x = widgets.addSlot(inputs.get(1), x, 6).getBounds().right() + 3;
        x = widgets.addFillingArrow(x, 6, 3000).getBounds().right() + 3;
        SlotWidget sealSlot = null;
        if (onSeal != null)
        {
            sealSlot = new ItemStackProviderWidget(itemInputSlot, onSeal, SEED_UNIQUE, x, 6);
            x = widgets.add(sealSlot).recipeContext(this).getBounds().right() + 3;
            x = widgets.addFillingArrow(x, 6, 3000).getBounds().right() + 3;
        }
        if (outputProvider != null)
        {
            x = widgets.add(new ItemStackProviderWidget(itemInputSlot, outputProvider, SEED_UNIQUE, x, 6)).recipeContext(this).getBounds().right() + 3;
        }
        if (onUnseal != null)
        {
            x = widgets.add(new ItemStackProviderWidget(sealSlot != null ? sealSlot : itemInputSlot, onUnseal, SEED_UNIQUE, x, 6)).recipeContext(this).getBounds().right() + 3;
        }


        for (EmiStack out : outputs)
        {
            x = widgets.addSlot(out, x, 6).recipeContext(this).getBounds().right() + 3;
        }

    }

    @Override
    public boolean supportsRecipeTree()
    {
        return outputProvider == null && super.supportsRecipeTree();
    }


}
