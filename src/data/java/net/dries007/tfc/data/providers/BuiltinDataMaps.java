/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;

public class BuiltinDataMaps extends DataMapProvider
{
    public BuiltinDataMaps(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup);
    }

    @Override
    protected void gather()
    {
        for (Metal metal : Metal.values())
            if (metal.weatheredParts())
            {
                add(metal, Metal.BlockType.BLOCK, Metal.BlockType.EXPOSED_BLOCK);
                add(metal, Metal.BlockType.EXPOSED_BLOCK, Metal.BlockType.WEATHERED_BLOCK);
                add(metal, Metal.BlockType.WEATHERED_BLOCK, Metal.BlockType.OXIDIZED_BLOCK);

                add(metal, Metal.BlockType.BLOCK_SLAB, Metal.BlockType.EXPOSED_BLOCK_SLAB);
                add(metal, Metal.BlockType.EXPOSED_BLOCK_SLAB, Metal.BlockType.WEATHERED_BLOCK_SLAB);
                add(metal, Metal.BlockType.WEATHERED_BLOCK_SLAB, Metal.BlockType.OXIDIZED_BLOCK_SLAB);

                add(metal, Metal.BlockType.BLOCK_STAIRS, Metal.BlockType.EXPOSED_BLOCK_STAIRS);
                add(metal, Metal.BlockType.EXPOSED_BLOCK_STAIRS, Metal.BlockType.WEATHERED_BLOCK_STAIRS);
                add(metal, Metal.BlockType.WEATHERED_BLOCK_STAIRS, Metal.BlockType.OXIDIZED_BLOCK_STAIRS);
            }
    }

    private void add(Metal metal, Metal.BlockType from, Metal.BlockType to)
    {
        builder(NeoForgeDataMaps.OXIDIZABLES).add(
            TFCBlocks.METALS.get(metal).get(from).holder(),
            new Oxidizable(TFCBlocks.METALS.get(metal).get(to).get()),
            false
        );
    }
}
