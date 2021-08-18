/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class TagFluidIngredient implements FluidIngredient
{
    private final Tag<Fluid> tag;
    private final int amount;

    private TagFluidIngredient(Tag<Fluid> tag, int amount)
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
            final int amount = GsonHelper.getAsInt(json, "amount", FluidAttributes.BUCKET_VOLUME);
            final String tagName = GsonHelper.getAsString(json, "tag");
            final Tag<Fluid> tag = FluidTags.getAllTags().getTag(new ResourceLocation(tagName));
            if (tag == null)
            {
                throw new JsonParseException("Not a fluid tag: " + tagName);
            }
            return new TagFluidIngredient(tag, amount);
        }

        @Override
        public TagFluidIngredient fromNetwork(FriendlyByteBuf buffer)
        {
            final int amount = buffer.readVarInt();
            Tag<Fluid> tag = FluidTags.getAllTags().getTag(buffer.readResourceLocation());
            return new TagFluidIngredient(Objects.requireNonNull(tag), amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TagFluidIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.amount);
            buffer.writeResourceLocation(Objects.requireNonNull(FluidTags.getAllTags().getId(ingredient.tag)));
        }
    }
}
