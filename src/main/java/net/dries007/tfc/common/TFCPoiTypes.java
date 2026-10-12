/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.LobsterTrapBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;

public final class TFCPoiTypes
{
    public static final DeferredRegister<PoiType> TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, TerraFirmaCraft.MOD_ID);
    public static final DeferredHolder<PoiType, PoiType> CLIMATE = TYPES.register("climate", () -> new PoiType(
        ImmutableSet.<BlockState>builder()
            .addAll(states(Blocks.SNOW))
            .addAll(states(TFCBlocks.SNOW_PILE.get()))
            .addAll(states(Blocks.ICE))
            .addAll(states(TFCBlocks.ICE_PILE.get()))
            .addAll(states(TFCBlocks.ICICLE.get()))
            .build(),
        0, 1
    ));

    public static final DeferredHolder<PoiType, PoiType> BAITED_LOBSTER_TRAP = TYPES.register("baited_lobster_trap", () -> new PoiType(
        TFCBlocks.LOBSTER_TRAP.get().getStateDefinition().getPossibleStates()
            .stream()
            .filter(state -> state.getValue(LobsterTrapBlock.TRAP) == LobsterTrapBlock.TrapState.BAITED)
            .collect(ImmutableSet.toImmutableSet()),
        0, 1
    ));

    private static Iterable<BlockState> states(Block block)
    {
        return block.getStateDefinition().getPossibleStates();
    }
}
