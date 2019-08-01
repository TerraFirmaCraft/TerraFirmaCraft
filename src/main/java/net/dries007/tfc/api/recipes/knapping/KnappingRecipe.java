/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.util.SimpleCraftMatrix;

/**
 * todo: in 1.13+ move this to a json recipe type
 */
public abstract class KnappingRecipe extends IForgeRegistryEntry.Impl<KnappingRecipe>
{
    private final IKnappingType type;
    private final SimpleCraftMatrix matrix;

    protected KnappingRecipe(IKnappingType type, boolean outsideSlotRequired, String... pattern)
    {
        this.matrix = new SimpleCraftMatrix(outsideSlotRequired, pattern);
        this.type = type;
    }

    public SimpleCraftMatrix getMatrix()
    {
        return matrix;
    }

    public abstract ItemStack getOutput(ItemStack input);

    public IKnappingType getType()
    {
        return this.type;
    }

    /**
     * Default TFC knapping types. Feel free to implement your own.
     */
    public enum Type implements IKnappingType
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

        @Nonnull
        @Override
        public String getName()
        {
            return name();
        }

        @Override
        public int getAmountToConsume()
        {
            return amountToConsume;
        }
    }
}
