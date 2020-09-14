/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.primitives.Ints;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class BlockFluidTFC extends BlockFluidClassic
{
    public BlockFluidTFC(Fluid fluid, Material material)
    {
        super(fluid, material);
    }

    public BlockFluidTFC(Fluid fluid, Material material, boolean canCreateSources)
    {
        this(fluid, material);
        this.canCreateSources = canCreateSources;
        setHardness(100.0F);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        // we don't call updateTick(). Why? Because randomTicks causing flow updates is not how vanilla water works.
        // we want our liquids to behave _exactly_ like vanilla liquids do.
        // even when they are interacting as different types.

        // override this method if you want randomTicks to do something
        // and DO NOT call updateTick with it.
    }

    // NOTE: All of the effects are removed from the fluid, seems to be the only way to fix gray particles coming from the fluids.
    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
    {
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        return true;
    }

    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand)
    {
        super.updateTick(world, pos, state, rand);

        // have to catch the updates that the super call did
        IBlockState newState = world.getBlockState(pos);

        // detect if we should replace ourselves with a different BlockFluidTFC type
        if (!isSourceBlock(world, pos))
        {
            int minMeta = 100;
            int currentMeta = quantaPerBlock - 1;
            BlockFluidTFC blockType = this;

            if (newState.getBlock() == this)
                currentMeta = newState.getValue(LEVEL);

            // only check adjacently if here isn't powered from above
            if (world.getBlockState(pos.down(densityDir)).getBlock() != this)
            {
                for (EnumFacing side : EnumFacing.HORIZONTALS)
                {
                    BlockPos neighborPos = pos.offset(side);
                    IBlockState neighborState = world.getBlockState(neighborPos);
                    Block block = neighborState.getBlock();

                    if (block instanceof BlockFluidTFC)
                    {
                        BlockFluidTFC neighborBlock = (BlockFluidTFC) block;
                        int neighborMeta;
                        Block neighborAboveBlock = world.getBlockState(neighborPos.down(densityDir)).getBlock();
                        if (neighborAboveBlock == neighborBlock)
                            neighborMeta = 0;
                        else
                            neighborMeta = neighborState.getValue(LEVEL);

                        if (neighborMeta < minMeta)
                        {
                            blockType = neighborBlock;
                            minMeta = neighborMeta;
                        }
                        else if (neighborMeta == minMeta)
                        {
                            if (neighborBlock.getDensity() > blockType.getDensity() ||
                                (neighborBlock == this && neighborBlock.getDensity() >= blockType.getDensity()))
                            {
                                blockType = neighborBlock;
                            }
                        }
                    }
                }
            }

            if (minMeta + 1 < currentMeta && blockType != this)
            {
                world.setBlockState(pos, blockType.getDefaultState().withProperty(LEVEL, currentMeta), 3);
            }
        }
    }

    @Override
    protected boolean[] getOptimalFlowDirections(World world, BlockPos pos)
    {
        for (int side = 0; side < 4; side++)
        {
            flowCost[side] = 1000;

            BlockPos pos2 = pos.offset(SIDES.get(side));

            if (!canFlowInto(world, pos2) || isBlockingSourceBlock(world, pos2))
            {
                continue;
            }

            if (canFlowInto(world, pos2.up(densityDir)))
            {
                flowCost[side] = 0;
            }
            else
            {
                flowCost[side] = calculateFlowCost(world, pos2, 1, side);
            }
        }

        int min = Ints.min(flowCost);
        for (int side = 0; side < 4; side++)
        {
            isOptimalFlowDirection[side] = flowCost[side] == min;
        }
        return isOptimalFlowDirection;
    }

    @Override
    protected int calculateFlowCost(World world, BlockPos pos, int recurseDepth, int side)
    {
        int cost = 1000;
        for (int adjSide = 0; adjSide < 4; adjSide++)
        {
            if (SIDES.get(adjSide) == SIDES.get(side).getOpposite())
            {
                continue;
            }

            BlockPos pos2 = pos.offset(SIDES.get(adjSide));

            if (!canFlowInto(world, pos2) || isBlockingSourceBlock(world, pos2))
            {
                continue;
            }

            if (canFlowInto(world, pos2.up(densityDir)))
            {
                return recurseDepth;
            }

            if (recurseDepth >= quantaPerBlock / 2)
            {
                continue;
            }

            cost = Math.min(cost, calculateFlowCost(world, pos2, recurseDepth + 1, adjSide));
        }
        return cost;
    }

    @Override
    protected void flowIntoBlock(World world, BlockPos pos, int meta)
    {
        if (meta < 0) return;

        if (displaceIfPossible(world, pos))
        {
            IBlockState targetBlockState = world.getBlockState(pos);
            Block targetBlock = targetBlockState.getBlock();


            // displaceIfPossible covers all cases that aren't BlockFluidTFc instances
            // If it is a BlockFluidTFC instance, we need to check all the corner cases for our unique flow behavior
            boolean replace = !(targetBlock instanceof BlockFluidBase);

            // to make sure we only replace BlockFluidTFC blocks, and not any other modded fluid blocks.
            if (targetBlock instanceof BlockFluidTFC)
            {
                // always replace flows if we're flowing in from above
                if (world.getBlockState(pos.down(densityDir)).getBlock() == this)
                {
                    replace = true;
                }

                final int blockFlowStrength = targetBlockState.getValue(LEVEL);

                // replace flows immediately when their supporting flows are gone
                if (!replace)
                {
                    boolean supported = false;

                    if (world.getBlockState(pos.down(densityDir)).getBlock() == targetBlock)
                    {
                        supported = true;
                    }

                    if (!supported)
                    {
                        for (EnumFacing side : EnumFacing.HORIZONTALS)
                        {
                            IBlockState neighbor = world.getBlockState(pos.offset(side));
                            if (neighbor.getBlock() == targetBlock &&
                                (blockFlowStrength > neighbor.getValue(LEVEL) ||
                                    world.getBlockState(pos.offset(side).down(densityDir)).getBlock() == targetBlock))
                            {
                                supported = true;
                                break;
                            }
                        }
                    }

                    replace = !supported;
                }

                // we're on equal ground and fighting each other based on density
                // and flow strength
                if (!replace)
                {
                    int flowStrength = blockFlowStrength;

                    if (((BlockFluidTFC) targetBlock).getDensity() > this.getDensity())
                        flowStrength += 1;

                    if (flowStrength > meta)
                        replace = true;
                }
            }

            if (replace)
            {
                world.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, meta), 3);
            }
        }
    }

    @Override
    protected boolean canFlowInto(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return super.canFlowInto(world, pos) || state.getMaterial().isLiquid();
    }

    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block.isAir(state, world, pos))
        {
            return true;
        }

        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }

        Material material = state.getMaterial();
        if (material.blocksMovement() || material == Material.PORTAL || material == Material.STRUCTURE_VOID)
        {
            return false;
        }

        // this is where it differs from the source:

        if (block instanceof BlockFluidTFC)
        {
            return (state.getValue(LEVEL) != 0);
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
            return true;
        }

        return this.density > density;
    }

    @Nonnull
    @Override
    public IBlockState getExtendedState(@Nonnull IBlockState oldState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        state = state.withProperty(FLOW_DIRECTION, (float) getFlowDirection(world, pos));
        IBlockState[][] upBlockState = new IBlockState[3][3];
        float[][] height = new float[3][3];
        float[][] corner = new float[2][2];
        upBlockState[1][1] = world.getBlockState(pos.down(densityDir));
        height[1][1] = getFluidHeightForRender(world, pos, upBlockState[1][1]);
        if (height[1][1] == 1)
        {
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    corner[i][j] = 1;
                }
            }
        }
        else
        {
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    if (i != 1 || j != 1)
                    {
                        upBlockState[i][j] = world.getBlockState(pos.add(i - 1, 0, j - 1).down(densityDir));
                        height[i][j] = getFluidHeightForRender(world, pos.add(i - 1, 0, j - 1), upBlockState[i][j]);
                    }
                }
            }
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    corner[i][j] = getFluidHeightAverage(height[i][j], height[i][j + 1], height[i + 1][j], height[i + 1][j + 1]);
                }
            }
            //check for downflow above corners
            boolean n = isMergeableFluid(upBlockState[0][1]);
            boolean s = isMergeableFluid(upBlockState[2][1]);
            boolean w = isMergeableFluid(upBlockState[1][0]);
            boolean e = isMergeableFluid(upBlockState[1][2]);
            boolean nw = isMergeableFluid(upBlockState[0][0]);
            boolean ne = isMergeableFluid(upBlockState[0][2]);
            boolean sw = isMergeableFluid(upBlockState[2][0]);
            boolean se = isMergeableFluid(upBlockState[2][2]);
            if (nw || n || w)
            {
                corner[0][0] = 1;
            }
            if (ne || n || e)
            {
                corner[0][1] = 1;
            }
            if (sw || s || w)
            {
                corner[1][0] = 1;
            }
            if (se || s || e)
            {
                corner[1][1] = 1;
            }
        }

        for (int i = 0; i < 4; i++)
        {
            EnumFacing side = EnumFacing.byHorizontalIndex(i);
            BlockPos offset = pos.offset(side);
            boolean useOverlay = world.getBlockState(offset).getBlockFaceShape(world, offset, side.getOpposite()) == BlockFaceShape.SOLID;
            state = state.withProperty(SIDE_OVERLAYS[i], useOverlay);
        }

        state = state.withProperty(LEVEL_CORNERS[0], corner[0][0]);
        state = state.withProperty(LEVEL_CORNERS[1], corner[0][1]);
        state = state.withProperty(LEVEL_CORNERS[2], corner[1][1]);
        state = state.withProperty(LEVEL_CORNERS[3], corner[1][0]);
        return state;
    }

    @Override
    public float getFluidHeightForRender(IBlockAccess world, BlockPos adjPos, @Nonnull IBlockState upState)
    {
        IBlockState adjState = world.getBlockState(adjPos);

        // any adjacent above matching liquids merge to 1
        if (isMergeableFluid(upState))
        {
            return 1;
        }

        // adjacent mergeable liquids
        if (isMergeableFluid(adjState))
        {
            Block adjBlock = adjState.getBlock();
            if (adjBlock == this || adjBlock instanceof BlockLiquid)
                return super.getFluidHeightForRender(world, adjPos, upState);
            else
                return ((BlockFluidBase) adjBlock).getFluidHeightForRender(world, adjPos, upState);
        }

        // adjacent solid
        if (adjState.getMaterial().isSolid())
            return -1;

        // adjacent air or non-mergeable liquids
        return 0;
    }

    protected boolean isMergeableFluid(@Nonnull IBlockState blockstate)
    {
        return (blockstate.getMaterial() == getDefaultState().getMaterial()) && (blockstate.getMaterial().isLiquid());
    }

    protected boolean isBlockingSourceBlock(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return isMergeableFluid(state) && state.getValue(LEVEL) == 0;
    }
}
