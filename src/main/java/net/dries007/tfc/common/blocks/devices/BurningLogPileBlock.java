package net.dries007.tfc.common.blocks.devices;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BurningLogPileTileEntity;
import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class BurningLogPileBlock extends Block implements IForgeBlockProperties
{
    public static void tryLightLogPile(World world, BlockPos pos)
    {
        LogPileTileEntity pile = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
        if (pile != null)
        {
            int logs = pile.countLogs();
            pile.clearContent(); // avoid dumping when onRemove is called
            world.setBlockAndUpdate(pos, TFCBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            Helpers.playSound(world, pos, SoundEvents.BLAZE_SHOOT);

            BurningLogPileTileEntity newPile = Helpers.getTileEntity(world, pos, BurningLogPileTileEntity.class);
            if (newPile != null)
            {
                newPile.light(logs);
                tryLightNearby(world, pos);
            }
        }
    }

    private static boolean isValidCoverBlock(BlockState offsetState, World world, BlockPos pos, Direction side)
    {
        if (offsetState.is(TFCTags.Blocks.CHARCOAL_COVER_WHITELIST))// log pile, charcoal pile, this
        {
            return true;
        }
        return !offsetState.getMaterial().isFlammable() && offsetState.isFaceSturdy(world, pos, side);
    }

    private static void tryLightNearby(World world, BlockPos pos)
    {
        if (world.isClientSide()) return;
        for (Direction side : Helpers.DIRECTIONS)
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
                world.setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
            }
        }
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

    @OnlyIn(Dist.CLIENT)
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
        for (int i = 0; i < rand.nextInt(3); i++)
        {
            worldIn.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, (0.5F - rand.nextFloat()) / 10, 0.1f + rand.nextFloat() / 8, (0.5F - rand.nextFloat()) / 10);
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(Items.CHARCOAL);
    }
}
