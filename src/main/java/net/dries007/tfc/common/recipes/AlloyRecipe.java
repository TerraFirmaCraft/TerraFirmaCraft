/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.FluidAlloy;

public record AlloyRecipe(
    List<AlloyRange> contents,
    Fluid result
) implements INoopInputRecipe, IRecipePredicate<FluidAlloy>
{
    public static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        AlloyRange.CODEC.listOf().fieldOf("contents").forGetter(c -> c.contents),
        BuiltInRegistries.FLUID.byNameCodec().fieldOf("result").forGetter(c -> c.result)
    ).apply(i, AlloyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
        AlloyRange.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.contents,
        ByteBufCodecs.registry(Registries.FLUID), c -> c.result,
        AlloyRecipe::new
    );

    @Nullable
    public static AlloyRecipe get(RecipeManager recipes, FluidAlloy alloy)
    {
        return RecipeHelpers.unbox(RecipeHelpers.getHolder(recipes, TFCRecipeTypes.ALLOY, alloy));
    }

    @Override
    public boolean matches(FluidAlloy input)
    {
        return input.matches(this);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ALLOY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.ALLOY.get();
    }
}
