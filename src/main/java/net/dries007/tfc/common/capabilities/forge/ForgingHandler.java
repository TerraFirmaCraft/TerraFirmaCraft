/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class ForgingHandler implements IForging
{
    private final LazyOptional<IForging> capability = LazyOptional.of(() -> this);

    protected ItemStack container;

    public ForgingHandler(ItemStack container)
    {
        this.container = container;
    }

    public ForgingHandler()
    {
        container = ItemStack.EMPTY;
    }

    public ItemStack getContainer()
    {
        return container;
    }

    @Override
    public int getWork()
    {
        CompoundNBT tag = container.getTag();
        if (tag != null && tag.contains("forging"))
        {
            return tag.getCompound("forging").getInt("work");
        }
        return 0;
    }

    @Override
    public void setWork(int work)
    {
        getTag().putInt("work", work);
        checkEmpty();
    }

    @Nullable
    @Override
    public ResourceLocation getRecipeName()
    {
        CompoundNBT tag = container.getTag();
        if (tag != null && tag.contains("forging") && tag.getCompound("forging").contains("recipe"))
        {
            return new ResourceLocation(tag.getCompound("forging").getString("recipe"));
        }
        return null;
    }

    @Override
    public void setRecipe(@Nullable ResourceLocation recipeName)
    {
        if (recipeName == null)
        {
            getTag().remove("recipe");
            checkEmpty();
        }
        else
        {
            getTag().putString("recipe", recipeName.toString());
        }
    }

    @Override
    public ForgeSteps getSteps()
    {
        CompoundNBT tag = container.getTag();
        if (tag != null && tag.contains("forging"))
        {
            return ForgeSteps.get(tag.getCompound("forging").getCompound("steps"));
        }
        return ForgeSteps.empty();
    }

    @Override
    public void addStep(@Nullable ForgeStep step)
    {
        getTag().put("steps", ForgeSteps.get(getTag().getCompound("steps")).addStep(step).serialize());
        checkEmpty();
    }

    @Override
    public void reset()
    {
        CompoundNBT tag = container.getTag();
        if (tag != null)
        {
            tag.remove("forging");
            // Also, removes nbt data from container item if there's nothing there
            if (container.getTag().isEmpty())
            {
                container.setTag(null);
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ForgingCapability.CAPABILITY.orEmpty(cap, capability);
    }

    /**
     * Initialize tag if needed, returns a tag with forging data
     * Only call this when adding work / forge step
     */
    private CompoundNBT getTag()
    {
        CompoundNBT tag = container.getTag();
        if (tag == null)
        {
            tag = new CompoundNBT();
            container.setTag(tag);
        }
        tag.put("forging", new CompoundNBT());
        tag.getCompound("forging").put("steps", new CompoundNBT());
        return tag.getCompound("forging");
    }

    private void checkEmpty()
    {
        // Checks if the capability is empty and resets the container tag
        CompoundNBT tag = container.getTag();
        if (tag != null && tag.contains("forging"))
        {
            if (getWork() == 0 && !getSteps().hasWork() && getRecipeName() == null)
            {
                reset();
            }
        }
    }
}