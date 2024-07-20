/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.Helpers;

/**
 * Recipe type for all cooking pot recipes
 */
public class PotRecipe implements ISimpleRecipe<PotBlockEntity.PotInventory>
{
    public static final MapCodec<PotRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.listOf(0, 5).fieldOf("ingredients").forGetter(c -> c.itemIngredients),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid_ingredient").forGetter(c -> c.fluidIngredient),
        Codec.INT.fieldOf("duration").forGetter(c -> c.duration),
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temperature)
    ).apply(i, PotRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(5)), c -> c.itemIngredients,
        SizedFluidIngredient.STREAM_CODEC, c -> c.fluidIngredient,
        ByteBufCodecs.VAR_INT, c -> c.duration,
        ByteBufCodecs.FLOAT, c -> c.temperature,
        PotRecipe::new
    );

    protected final List<Ingredient> itemIngredients;
    protected final SizedFluidIngredient fluidIngredient;
    protected final int duration;
    protected final float temperature;

    protected PotRecipe(PotRecipe base)
    {
        this(base.itemIngredients, base.fluidIngredient, base.duration, base.temperature);
    }

    public PotRecipe(List<Ingredient> itemIngredients, SizedFluidIngredient fluidIngredient, int duration, float temperature)
    {
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.duration = duration;
        this.temperature = temperature;
    }

    @Override
    public boolean matches(PotBlockEntity.PotInventory inventory, Level worldIn)
    {
        if (!fluidIngredient.test(inventory.getFluidInTank(0)))
        {
            return false;
        }
        final List<ItemStack> stacks = new ArrayList<>();
        for (int i = PotBlockEntity.SLOT_EXTRA_INPUT_START; i <= PotBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stacks.add(stack);
            }
        }
        return (stacks.isEmpty() && itemIngredients.isEmpty()) || Helpers.perfectMatchExists(stacks, itemIngredients);
    }

    @Override
    public ItemStack assemble(PotBlockEntity.PotInventory input, HolderLookup.Provider registries)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.POT.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        throw new UnsupportedOperationException();
    }

    public SizedFluidIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    public List<Ingredient> getItemIngredients()
    {
        return itemIngredients;
    }

    /**
     * @return true if the temperature is hot enough to boil
     */
    public boolean isHotEnough(float tempIn)
    {
        return tempIn > temperature;
    }

    /**
     * @return The number of ticks needed to boil for.
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * @return The output of the pot recipe.
     */
    public PotOutput getOutput(PotBlockEntity.PotInventory inventory)
    {
        return PotOutput.EMPTY_INSTANCE;
    }
}
