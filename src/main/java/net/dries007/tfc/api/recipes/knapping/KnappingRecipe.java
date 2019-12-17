/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.util.SimpleCraftMatrix;

/**
 * todo: in 1.13+ move this to a json recipe type
 */
public abstract class KnappingRecipe extends IForgeRegistryEntry.Impl<KnappingRecipe>
{
    private final KnappingType type;
    private final SimpleCraftMatrix matrix;

    protected KnappingRecipe(KnappingType type, boolean outsideSlotRequired, String... pattern)
    {
        this.matrix = new SimpleCraftMatrix(outsideSlotRequired, pattern);
        this.type = type;
    }

    public SimpleCraftMatrix getMatrix()
    {
        return matrix;
    }

    public abstract ItemStack getOutput(ItemStack input);

    public KnappingType getType()
    {
        return this.type;
    }
}
