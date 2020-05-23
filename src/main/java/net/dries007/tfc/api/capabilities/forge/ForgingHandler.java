/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.forge;

import javax.annotation.Nonnull;
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
        }
        else
        {
            getTag().putString("recipe", recipeName.toString());
        }
    }

    @Override
    @Nonnull
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
        getTag().put("steps", ForgeSteps.get(getTag().getCompound("steps")).serialize());
    }

    @Override
    public void reset()
    {
        CompoundNBT tag = container.getTag();
        if (tag != null)
        {
            tag.remove("forging");
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        return CapabilityForging.CAPABILITY.orEmpty(cap, capability);
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
}
