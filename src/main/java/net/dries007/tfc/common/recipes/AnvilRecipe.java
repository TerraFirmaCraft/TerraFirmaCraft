/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;
import com.google.common.collect.BiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.component.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.Metal;

public class AnvilRecipe implements ISimpleRecipe<AnvilRecipe.Inventory>
{
    public static boolean hasAny(Level level, ItemStack stack, int tier)
    {
        return RecipeHelpers.getRecipes(level, TFCRecipeTypes.ANVIL)
            .stream()
            .anyMatch(r -> r.value().input.test(stack) && tier >= r.value().minTier); // anyMatch() should be faster than calling toList().isEmpty()
    }

    public static List<AnvilRecipe> getAll(Level level, ItemStack stack, int tier)
    {
        return RecipeHelpers.getRecipes(level, TFCRecipeTypes.ANVIL)
            .stream()
            .filter(r -> r.value().input.test(stack) && tier >= r.value().minTier)
            .map(RecipeHolder::value)
            .toList();
    }

    private static final BiMap<ResourceLocation, AnvilRecipe> CACHE = IndirectHashCollection.createForRecipeId(TFCRecipeTypes.ANVIL);

    @Nullable
    public static AnvilRecipe byId(ResourceLocation id)
    {
        return CACHE.get(id);
    }

    @Nullable
    public static ResourceLocation getId(AnvilRecipe recipe)
    {
        return CACHE.inverse().get(recipe);
    }

    public static final MapCodec<AnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.input),
        Codec.INT.optionalFieldOf("tier", -1).forGetter(c -> c.minTier),
        ForgeRule.CODEC.listOf().fieldOf("rules")
            .validate(rules -> ForgeRule.isConsistent(rules)
                ? DataResult.success(rules)
                : DataResult.error(() -> "The rules " + rules + " cannot be satisfied by any combination of steps!"))
            .forGetter(c -> c.rules),
        Codec.BOOL.optionalFieldOf("apply_bonus", false).forGetter(c -> c.applyForgingBonus),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.output)
    ).apply(i, AnvilRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.input,
        ByteBufCodecs.VAR_INT, c -> c.minTier,
        ForgeRule.STREAM_CODEC.apply(ByteBufCodecs.list(3)), c -> c.rules,
        ByteBufCodecs.BOOL, c -> c.applyForgingBonus,
        ItemStackProvider.STREAM_CODEC, c -> c.output,
        AnvilRecipe::new
    );

    private final Ingredient input;
    private final int minTier;
    private final List<ForgeRule> rules;
    private final boolean applyForgingBonus;
    private final ItemStackProvider output;

    public AnvilRecipe(Ingredient input, int minTier, List<ForgeRule> rules, boolean applyForgingBonus, ItemStackProvider output)
    {
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
        return input.test(inventory.getItem()) && isCorrectTier(inventory.getTier());
    }

    public boolean checkComplete(Inventory inventory)
    {
        final Forging forging = ForgingCapability.get(inventory.getItem());
        return forging.view().matches(rules)
            && isWorkMatched(forging.view().work(), computeTarget(inventory));
    }

    public List<ForgeRule> getRules()
    {
        return rules;
    }

    public boolean shouldApplyForgingBonus()
    {
        return applyForgingBonus;
    }

    /**
     * @return {@code true} if an anvil of {@code anvilTier} can perform this recipe.
     */
    public boolean isCorrectTier(int anvilTier)
    {
        return anvilTier >= minTier;
    }

    public int getMinTier()
    {
        return minTier;
    }

    @Override
    public ItemStack assemble(Inventory input, HolderLookup.Provider registries)
    {
        return output.getStack(input.getItem());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return output.getEmptyStack();
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
            .fromHashOf(BuiltInRegistries.ITEM.getKey(output.stack().getItem()))
            .nextInt(154 - 2 * 40);
    }

    private boolean isWorkMatched(int work, int target)
    {
        final int leeway = TFCConfig.SERVER.anvilAcceptableWorkRange.get();
        return work >= target - leeway && work <= target + leeway;
    }

    public interface Inventory extends RecipeInput
    {
        /**
         * @return the primary input to the anvil recipe
         */
        ItemStack getItem();

        /**
         * @return The tier ({@link Metal.Tier} of the anvil)
         */
        int getTier();

        /**
         * @return The seed for the anvil recipe work target. By default, this returns the world seed.
         * Only called on server, free to return zero elsewhere.
         */
        long getSeed();
    }
}
