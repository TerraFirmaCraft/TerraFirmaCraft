package net.dries007.tfc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CharcoalPileBlock extends SnowBlock
{
    public CharcoalPileBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(worldIn, pos.below()), Direction.UP) || (blockstate.getBlock() == this && blockstate.getValue(LAYERS) == 8);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(world, pos, state, player);
        int prevLayers = state.getValue(LAYERS);
        if (prevLayers == 1)
        {
            return true;
        }
        return world.setBlock(pos, state.setValue(LAYERS, prevLayers - 1), world.isClientSide ? 11 : 3);
    }
}
