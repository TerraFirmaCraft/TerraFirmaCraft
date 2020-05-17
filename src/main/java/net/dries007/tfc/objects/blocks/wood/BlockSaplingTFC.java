/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

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
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class BlockSaplingTFC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.9, 0.9);
    private static final Map<Tree, BlockSaplingTFC> MAP = new HashMap<>();

    public static BlockSaplingTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    private final Tree wood;

    public BlockSaplingTFC(Tree wood)
    {
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        OreDictionaryHelper.register(this, "tree", "sapling");
        //noinspection ConstantConditions
        OreDictionaryHelper.register(this, "tree", "sapling", wood.getRegistryName().getPath());
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(STAGE);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, STAGE);
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

    public Tree getWood()
    {
        return wood;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);

        if (!world.isRemote)
        {
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                long days = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;
                if (days > wood.getMinGrowthTime())
                {
                    grow(world, random, pos, state);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    @Override
    @Nonnull
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Plains;
    }

    @Override
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState)
    {
        return false;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState blockState)
    {
        wood.makeTree(world, blockPos, random, false);
    }
}
