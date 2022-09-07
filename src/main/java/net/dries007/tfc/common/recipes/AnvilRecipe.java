/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class AnvilRecipe implements ISimpleRecipe<AnvilRecipe.Inventory>
{
    public static boolean hasAny(Level level, Inventory inventory)
    {
        return hasAny(level, inventory.getItem(), inventory.getTier());
    }

    public static boolean hasAny(Level level, ItemStack stack, int tier)
    {
        return Helpers.getRecipes(level, TFCRecipeTypes.ANVIL)
            .values()
            .stream()
            .anyMatch(r -> r.input.test(stack) && tier >= r.minTier); // anyMatch() should be faster than calling toList().isEmpty()
    }

    public static List<AnvilRecipe> getAll(Level level, Inventory inventory)
    {
        return getAll(level, inventory.getItem(), inventory.getTier());
    }

    public static List<AnvilRecipe> getAll(Level level, ItemStack stack, int tier)
    {
        return Helpers.getRecipes(level, TFCRecipeTypes.ANVIL)
            .values()
            .stream()
            .filter(r -> r.input.test(stack) && tier >= r.minTier)
            .toList();
    }

    private final ResourceLocation id;
    private final Ingredient input;
    private final int minTier;
    private final ForgeRule[] rules;
    private final boolean applyForgingBonus;
    private final ItemStackProvider output;

    public AnvilRecipe(ResourceLocation id, Ingredient input, int minTier, ForgeRule[] rules, boolean applyForgingBonus, ItemStackProvider output)
    {
        this.id = id;
        this.input = input;
        this.minTier = minTier;
        this.rules = rules;
        this.applyForgingBonus = applyForgingBonus;
        this.output = output;
    }

    /**
     * This match is used for when querying recipes for a single item. Multiple valid recipes may be returned, rather than the first one.
     * As such, this needs to only depend on what recipes can be <em>started</em> on a particular item, anvil combination.
     */
    @Override
    public boolean matches(Inventory inventory, @Nullable Level level)
    {
        return input.test(inventory.getItem()) && inventory.getTier() >= minTier;
    }

    public boolean checkComplete(Inventory inventory)
    {
        final Forging forging = ForgingCapability.get(inventory.getItem());
        return forging != null && forging.matches(rules) && isWorkMatched(forging.getWork(), computeTarget(inventory));
    }

    public ForgeRule[] getRules()
    {
        return rules;
    }

    public boolean shouldApplyForgingBonus()
    {
        return applyForgingBonus;
    }

    @Override
    public ItemStack assemble(Inventory inventory)
    {
        return output.getStack(inventory.getItem());
    }

    @Override
    public ItemStack getResultItem()
    {
        return output.getEmptyStack();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ANVIL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.ANVIL.get();
    }

    public Ingredient getInput()
    {
        return input;
    }

    public int computeTarget(Inventory inventory)
    {
        return 40 + new XoroshiroRandomSource(inventory.getSeed())
            .forkPositional()
            .fromHashOf(id)
            .nextInt(154 - 2 * 40);
    }

    private boolean isWorkMatched(int work, int target)
    {
        final int leeway = TFCConfig.SERVER.anvilAcceptableWorkRange.get();
        return work >= target - leeway && work <= target + leeway;
    }

    public interface Inventory extends EmptyInventory
    {
        /**
         * @return the primary input to the anvil recipe
         */
        ItemStack getItem();

        /**
         * @return The tier ({@link net.dries007.tfc.util.Metal.Tier} of the anvil)
         */
        int getTier();

        /**
         * @return The seed for the anvil recipe work target. By default this returns the world seed. Only called on server, free to return zero elsewhere.
         */
        long getSeed();
    }

    public static class Serializer extends RecipeSerializerImpl<AnvilRecipe>
    {
        @Override
        public AnvilRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(json.get("input"));
            final int tier = JsonHelpers.getAsInt(json, "tier", -1);
            final JsonArray rulesJson = JsonHelpers.getAsJsonArray(json, "rules");
            final ForgeRule[] rules = new ForgeRule[rulesJson.size()];
            for (int i = 0; i < rules.length; i++)
            {
                rules[i] = JsonHelpers.getEnum(rulesJson.get(i), ForgeRule.class);
            }
            if (!ForgeRule.isConsistent(rules))
            {
                throw new JsonParseException("The rules " + Arrays.toString(rules) + " cannot be satisfied by any combination of steps!");
            }
            final boolean applyForgingBonus = JsonHelpers.getAsBoolean(json, "apply_forging_bonus", false);
            final ItemStackProvider output = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            return new AnvilRecipe(recipeId, ingredient, tier, rules, applyForgingBonus, output);
        }

        @Nullable
        @Override
        public AnvilRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient input = Ingredient.fromNetwork(buffer);
            final int tier = buffer.readVarInt();
            final ForgeRule[] rules = Helpers.decodeArray(buffer, ForgeRule[]::new, ForgeRule::fromNetwork);
            final boolean applyForgingBonus = buffer.readBoolean();
            final ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            return new AnvilRecipe(recipeId, input, tier, rules, applyForgingBonus, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AnvilRecipe recipe)
        {
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.minTier);
            Helpers.encodeArray(buffer, recipe.rules, ForgeRule::toNetwork);
            buffer.writeBoolean(recipe.applyForgingBonus);
            recipe.output.toNetwork(buffer);
        }
    }
}
