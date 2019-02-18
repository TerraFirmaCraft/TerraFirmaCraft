/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.forge.ForgeSteps;

/**
 * Anvil Recipe
 *
 * They all take a single item input and will produce a single item output
 */
@ParametersAreNonnullByDefault
public class AnvilRecipe extends IForgeRegistryEntry.Impl<AnvilRecipe>
{
    private static long SEED = 0;

    private static final Random RNG = new Random();

    @Nonnull
    public static List<AnvilRecipe> getAllFor(ItemStack stack)
    {
        return TFCRegistries.ANVIL.getValuesCollection().stream().filter(x -> x.matches(stack)).collect(Collectors.toList());
    }

    private final ForgeRule[] rules;
    private final ItemStack output;
    private final ItemStack input;
    private final Metal.Tier minTier;
    private final long workingSeed;

    public AnvilRecipe(ResourceLocation name, ItemStack input, ItemStack output, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        this.input = input;
        this.output = output;
        if (input.isEmpty() || output.isEmpty())
            throw new IllegalArgumentException("Input and output are not allowed to be empty");

        this.rules = rules;
        if (rules.length == 0 || rules.length > 3)
            throw new IllegalArgumentException("Rules length must be within the closed interval [1, 3]");

        this.minTier = minTier;

        setRegistryName(name);
        workingSeed = ++SEED;
    }

    public boolean matches(ItemStack input)
    {
        return this.input.isItemEqual(input);
    }

    public boolean matches(ForgeSteps steps)
    {
        for (ForgeRule rule : rules)
        {
            if (!rule.matches(steps))
                return false;
        }
        return true;
    }

    @Nonnull
    public ItemStack getOutput()
    {
        return output.copy();
    }

    @Nonnull
    public ForgeRule[] getRules()
    {
        return rules;
    }

    @Nonnull
    public Metal.Tier getTier()
    {
        return minTier;
    }

    public int getTarget(long worldSeed)
    {
        RNG.setSeed(worldSeed + workingSeed);
        return 40 + RNG.nextInt(TEAnvilTFC.WORK_MAX + -2 * 40);
    }
}
