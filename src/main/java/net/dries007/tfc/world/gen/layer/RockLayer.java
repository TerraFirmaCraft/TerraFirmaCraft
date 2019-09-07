/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import java.util.Arrays;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.world.gen.rock.RockCategory;

public enum RockLayer implements IC1Transformer
{
    INSTANCE;

    private final int[][] categoryToRockValues = Arrays.stream(RockCategory.values()).map(category -> TFCRegistries.ROCKS.getValues().stream().mapToInt(TFCRegistries.ROCKS::getID).toArray()).toArray(int[][]::new);
    private final int[] categoryToRockLengths = Arrays.stream(categoryToRockValues).mapToInt(array -> array.length).toArray();

    @Override
    public int apply(INoiseRandom context, int value)
    {
        return categoryToRockValues[value][context.random(categoryToRockLengths[value])];
    }
}
