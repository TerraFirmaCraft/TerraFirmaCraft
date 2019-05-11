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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.IFallingBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockVariantFallable extends BlockRockVariant implements IFallingBlock
{
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
        if (shouldFall(worldIn, pos))
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
        checkFalling(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        checkFalling(worldIn, pos, state);
    }

    // What is the position that the block will fall at?
    @Nullable
    @Override
    public BlockPos getFallablePos(World world, BlockPos pos)
    {
//        for (EnumFacing f : EnumFacing.HORIZONTALS)
//            TerraFirmaCraft.getLog().info("Can it fall: " + f + "? " + shouldFall(world, pos.offset(f)));
        if (type.canFall() && shouldFall(world, pos))
            return pos;
        if (type.canFallHorizontal())
        {
            // Check if supported
            EnumFacing[] faces = Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(x -> world.getBlockState(pos.offset(x)).isOpaqueCube())
                .toArray(EnumFacing[]::new);
            if (faces.length >= 2)
            {
//                TerraFirmaCraft.getLog().info("Defeated by the supported blocks");
                return null;
            }

            // Check if it can fall
            faces = Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(x -> shouldFall(world, pos.offset(x)) && canFallThrough(world.getBlockState(pos.offset(x))))
                .toArray(EnumFacing[]::new);

            if (faces.length >= 1)
                return pos.offset(faces[(int) (Math.random() * faces.length)]);
        }
        return null;
    }
}
