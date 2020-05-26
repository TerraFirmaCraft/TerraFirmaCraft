/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class ModuloLayer implements IC0Transformer
{
    private final int modulus;

    public ModuloLayer(int modulus)
    {
        this.modulus = modulus;
    }

    @Override
    public int apply(INoiseRandom context, int value)
    {
        return value % modulus;
    }
}
