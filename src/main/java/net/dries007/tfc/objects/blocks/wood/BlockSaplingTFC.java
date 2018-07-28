/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockSaplingTFC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
    private static final Map<Tree, BlockSaplingTFC> MAP = new HashMap<>();

    public static BlockSaplingTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

    public BlockSaplingTFC(Tree wood)
    {
        super();
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setHardness(0.0F);
        OreDictionaryHelper.register(this, "tree", "sapling");
        OreDictionaryHelper.register(this, "tree", "sapling", wood.name);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @SuppressWarnings("deprecation")
    @Override
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
    public int damageDropped(IBlockState state)
    {
        return 0; // explicit override on default, because saplings should be reset when they are broken.
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (world.isRemote) return;

        super.updateTick(world, pos, state, random);

        //todo: move conditions to canGrow
        if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0)
        {
            // todo: see what 1710 checks
            grow(world, random, pos, state);
        }
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState blockState)
    {
        // todo
        //wood.makeTree(world, blockPos, random);
    }

    @Override
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState)
    {
        TerraFirmaCraft.getLog().info("canUseBoneMeal called");
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, STAGE);
    }
}
