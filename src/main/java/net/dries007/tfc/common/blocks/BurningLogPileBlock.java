package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.tileentity.BurningLogPileTileEntity;
import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class BurningLogPileBlock extends Block implements IForgeBlockProperties
{
    private static boolean isValidCoverBlock(BlockState offsetState, World world, BlockPos pos, Direction side)
    {
        if (offsetState.is(TFCBlocks.LOG_PILE.get()) || offsetState.is(TFCBlocks.CHARCOAL_PILE.get()) || offsetState.is(TFCBlocks.BURNING_LOG_PILE.get()))
        {
            return true;
        }
        return !offsetState.getMaterial().isFlammable() && offsetState.isFaceSturdy(world, pos, side);
    }

    private final ForgeBlockProperties properties;

    public BurningLogPileBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        tryLightNearby(world, pos);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double x = pos.getX() + rand.nextFloat();
        double y = pos.getY() + 1;
        double z = pos.getZ() + rand.nextFloat();
        worldIn.addParticle(ParticleTypes.SMOKE, x, y, z, 0f, 0.1f + 0.1f * rand.nextFloat(), 0f);
        if (rand.nextInt(12) == 0)
        {
            worldIn.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
    }

    public static void tryLightLogPile(World world, BlockPos pos)
    {
        LogPileTileEntity pile = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
        if (pile != null)
        {
            int logs = pile.countLogs();
            pile.clearContent(); // avoid dumping when onRemove is called
            world.setBlockAndUpdate(pos, TFCBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            BurningLogPileTileEntity newPile = Helpers.getTileEntity(world, pos, BurningLogPileTileEntity.class);
            if (newPile != null)
            {
                newPile.light(logs);
                world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, SoundEvents.BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, 0.8f, false);
                tryLightNearby(world, pos);
            }
        }
    }

    private static void tryLightNearby(World world, BlockPos pos)
    {
        if (world.isClientSide()) return;
        for (Direction side : Direction.values())
        {
            final BlockPos offsetPos = pos.relative(side);
            BlockState offsetState = world.getBlockState(offsetPos);
            if (isValidCoverBlock(offsetState, world, offsetPos, side.getOpposite()))
            {
                if (offsetState.is(TFCBlocks.LOG_PILE.get()))
                {
                    tryLightLogPile(world, offsetPos);
                }
            }
            else
            {
                world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            }
        }
    }
}
