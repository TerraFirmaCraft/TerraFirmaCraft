/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.util.SimpleCraftMatrix;

public abstract class KnappingRecipe extends IForgeRegistryEntry.Impl<KnappingRecipe>
{
    private final Type type;
    private final SimpleCraftMatrix matrix;

    public KnappingRecipe(Type type, String[] pattern)
    {
        this.matrix = new SimpleCraftMatrix(pattern);
        this.type = type;
    }

    public SimpleCraftMatrix getMatrix()
    {
        return matrix;
    }

    public abstract ItemStack getOutput(ItemStack input);

    public Type getType()
    {
        return this.type;
    }

    public enum Type
    {
        STONE,
        CLAY,
        LEATHER
    }

    public static class Stone extends KnappingRecipe
    {
        private final Function<RockCategory, ItemStack> supplier;

        public Stone(Type type, Function<RockCategory, ItemStack> supplier, String... pattern)
        {
            super(type, pattern);
            this.supplier = supplier;
        }

        @Override
        public ItemStack getOutput(ItemStack input)
        {
            if (input.getItem() instanceof IRockObject)
            {
                return supplier.apply(((IRockObject) input.getItem()).getRockCategory(input));
            }
            return ItemStack.EMPTY;
        }
    }

    public static class Simple extends KnappingRecipe
    {
        private final ItemStack output;

        public Simple(Type type, ItemStack output, String[] pattern)
        {
            super(type, pattern);
            this.output = output;
        }

        @Override
        public ItemStack getOutput(ItemStack input)
        {
            return output.copy();
        }
    }
}
