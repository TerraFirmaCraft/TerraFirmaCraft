/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class TagFluidIngredient implements FluidIngredient
{
    private final ITag<Fluid> tag;
    private final int amount;

    private TagFluidIngredient(ITag<Fluid> tag, int amount)
    {
        this.tag = tag;
        this.amount = amount;
    }

    @Override
    public boolean test(FluidStack stack)
    {
        return tag.contains(stack.getFluid()) && amount >= stack.getAmount();
    }

    @Override
    public boolean testIgnoreAmount(Fluid fluid)
    {
        return tag.contains(fluid);
    }

    @Override
    public FluidIngredient.Serializer<?> getSerializer()
    {
        return FluidIngredient.TAG;
    }

    public static class Serializer implements FluidIngredient.Serializer<TagFluidIngredient>
    {
        @Override
        public TagFluidIngredient fromJson(JsonObject json)
        {
            final int amount = JSONUtils.getAsInt(json, "amount", FluidAttributes.BUCKET_VOLUME);
            final String tagName = JSONUtils.getAsString(json, "tag");
            final ITag<Fluid> tag = FluidTags.getAllTags().getTag(new ResourceLocation(tagName));
            if (tag == null)
            {
                throw new JsonParseException("Not a fluid tag: " + tagName);
            }
            return new TagFluidIngredient(tag, amount);
        }

        @Override
        public TagFluidIngredient fromNetwork(PacketBuffer buffer)
        {
            final int amount = buffer.readVarInt();
            ITag<Fluid> tag = FluidTags.getAllTags().getTag(buffer.readResourceLocation());
            return new TagFluidIngredient(Objects.requireNonNull(tag), amount);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, TagFluidIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.amount);
            buffer.writeResourceLocation(Objects.requireNonNull(FluidTags.getAllTags().getId(ingredient.tag)));
        }
    }
}
