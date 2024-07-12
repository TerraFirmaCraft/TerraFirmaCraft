/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Random;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.world.Codecs;

public class HeatingRecipe implements INoopInputRecipe, IRecipePredicate<ItemStack>
{
    public static final IndirectHashCollection<Item, HeatingRecipe> CACHE = IndirectHashCollection.createForRecipe(r -> RecipeHelpers.itemKeys(r.ingredient), TFCRecipeTypes.HEATING);

    public static final MapCodec<HeatingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.optionalFieldOf("result_item", ItemStackProvider.empty()).forGetter(c -> c.outputItem),
        FluidStack.CODEC.optionalFieldOf("result_fluid", FluidStack.EMPTY).forGetter(c -> c.outputFluid),
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temperature),
        Codec.BOOL.optionalFieldOf("use_durability", false).forGetter(c -> c.useDurability),
        Codecs.UNIT_FLOAT.optionalFieldOf("chance", 1f).forGetter(c -> c.chance)
    ).apply(i, HeatingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HeatingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.outputItem,
        FluidStack.STREAM_CODEC, c -> c.outputFluid,
        ByteBufCodecs.FLOAT, c -> c.temperature,
        ByteBufCodecs.BOOL, c -> c.useDurability,
        ByteBufCodecs.FLOAT, c -> c.chance,
        HeatingRecipe::new
    );

    @Nullable
    public static HeatingRecipe getRecipe(ItemStack stack)
    {
        return RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
    }

    private final Ingredient ingredient;
    private final ItemStackProvider outputItem;
    private final FluidStack outputFluid;
    private final float temperature;
    private final boolean useDurability;
    private final float chance;

    public HeatingRecipe(Ingredient ingredient, ItemStackProvider outputItem, FluidStack outputFluid, float temperature, boolean useDurability, float chance)
    {
        this.ingredient = ingredient;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.temperature = temperature;
        this.useDurability = useDurability;
        this.chance = chance;
    }

    /**
     * @return {@code true} if the input matches the recipe
     */
    @Override
    public boolean matches(ItemStack input)
    {
        return ingredient.test(input);
    }

    /**
     * Returns the item component of the recipe output. Note that {@code input} must be a single count stack. Use
     * {@link #assembleStacked(ItemStack, int, float)} for outputs operating on stacked inputs.
     */
    public ItemStack assembleItem(ItemStack input)
    {
        final ItemStack outputStack = outputItem.getSingleStack(input);

        // We always upgrade the heat regardless
        final @Nullable IHeat inputHeat = HeatCapability.get(input);
        if (inputHeat != null)
        {
            HeatCapability.setTemperature(outputStack, inputHeat.getTemperature());
        }

        if (!outputStack.isEmpty() && chance < 1f)
        {
            final Random random = new Random();
            return random.nextFloat() < chance ? outputStack : ItemStack.EMPTY;
        }
        return outputStack;
    }

    /**
     * Returns the fluid component of the recipe output. This can be used for both stacked or non-stacked inputs.
     */
    public FluidStack assembleFluid(ItemStack inputStack)
    {
        final FluidStack outputFluid = this.outputFluid.copy();
        if (useDurability && !outputFluid.isEmpty() && inputStack.getMaxDamage() > 0 && inputStack.isDamageableItem())
        {
            outputFluid.setAmount(Mth.floor(outputFluid.getAmount() * (1 - (float) inputStack.getDamageValue() / inputStack.getMaxDamage())));
        }
        return outputFluid;
    }

    /**
     * A variant of {@link #assembleItem(ItemStack)} which respects a stacked input item.
     * @param stackSizeCap The slot limit of the output container. This method will return no more than this limit.
     * @param chance The chance to use for the recipe. Typically, this is {@code recipe.getChance()}, but for display purposes {@code 1f} can be used
     */
    public ItemStack assembleStacked(ItemStack inputStack, int stackSizeCap, float chance)
    {
        final ItemStack outputStack = outputItem.getSingleStack(inputStack);

        // We always upgrade the heat regardless
        final @Nullable IHeat inputHeat = HeatCapability.get(inputStack);
        if (inputHeat != null)
        {
            HeatCapability.setTemperature(outputStack, inputHeat.getTemperature());
        }

        // Set the stack size to the best possible (output count * input count), then limit to stack size / inventory limit
        outputStack.setCount(Math.min(
            outputStack.getCount() * inputStack.getCount(),
            Math.min(outputStack.getMaxStackSize(), stackSizeCap)
        ));

        // Reduce stack size based on chance output
        if (!outputStack.isEmpty() && chance < 1f)
        {
            final Random random = new Random();
            int count = 0;
            for (int i = 0; i < outputStack.getCount(); i++)
            {
                if (random.nextFloat() < chance)
                    count += 1;
            }
            outputStack.setCount(count);
            if (count == 0)
                return ItemStack.EMPTY;
        }
        return outputStack;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public float getChance()
    {
        return chance;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return outputItem.getEmptyStack();
    }

    public FluidStack getDisplayOutputFluid()
    {
        return outputFluid;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.HEATING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.HEATING.get();
    }
}
