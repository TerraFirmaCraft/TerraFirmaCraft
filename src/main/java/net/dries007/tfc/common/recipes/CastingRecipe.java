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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class CastingRecipe implements INoopInputRecipe, IRecipePredicate<MoldLike>
{
    public static final MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("mold").forGetter(c -> c.ingredient),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(c -> c.fluidIngredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        Codec.FLOAT.optionalFieldOf("break_chance", 1f).forGetter(c -> c.breakChance)
    ).apply(i, CastingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        SizedFluidIngredient.STREAM_CODEC, c -> c.fluidIngredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ByteBufCodecs.FLOAT, c -> c.breakChance,
        CastingRecipe::new
    );

    public static final IndirectHashCollection<Item, CastingRecipe> CACHE = IndirectHashCollection.createForRecipe(r -> RecipeHelpers.itemKeys(r.ingredient), TFCRecipeTypes.CASTING);

    @Nullable
    public static CastingRecipe get(MoldLike mold)
    {
        return RecipeHelpers.getRecipe(CACHE, mold, mold.getContainer().getItem());
    }

    private final Ingredient ingredient;
    private final SizedFluidIngredient fluidIngredient;
    private final ItemStackProvider result;
    private final float breakChance;

    public CastingRecipe(Ingredient ingredient, SizedFluidIngredient fluidIngredient, ItemStackProvider result, float breakChance)
    {
        this.ingredient = ingredient;
        this.fluidIngredient = fluidIngredient;
        this.result = result;
        this.breakChance = breakChance;
    }

    /**
     * @return {@code true} if the recipe matches the input, ignoring temperature. The mold must check if the content is solid.
     */
    @Override
    public boolean matches(MoldLike mold)
    {
        return ingredient.test(mold.getContainer())
            && fluidIngredient.test(mold.getFluidInTank(0));
    }

    /**
     * Assembles the recipe output, and copies over remaining heat from the mold to the output
     */
    public ItemStack assemble(MoldLike mold)
    {
        final ItemStack stack = result.getSingleStack(mold.getContainer().copy());
        final @Nullable IHeat heat = HeatCapability.get(stack);
        if (heat != null)
        {
            heat.setTemperatureIfWarmer(mold.getTemperature());
        }
        return stack;
    }

    public float getBreakChance()
    {
        return breakChance;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public SizedFluidIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.CASTING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.CASTING.get();
    }
}
