package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

import static net.dries007.tfc.objects.fluids.FluidsTFC.FRESH_WATER;

public class BarrelRecipeCoolItems extends BarrelRecipe
{
    public BarrelRecipeCoolItems()
    {
        super(IIngredient.of((FluidStack) null), IIngredient.of((ItemStack) null), null, (ItemStack) null, 0);
    }

    @Override
    public boolean isValidInput(FluidStack inputFluid, ItemStack inputStack)
    {
        return IIngredient.of(FRESH_WATER, 1).testIgnoreCount(inputFluid) && inputStack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null) && inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null).getTemperature() > 0;
    }

    @Override
    @Nonnull
    public ItemStack getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        IItemHeat heat = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        heat.setTemperature(heat.getTemperature() - 50);
        return inputStack;
    }

    @Override
    @Nullable
    public FluidStack getOutputFluid(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = inputStack.getCount();
        int retainAmount = inputFluid.amount - multiplier;
        if (retainAmount > 0)
        {
            return new FluidStack(inputFluid.getFluid(), retainAmount);
        }
        return null;
    }

    @Override
    public boolean shouldRepeat()
    {
        return true;
    }

    @Override
    public void onRecipeComplete(World world, BlockPos pos, int tickCounter)
    {
        if (tickCounter % 4 == 0)
            world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
