/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.forge;

import java.util.LinkedList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

@ParametersAreNonnullByDefault
public class ForgeSteps implements INBTSerializable<NBTTagCompound>
{
    private final LinkedList<ForgeStep> steps;

    public ForgeSteps()
    {
        steps = new LinkedList<>();
        reset();
    }

    public void reset()
    {
        for (int i = 0; i < 3; i++) addStep(null);
    }

    public void addStep(@Nullable ForgeStep step)
    {
        steps.add(step);
        while (steps.size() > 3)
        {
            steps.remove();
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("last", getStepInt(0));
        nbt.setInteger("second", getStepInt(1));
        nbt.setInteger("third", getStepInt(2));
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            addStep(ForgeStep.valueOf(nbt.getInteger("last")));
            addStep(ForgeStep.valueOf(nbt.getInteger("second")));
            addStep(ForgeStep.valueOf(nbt.getInteger("third")));
        }
    }

    @Nonnull
    public ForgeSteps copy()
    {
        ForgeSteps newSteps = new ForgeSteps();
        for (ForgeStep step : this.steps)
            newSteps.addStep(step);
        return newSteps;
    }

    @Nullable
    public ForgeStep getStep(int idx)
    {
        return steps.get(idx);
    }

    @Override
    public String toString()
    {
        return "[" + getStep(0) + ", " + getStep(1) + ", " + getStep(2) + "]";
    }

    /**
     * Checks if this is fresh new (no forging has been done yet)
     *
     * @return true if has been worked at least once, false otherwise
     */
    public boolean hasWork()
    {
        for (ForgeStep step : steps)
        {
            if (step != null)
            {
                return true;
            }
        }
        return false;
    }

    private int getStepInt(int idx)
    {
        ForgeStep step = steps.get(idx);
        return step == null ? -1 : step.ordinal();
    }

    private void setStepInt(int position, int step)
    {
        steps.set(position, ForgeStep.valueOf(step));
    }
}
