/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.forge;

import java.util.LinkedList;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;

public class ForgeSteps
{
    public static ForgeSteps get(CompoundNBT tag)
    {
        if (tag.isEmpty())
        {
            return empty();
        }
        else
        {
            return new ForgeSteps(tag);
        }
    }

    public static ForgeSteps empty()
    {
        return new ForgeSteps();
    }

    private final LinkedList<ForgeStep> steps;

    private ForgeSteps(CompoundNBT nbt)
    {
        steps = new LinkedList<>();
        deserialize(nbt);
    }

    private ForgeSteps()
    {
        steps = new LinkedList<>();
        reset();
    }

    public void reset()
    {
        for (int i = 0; i < 3; i++) addStep(null);
    }

    public ForgeSteps addStep(@Nullable ForgeStep step)
    {
        steps.add(step);
        while (steps.size() > 3)
        {
            steps.remove();
        }
        return this;
    }

    public CompoundNBT serialize()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("last", getStepInt(0));
        nbt.putInt("second", getStepInt(1));
        nbt.putInt("third", getStepInt(2));
        return nbt;
    }

    public void deserialize(CompoundNBT nbt)
    {
        addStep(ForgeStep.valueOf(nbt.getInt("last")));
        addStep(ForgeStep.valueOf(nbt.getInt("second")));
        addStep(ForgeStep.valueOf(nbt.getInt("third")));
    }

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
