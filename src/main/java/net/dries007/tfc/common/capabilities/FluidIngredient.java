/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.Objects;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class FluidIngredient implements Predicate<FluidStack>
{
    public static FluidStack fluidStackFromJson(JsonObject json)
    {
        int amount = GsonHelper.getAsInt(json, "amount", -1);
        String fluidName = GsonHelper.getAsString(json, "fluid");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
        if (fluid == null)
        {
            throw new JsonParseException("Not a fluid: " + fluidName);
        }
        return new FluidStack(fluid, amount);
    }

    public static FluidIngredient fromJson(JsonObject json)
    {
        int amount = GsonHelper.getAsInt(json, "amount", -1);
        if (json.has("tag") && json.has("fluid"))
        {
            throw new JsonParseException("Fluid ingredient cannot have both 'tag' and 'fluid' entries");
        }
        else if (json.has("fluid"))
        {
            String fluidName = GsonHelper.getAsString(json, "fluid");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (fluid == null)
            {
                throw new JsonParseException("Not a fluid: " + fluidName);
            }
            return new SimpleIngredient(fluid, amount);
        }
        else if (json.has("tag"))
        {
            String tagName = GsonHelper.getAsString(json, "tag");
            Tag<Fluid> tag = FluidTags.getAllTags().getTag(new ResourceLocation(tagName));
            if (tag == null)
            {
                throw new JsonParseException("Not a fluid tag: " + tagName);
            }
            return new TagIngredient(tag, amount);
        }
        else
        {
            throw new JsonParseException("Fluid ingredient must have either 'tag' or 'fluid' entries");
        }
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        int amount = buffer.readVarInt();
        switch (buffer.readByte())
        {
            case 0:
            {
                Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
                return new SimpleIngredient(fluid, amount);
            }
            case 1:
            {
                Tag<Fluid> tag = FluidTags.getAllTags().getTag(buffer.readResourceLocation());
                return new TagIngredient(Objects.requireNonNull(tag), amount);
            }
            default:
                return new SimpleIngredient(Fluids.EMPTY, 0);
        }
    }

    public static void toNetwork(FluidIngredient ingredient, FriendlyByteBuf buffer)
    {
        buffer.writeInt(ingredient.amount);
        ingredient.toNetwork(buffer);
    }

    protected final int amount;

    protected FluidIngredient(int amount)
    {
        this.amount = amount;
    }

    public int getAmount()
    {
        return amount;
    }

    /**
     * Tests if the ingredient matches the provided stack, including count.
     */
    @Override
    public boolean test(FluidStack stack)
    {
        return (amount < 0 || stack.getAmount() >= amount) && testIgnoreCount(stack);
    }

    public abstract boolean testIgnoreCount(FluidStack stack);

    protected abstract void toNetwork(FriendlyByteBuf buffer);

    static class SimpleIngredient extends FluidIngredient
    {
        private final Fluid fluid;

        SimpleIngredient(Fluid fluid, int amount)
        {
            super(amount);
            this.fluid = fluid;
        }

        @Override
        public boolean testIgnoreCount(FluidStack stack)
        {
            return stack.getFluid() == fluid;
        }

        @Override
        protected void toNetwork(FriendlyByteBuf buffer)
        {
            buffer.writeByte(0);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
        }
    }

    static class TagIngredient extends FluidIngredient
    {
        private final Tag<Fluid> tag;

        TagIngredient(Tag<Fluid> tag, int amount)
        {
            super(amount);
            this.tag = tag;
        }

        @Override
        public boolean testIgnoreCount(FluidStack stack)
        {
            return tag.contains(stack.getFluid());
        }

        @Override
        protected void toNetwork(FriendlyByteBuf buffer)
        {
            buffer.writeByte(1);
            buffer.writeResourceLocation(Objects.requireNonNull(FluidTags.getAllTags().getId(tag)));
        }
    }
}
