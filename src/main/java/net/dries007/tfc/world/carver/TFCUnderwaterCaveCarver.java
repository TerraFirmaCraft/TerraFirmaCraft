/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.RockManager;

public class TFCUnderwaterCaveCarver extends UnderwaterCaveWorldCarver
{
    private final Set<Block> originalCarvableBlocks;

    public TFCUnderwaterCaveCarver(Codec<ProbabilityConfig> dynamic)
    {
        super(dynamic);
        originalCarvableBlocks = replaceableBlocks;

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> replaceableBlocks = TFCCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }
}