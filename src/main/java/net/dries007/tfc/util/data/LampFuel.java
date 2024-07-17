/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * @param burnRate The burn rate of this lamp, measured in {@code ticks / mB}
 */
public record LampFuel(
    FluidIngredient fluid,
    BlockIngredient lamps,
    int burnRate
) {
    public static final Codec<LampFuel> CODEC = RecordCodecBuilder.create(i -> i.group(
        FluidIngredient.CODEC.fieldOf("fluid").forGetter(c -> c.fluid),
        BlockIngredient.CODEC.fieldOf("lamps").forGetter(c -> c.lamps),
        Codec.INT.fieldOf("burn_rate").forGetter(c -> c.burnRate)
    ).apply(i, LampFuel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LampFuel> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, c -> c.fluid,
        BlockIngredient.STREAM_CODEC, c -> c.lamps,
        ByteBufCodecs.VAR_INT, c -> c.burnRate,
        LampFuel::new
    );


    public static final DataManager<LampFuel> MANAGER = new DataManager<>(Helpers.identifier("lamp_fuels"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Fluid, LampFuel> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.fluidKeys(r.fluid), MANAGER::getValues);

    @Nullable
    public static LampFuel get(Fluid fluid, BlockState state)
    {
        for (LampFuel fuel : CACHE.getAll(fluid))
        {
            if (fuel.fluid.test(new FluidStack(fluid, 1)) && fuel.lamps.test(state))
            {
                return fuel;
            }
        }
        return null;
    }
}
