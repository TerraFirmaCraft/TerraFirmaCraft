/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

import com.mojang.serialization.Codec;

public class FacingHorizontalBlockStateProvider extends BlockStateProvider
{
    public static final Codec<FacingHorizontalBlockStateProvider> CODEC = Codecs.LENIENT_BLOCKSTATE.fieldOf("state").xmap(FacingHorizontalBlockStateProvider::new, provider -> provider.state).codec();

    private final BlockState state;

    public FacingHorizontalBlockStateProvider(BlockState state)
    {
        this.state = state;
    }

    @Override
    protected BlockStateProviderType<?> type()
    {
        return TFCBlockStateProviderTypes.HORIZONTAL_FACING_PROVIDER.get();
    }

    @Override
    public BlockState getState(Random random, BlockPos pos)
    {
        Direction facing = Direction.Plane.HORIZONTAL.random(random);
        return this.state.with(HorizontalBlock.HORIZONTAL_FACING, facing);
    }
}
