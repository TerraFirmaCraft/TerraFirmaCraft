/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockFruitTreeSapling extends BlockBush implements IGrowable
{
    private static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.9, 0.9);

    private static final Map<IFruitTree, BlockFruitTreeSapling> MAP = new HashMap<>();

    public static BlockFruitTreeSapling get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    private final IFruitTree tree;

    public BlockFruitTreeSapling(IFruitTree tree)
    {
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        this.tree = tree;
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        setTickRandomly(true);
        OreDictionaryHelper.register(this, "tree", "sapling");
        OreDictionaryHelper.register(this, "tree", "sapling", tree.getName());
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);

        if (!world.isRemote)
        {
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                float temp = ClimateTFC.getActualTemp(world, pos);
                float rainfall = ChunkDataTFC.getRainfall(world, pos);
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > (tree.getGrowthTime() * ConfigTFC.General.FOOD.fruitTreeGrowthTimeModifier) && tree.isValidForGrowth(temp, rainfall))
                {
                    te.resetCounter();
                    grow(world, random, pos, state);
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (te != null)
        {
            te.resetCounter();
        }
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETickCounter();
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    @Override
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState)
    {
        return true; //Only on sapling tho, so trunk still has to grow
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState blockState)
    {
        if (!world.isRemote)
        {
            world.setBlockState(blockPos, BlockFruitTreeTrunk.get(this.tree).getDefaultState());
            if (world.getBlockState(blockPos.up()).getMaterial().isReplaceable())
            {
                world.setBlockState(blockPos.up(), BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, false));
            }
        }
    }

    @Nonnull
    public IFruitTree getTree()
    {
        return tree;
    }
}
