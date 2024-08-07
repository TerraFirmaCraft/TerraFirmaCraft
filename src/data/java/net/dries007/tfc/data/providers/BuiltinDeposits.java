/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Deposit;

public class BuiltinDeposits extends DataManagerProvider<Deposit>
{
    public BuiltinDeposits(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Deposit.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        TFCBlocks.ORE_DEPOSITS.forEach((rock, map) -> map.forEach((type, block) -> {
            final String rockId = rock.getSerializedName();
            final String typeId = type.name().toLowerCase(Locale.ROOT);
            add("%s/%s".formatted(typeId, rockId), new Deposit(
                Ingredient.of(block),
                ResourceKey.create(Registries.LOOT_TABLE, Helpers.identifier("deposit/%s_%s".formatted(rockId, typeId))),
                List.of(
                    Helpers.identifier("item/pan/%s/%s_full".formatted(typeId, rockId)),
                    Helpers.identifier("item/pan/%s/%s_half".formatted(typeId, rockId)),
                    Helpers.identifier("item/pan/%s/result".formatted(typeId))
                )
            ));
        }));
    }
}
