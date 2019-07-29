/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.util.SimpleCraftMatrix;

/**
 * todo: in 1.13+ move this to a json recipe type
 */
public abstract class KnappingRecipe extends IForgeRegistryEntry.Impl<KnappingRecipe>
{
    private final Type type;
    private final SimpleCraftMatrix matrix;

    public KnappingRecipe(Type type, boolean outsideSlotRequired, String... pattern)
    {
        this.matrix = new SimpleCraftMatrix(outsideSlotRequired, pattern);
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
        STONE(1),
        CLAY(5),
        FIRE_CLAY(5),
        LEATHER(1);

        private final int amountToConsume;

        Type(int amountToConsume)
        {
            this.amountToConsume = amountToConsume;
        }

        public int getAmountToConsume()
        {
            return amountToConsume;
        }
    }

    public static class Stone extends KnappingRecipe
    {
        private final Function<RockCategory, ItemStack> supplier;

        public Stone(Type type, Function<RockCategory, ItemStack> supplier, String... pattern)
        {
            super(type, false, pattern);
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

        public Simple(Type type, boolean outsideSlotRequired, ItemStack output, String... pattern)
        {
            super(type, outsideSlotRequired, pattern);
            this.output = output;
        }

        @Override
        public ItemStack getOutput(ItemStack input)
        {
            return output.copy();
        }
    }
}
