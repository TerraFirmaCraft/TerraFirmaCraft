/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class BloomeryRecipe implements INoopInputRecipe
{
    public static final MapCodec<BloomeryRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(c -> c.inputFluid),
        SizedIngredient.FLAT_CODEC.fieldOf("catalyst").forGetter(c -> c.catalyst),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        Codec.INT.fieldOf("duration").forGetter(c -> c.duration)
    ).apply(i, BloomeryRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BloomeryRecipe> STREAM_CODEC = StreamCodec.composite(
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        SizedIngredient.STREAM_CODEC, c -> c.catalyst,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ByteBufCodecs.VAR_INT, c -> c.duration,
        BloomeryRecipe::new
    );

    private final SizedFluidIngredient inputFluid;
    private final SizedIngredient catalyst;
    private final ItemStackProvider result;
    private final int duration;

    public BloomeryRecipe(SizedFluidIngredient inputFluid, SizedIngredient catalyst, ItemStackProvider result, int duration)
    {
        this.inputFluid = inputFluid;
        this.catalyst = catalyst;
        this.result = result;
        this.duration = duration;
    }

    public int getDuration()
    {
        return duration;
    }

    public SizedIngredient getCatalyst()
    {
        return catalyst;
    }

    public SizedFluidIngredient getInputFluid()
    {
        return inputFluid;
    }

    /**
     * @return {@code true} if {@code unsealedStack} could be melted down to form part of the primary (fluid) input to this recipe.
     */
    public boolean matchesInput(ItemStack stack)
    {
        return consumeInput(stack) != null;
    }

    /**
     * @return {@code true} if {@code unsealedStack} could form part of the primary (fluid) input to this recipe.
     */
    public boolean matchesInput(FluidStack stack)
    {
        return inputFluid.ingredient().test(stack);
    }

    /**
     * @return {@code true} if {@code unsealedStack} is the correct catalyst for this recipe.
     */
    public boolean matchesCatalyst(ItemStack stack)
    {
        return catalyst.test(stack);
    }

    /**
     * @return The fluid that would be produced by the primary input {@code unsealedStack}, or {@code null} if {@code unsealedStack} is not a primary input.
     */
    public @Nullable FluidStack consumeInput(ItemStack stack)
    {
        final HeatingRecipe heat = HeatingRecipe.getRecipe(stack);
        if (heat != null)
        {
            // Don't test amount here, because we just want to know if this unsealedStack melts into the correct metal - not how much
            final FluidStack fluid = heat.assembleFluid(stack);
            if (matchesInput(fluid))
            {
                return fluid;
            }
        }
        return null;
    }

    public ItemStack assembleOutput()
    {
        return result.getEmptyStack();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.BLOOMERY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLOOMERY.get();
    }
}
