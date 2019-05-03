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

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TESaplingTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.world.classic.CalendarTFC;

@ParametersAreNonnullByDefault
public class BlockSaplingTFC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0., 0.9);
    private static final Map<Tree, BlockSaplingTFC> MAP = new HashMap<>();

    public static BlockSaplingTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TESaplingTFC te = Helpers.getTE(worldIn, pos, TESaplingTFC.class);
        if (te != null) te.onPlaced();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0; // explicit override on default, because saplings should be reset when they are broken.
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, STAGE);
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
        return new TESaplingTFC();
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);

        if (!world.isRemote)
        {
            TESaplingTFC te = Helpers.getTE(world, pos, TESaplingTFC.class);
            if (te != null)
            {
                long hours = te.getHoursSincePlaced();
                if (hours > wood.getMinGrowthTime() * CalendarTFC.HOURS_IN_DAY)
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
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState)
    {
        TerraFirmaCraft.getLog().debug("canUseBoneMeal called");
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState blockState)
    {
        wood.makeTree(world, blockPos, random);
    }
}
