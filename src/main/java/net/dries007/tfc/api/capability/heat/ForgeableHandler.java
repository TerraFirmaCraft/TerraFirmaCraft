/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.api.capability.IForgeableHandler;
import net.dries007.tfc.objects.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

public class ForgeableHandler extends ItemHeatHandler implements IForgeableHandler
{
    private final ForgeSteps steps;
    private int work;
    private String recipeName;

    public ForgeableHandler(@Nullable NBTTagCompound nbt, float heatCapacity, float meltingPoint)
    {
        super(nbt, heatCapacity, meltingPoint);
        steps = new ForgeSteps();
    }

    public ForgeableHandler()
    {
        // for custom implementations
        steps = new ForgeSteps();
    }

    @Override
    public int getWork()
    {
        return work;
    }

    @Override
    public void setWork(int work)
    {
        this.work = work;
    }

    @Override
    @Nullable
    public String getRecipeName()
    {
        return recipeName;
    }

    @Override
    public void setRecipe(@Nullable AnvilRecipe recipe)
    {
        recipeName = (recipe == null ? null : recipe.getName());
    }

    @Override
    @Nonnull
    public ForgeSteps getSteps()
    {
        return steps;
    }

    @Override
    public void addStep(ForgeStep step)
    {
        steps.addStep(step);
        work += step.getStepAmount();
    }

    @Override
    public void reset()
    {
        steps.reset();
        recipeName = null;
        work = 0;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = super.serializeNBT();

        nbt.setInteger("work", work);
        nbt.setTag("steps", steps.serializeNBT());
        if (recipeName != null)
        {
            nbt.setString("recipe", recipeName);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            work = nbt.getInteger("work");
            recipeName = nbt.hasKey("recipe") ? nbt.getString("recipe") : null; // stops defaulting to empty string
            steps.deserializeNBT(nbt.getCompoundTag("steps"));
        }

        super.deserializeNBT(nbt);
    }
}
