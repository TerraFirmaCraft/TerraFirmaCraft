package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.IFallingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;
import java.util.Random;

public class BlockPath extends BlockGrassPath implements IFallingBlock {

    private static final EnumMap<Rock, BlockPath> MAP = new EnumMap<>(Rock.class);

    public static BlockPath get(Rock rock)
    {
        return MAP.get(rock);
    }

    public final Rock rock;

    public BlockPath(Rock rock)
    {
        super();
        this.rock = rock;
    }

    public boolean shouldFall(IBlockState state, World world, BlockPos pos)
    {
        return IFallingBlock.super.shouldFall(state, world, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
        if (worldIn.isRemote) return;
        if (shouldFall(state, worldIn, pos))
        {
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            {
                worldIn.spawnEntity(new EntityFallingBlockTFC(worldIn, pos, this, worldIn.getBlockState(pos)));
            } else
            {
                worldIn.setBlockToAir(pos);
                pos = pos.add(0, -1, 0);
                while (canFallThrough(worldIn.getBlockState(pos)) && pos.getY() > 0)
                    pos = pos.add(0, -1, 0);
                if (pos.getY() > 0) worldIn.setBlockState(pos.up(), state); // Includes Forge's fix for data loss.
            }
        }
    }

        @SideOnly(Side.CLIENT)
        public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
        {
            if (rand.nextInt(16) != 0) return;
            if (shouldFall(stateIn, worldIn, pos))
            {
                double d0 = (double) ((float) pos.getX() + rand.nextFloat());
                double d1 = (double) pos.getY() - 0.05D;
                double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
                worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
            }
        }

        // IDK what the alternative is supposed to be, so I'm gonna continue using this.
        @SuppressWarnings("deprecation")
        @Override
        public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
        {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
            if (shouldFall(state, worldIn, pos)) worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
        }

        @Override
        public int tickRate(World worldIn)
        {
            return 1; // todo: tickrate in vanilla is 2, in tfc1710 it's 10
        }

        @Override
        public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
        {
            if (shouldFall(state, worldIn, pos)) worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
        }

        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune)
        {
            return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);

        }

        @Override
        public int damageDropped(IBlockState state)
        {
            return getMetaFromState(state);
        }

    }
