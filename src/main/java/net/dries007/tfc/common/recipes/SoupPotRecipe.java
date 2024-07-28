/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.IngredientsComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;
import net.dries007.tfc.util.Helpers;

public class SoupPotRecipe extends PotRecipe
{
    public static final MapCodec<SoupPotRecipe> CODEC = PotRecipe.CODEC.xmap(SoupPotRecipe::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, SoupPotRecipe> STREAM_CODEC = PotRecipe.STREAM_CODEC.map(SoupPotRecipe::new, Function.identity());

    public static final PotOutput.OutputType OUTPUT_TYPE = (provider, nbt) -> {
        ItemStack stack = ItemStack.parseOptional(provider, nbt.getCompound("item"));
        return new SoupOutput(stack);
    };

    public static final int SOUP_HUNGER_VALUE = 4;
    public static final float SOUP_DECAY_MODIFIER = 3.5F;

    public SoupPotRecipe(PotRecipe base)
    {
        super(base);
    }

    @Override
    public PotOutput getOutput(PotBlockEntity.PotInventory inventory)
    {
        int ingredientCount = 0;
        float water = 20, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        ItemStack soupStack = ItemStack.EMPTY;
        final List<ItemStack> itemIngredients = new ArrayList<>();
        for (int i = PotBlockEntity.SLOT_EXTRA_INPUT_START; i <= PotBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            final ItemStack stack = inventory.getStackInSlot(i);
            final @Nullable IFood food = FoodCapability.get(stack);
            if (food != null)
            {
                itemIngredients.add(stack);
                if (food.isRotten()) // this should mostly not happen since the ingredients are not rotten to start, but worth checking
                {
                    ingredientCount = 0;
                    break;
                }
                final FoodData data = food.getData();
                water += data.water();
                saturation += data.saturation();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient);
                }
                ingredientCount++;
            }
        }
        if (ingredientCount > 0)
        {
            float multiplier = 1 - (0.05f * ingredientCount); // per-serving multiplier of nutrition
            water *= multiplier; saturation *= multiplier;
            Nutrient maxNutrient = Nutrient.GRAIN; // determines what item you get. this is a default
            float maxNutrientValue = 0;
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int idx = nutrient.ordinal();
                nutrition[idx] *= multiplier;
                if (nutrition[idx] > maxNutrientValue)
                {
                    maxNutrientValue = nutrition[idx];
                    maxNutrient = nutrient;
                }
            }

            soupStack = new ItemStack(TFCItems.SOUPS.get(maxNutrient).get(), (int) (ingredientCount / 2f) + 1);
            soupStack.set(TFCComponents.INGREDIENTS, IngredientsComponent.of(itemIngredients));
            FoodCapability.setFoodForDynamicItemOnCreate(
                soupStack,
                new FoodData(SOUP_HUNGER_VALUE, water, saturation, 0, nutrition, SOUP_DECAY_MODIFIER));
        }

        return new SoupOutput(soupStack);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SOUP.get();
    }

    public record SoupOutput(ItemStack stack) implements PotOutput
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public ItemInteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (Helpers.isItem(clickedWith.getItem(), TFCTags.Items.SOUP_BOWLS) && !stack.isEmpty())
            {
                // set the internal bowl to the one we clicked with
                stack.set(TFCComponents.BOWL, Bowl.of(clickedWith));

                // take the player's bowl, give a soup
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.split(1));
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public int getFluidColor()
        {
            return TFCFluids.ALPHA_MASK | 0xA64214;
        }

        @Override
        public void write(HolderLookup.Provider provider, CompoundTag nbt)
        {
            nbt.put("item", stack.save(provider));
        }

        @Override
        public OutputType getType()
        {
            return SoupPotRecipe.OUTPUT_TYPE;
        }

        @Override
        public BlockEntityTooltip getTooltip()
        {
            return ((level, state, pos, entity, tooltip) -> {
                BlockEntityTooltips.itemWithCount(tooltip, stack);
                FoodCapability.addTooltipInfo(stack, tooltip);
            });
        }
    }
}
