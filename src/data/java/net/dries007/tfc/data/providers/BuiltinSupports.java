/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.data.Support;

public class BuiltinSupports extends DataManagerProvider<Support>
{
    public BuiltinSupports(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Support.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("horizontal_support_beam", new Support(BlockIngredient.of(
            Arrays.stream(Wood.values()).map(w -> TFCBlocks.WOODS.get(w).get(Wood.BlockType.HORIZONTAL_SUPPORT).get())),
            2, 2, 4
        ));
    }
}
