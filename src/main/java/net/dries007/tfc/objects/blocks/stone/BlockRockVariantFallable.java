/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.Arrays;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.IFallingBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockVariantFallable extends BlockRockVariant implements IFallingBlock
{
    public static boolean isSupportingSideBlock(IBlockState state)
    {
        return state.isNormalCube() || (state.getBlock() instanceof BlockRockVariant && (((BlockRockVariant) state.getBlock()).getType() == Rock.Type.FARMLAND || ((BlockRockVariant) state.getBlock()).getType() == Rock.Type.PATH));
    }

    public BlockRockVariantFallable(Rock.Type type, Rock rock)
    {
        super(type, rock);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (!this.type.canFall()) return;
        if (rand.nextInt(16) != 0) return;
        if (shouldFall(worldIn, pos, pos))
        {
            double d0 = (float) pos.getX() + rand.nextFloat();
            double d1 = (double) pos.getY() - 0.05D;
            double d2 = (float) pos.getZ() + rand.nextFloat();
            worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (checkFalling(worldIn, pos, state))
        {
            onRockSlide(worldIn, pos);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        if (checkFalling(worldIn, pos, state))
        {
            onRockSlide(worldIn, pos);
        }
    }

    // What is the position that the block will fall at?
    @Nullable
    @Override
    public BlockPos getFallablePos(World world, BlockPos pos, boolean ignoreSupportChecks)
    {
        if (type.canFall() && shouldFall(world, pos, pos, ignoreSupportChecks))
        {
            return checkAreaClear(world, pos);
        }
        if (type.canFallHorizontal())
        {
            // Check if supported by at least two horizontals, or one on top
            if (isSupportingSideBlock(world.getBlockState(pos.up())))
            {
                return null;
            }

            EnumFacing[] faces = Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(x -> isSupportingSideBlock(world.getBlockState(pos.offset(x))))
                .toArray(EnumFacing[]::new);

            if (faces.length >= 2)
            {
                return null;
            }

            // Check if it can fall
            IBlockState originalState = world.getBlockState(pos);
            faces = Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(x -> shouldFall(world, pos.offset(x), pos, ignoreSupportChecks) && IFallingBlock.canFallThrough(world, pos.offset(x), originalState.getMaterial()))
                .toArray(EnumFacing[]::new);

            if (faces.length >= 1)
            {
                return checkAreaClear(world, pos.offset(faces[RANDOM.nextInt(faces.length)]));
            }
        }
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (type == Rock.Type.GRAVEL)
        {
            if (fortune > 3)
            {
                fortune = 3;
            }

            if (rand.nextInt(10 - fortune * 3) == 0)
            {
                return Items.FLINT;
            }
        }
        return super.getItemDropped(state, rand, fortune);
    }

    @Nullable
    private BlockPos checkAreaClear(World world, BlockPos pos)
    {
        // Check that there are no entities in the area, otherwise it would collide with them
        if (!world.getEntitiesWithinAABB(EntityFallingBlockTFC.class, new AxisAlignedBB(pos, pos.add(1, 1, 1))).isEmpty())
        {
            // If we can't fall due to a collision, wait for the block to move out of the way and try again later
            world.scheduleUpdate(pos, this, 20);
            return null;
        }
        return pos;
    }
}
