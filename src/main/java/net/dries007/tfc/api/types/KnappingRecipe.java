/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class KnappingRecipe extends IForgeRegistryEntry.Impl<KnappingRecipe>
{
    private final int width;
    private final int height;
    private final boolean[] pattern;
    private final ItemStack output;

    public KnappingRecipe(ItemStack output, String... pattern)
    {
        if (pattern.length == 0 || pattern.length > 5)
            throw new IllegalArgumentException("Pattern length is invalid");

        this.width = pattern[0].length();
        this.height = pattern.length;
        this.output = output;
        this.pattern = new boolean[width * height];

        for (int i = 0; i < height; i++)
        {
            String line = pattern[i];
            if (line.length() != width)
                throw new IllegalArgumentException("Line " + i + " in the pattern has the incorrect length");
            for (int c = 0; c < width; c++)
                this.pattern[i * height + c] = (line.charAt(c) == ' ');
        }
    }

    // This will check if it matches a 5x5 boolean matrix, from a GuiContainerKnapping
    public boolean matches(boolean[] matrix)
    {
        // Check all possible shifted positions
        for (int xShift = 0; xShift <= 5 - width; xShift++)
        {
            for (int yShift = 0; yShift <= 5 - height; yShift++)
            {
                boolean flag = true;
                // check if the matrix matches this orientation
                for (int x = 0; x < width; x++)
                {
                    for (int y = 0; y < height; y++)
                    {
                        // Check the individual position
                        int matrixIdx = (yShift + y) * 5 + xShift + x;
                        int patternIdx = (yShift + y) * width + xShift + x;
                        if (matrix[matrixIdx] != pattern[patternIdx])
                        {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag)
                    return true;
            }
        }
        return false;
    }

    public ItemStack getOutput()
    {
        return output.copy();
    }
}
