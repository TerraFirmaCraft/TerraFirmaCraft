/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockLeavesTFC extends BlockLeaves
{
    private static final Map<Tree, BlockLeavesTFC> MAP = new HashMap<>();

    public static BlockLeavesTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

    public BlockLeavesTFC(Tree wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setDefaultState(blockState.getBaseState().withProperty(DECAYABLE, false)); // TFC leaves don't use CHECK_DECAY, so just don't use it
        leavesFancy = true; // Fast / Fancy graphics works correctly
        OreDictionaryHelper.register(this, "tree", "leaves");
        OreDictionaryHelper.register(this, "tree", "leaves", wood.getRegistryName().getPath());
        Blocks.FIRE.setFireInfo(this, 30, 60);
        setTickRandomly(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DECAYABLE, (meta & 0b01) == 0b01);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(DECAYABLE) ? 1 : 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, @Nullable Block blockIn, @Nullable BlockPos fromPos)
    {
        doLeafDecay(world, pos, state);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        //Player will take damage when falling through leaves if fall is over 9 blocks, fall damage is then set to 0.
        entityIn.fall((entityIn.fallDistance - 6), 1.0F); // TODO: 17/4/18 Balance fall distance reduction.
        entityIn.fallDistance = 0;
        //Entity motion is reduced by leaves.
        entityIn.motionX *= 0.1D;
        entityIn.motionY *= 0.1D;
        entityIn.motionZ *= 0.1D;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, DECAYABLE);
    }

    @Override
    public void updateTick(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand)
    {
        doLeafDecay(worldIn, pos, state);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(BlockSaplingTFC.get(wood));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        /*
         * This is a way to make sure the leave settings are updated.
         * The result of this call is cached somewhere, so it's not that important, but:
         * The alternative would be to use `Minecraft.getMinecraft().gameSettings.fancyGraphics` directly in the 2 relevant methods.
         * It's better to do that than to refer to Blocks.LEAVES, for performance reasons.
         */
        leavesFancy = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        return super.getRenderLayer();
    }

    @Override
    @Nonnull
    public BlockPlanks.EnumType getWoodType(int meta)
    {
        // Unused so return whatever
        return BlockPlanks.EnumType.OAK;
    }

    @Override
    public void beginLeavesDecay(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos)
    {
        // Don't do vanilla decay
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        /*
         * See comment on getRenderLayer()
         */
        leavesFancy = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    @Nonnull
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return ImmutableList.of(new ItemStack(this));
    }

    private void doLeafDecay(World world, BlockPos pos, IBlockState state)
    {
        // TFC Leaf Decay
        if (world.isRemote || !state.getValue(DECAYABLE))
            return;

        List<BlockPos> paths = new ArrayList<>();
        List<BlockPos> pathsToAdd;
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos(pos);
        IBlockState state1;
        paths.add(pos); // Center block

        for (int i = 0; i < wood.getMaxDecayDistance(); i++)
        {
            pathsToAdd = new ArrayList<>();
            for (BlockPos p1 : paths)
            {
                for (EnumFacing face : EnumFacing.values())
                {
                    pos1.setPos(p1).move(face);
                    if (paths.contains(pos1.toImmutable()))
                        continue;
                    state1 = world.getBlockState(pos1);
                    if (state1.getBlock() == BlockLogTFC.get(wood))
                        return;
                    if (state1.getBlock() == this)
                        pathsToAdd.add(pos1.toImmutable());

                }
            }
            paths.addAll(pathsToAdd);
        }

        world.setBlockToAir(pos);
    }
}
