/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import com.mojang.serialization.Codec;

// todo: stuff
public class DoubleRandomTreeFeature extends RandomTreeFeature
{
    public DoubleRandomTreeFeature(Codec<RandomTreeConfig> codec)
    {
        super(codec);
    }
}