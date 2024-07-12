/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.IdentityHashMap;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.Metal;

public class AlloyRecipe implements INoopInputRecipe, IRecipePredicate<Alloy>
{
    public static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.unboundedMap(
            Metal.MANAGER.byIdReferenceCodec(),
            RecordCodecBuilder.<Range>create(j -> j.group(
                Codec.DOUBLE.fieldOf("min").forGetter(c -> c.min),
                Codec.DOUBLE.fieldOf("min").forGetter(c -> c.max)
            ).apply(j, Range::new))
        ).fieldOf("contents").forGetter(c -> c.metals),
        Metal.MANAGER.byIdReferenceCodec().fieldOf("result").forGetter(c -> c.result)
    ).apply(i, AlloyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(
            IdentityHashMap::new,
            Metal.MANAGER.byIdStreamCodec(),
            StreamCodec.<ByteBuf, Range, Double, Double>composite(
                ByteBufCodecs.DOUBLE, c -> c.min,
                ByteBufCodecs.DOUBLE, c -> c.max,
                Range::new
            )
        ), c -> c.metals,
        Metal.MANAGER.byIdStreamCodec(), c -> c.result,
        AlloyRecipe::new
    );

    @Nullable
    public static AlloyRecipe get(RecipeManager recipes, Alloy alloy)
    {
        return RecipeHelpers.unbox(RecipeHelpers.getHolder(recipes, TFCRecipeTypes.ALLOY, alloy));
    }

    private final Map<DataManager.Reference<Metal>, Range> metals;
    private final DataManager.Reference<Metal> result;

    public AlloyRecipe(Map<DataManager.Reference<Metal>, Range> metals, DataManager.Reference<Metal> result)
    {
        this.metals = metals;
        this.result = result;
    }

    @Override
    public boolean matches(Alloy input)
    {
        return input.matches(this);
    }

    public Map<DataManager.Reference<Metal>, Range> getRanges()
    {
        return metals;
    }

    public Metal getResult()
    {
        return result.get();
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

    public record Range(double min, double max)
    {
        public boolean isIn(double value, double epsilon)
        {
            return min - epsilon <= value && value <= max + epsilon;
        }
    }
}
