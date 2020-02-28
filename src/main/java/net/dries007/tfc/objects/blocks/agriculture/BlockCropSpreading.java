/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.te.TECropSpreading;
import net.dries007.tfc.util.Helpers;

@Deprecated // For now, unused, needs design work
@ParametersAreNonnullByDefault
public abstract class BlockCropSpreading extends BlockCropTFC
{
    private static final int MAX_SPREAD_AGE = 16;

    public static BlockCropSpreading create(ICrop crop)
    {
        PropertyInteger property = getStagePropertyForCrop(crop);

        if (property == null)
            throw new IllegalStateException("Invalid growthstage property " + (crop.getMaxStage() + 1) + " for crop");

        return new BlockCropSpreading(crop)
        {
            @Override
            public PropertyInteger getStageProperty()
            {
                return property;
            }
        };
    }

    BlockCropSpreading(ICrop crop)
    {
        super(crop);

        setDefaultState(getBlockState().getBaseState().withProperty(WILD, false).withProperty(getStageProperty(), 0));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TECropSpreading tile = Helpers.getTE(worldIn, pos, TECropSpreading.class);
        if (tile != null)
        {
            tile.onPlaced();
        }
        super.onBlockAdded(worldIn, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TECropSpreading();
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        // The crop can grow up to 3 stages from "start"
        // i.e. plant one crop, start=0, so it will grow up to stage=3
        // each crop, once at max, will not grow anymore
        // instead, on growth tick, they will attempt to create a new crop at [age] - 1 in adjacent block
        // additionally, they will increase the growth of a random nearby crop by 1
        if (!worldIn.isRemote)
        {
            TECropSpreading tile = Helpers.getTE(worldIn, pos, TECropSpreading.class);
            if (tile != null)
            {
                int currentGrowthStage = state.getValue(getStageProperty());
                // Should the crop grow at all?
                if (tile.getBaseAge() + currentGrowthStage < MAX_SPREAD_AGE)
                {
                    if (currentGrowthStage < tile.getMaxGrowthStage())
                    {
                        // grow normally
                        worldIn.setBlockState(pos, state.withProperty(getStageProperty(), currentGrowthStage + 1));
                    }
                    else
                    {
                        // Pick a random nearby block to spawn another crop on
                        EnumFacing offset = EnumFacing.HORIZONTALS[rand.nextInt(4)];
                        BlockPos newPos = pos.offset(offset);

                        IBlockState newState = worldIn.getBlockState(newPos);
                        if (newState.getBlock() == this)
                        {
                            // Increase the growth max on the adjacent existing crop
                            TECropSpreading newTile = Helpers.getTE(worldIn, newPos, TECropSpreading.class);
                            if (newTile != null && newTile.getMaxGrowthStage() < currentGrowthStage)
                            {
                                newTile.setMaxGrowthStage(newTile.getMaxGrowthStage() + 1);
                            }
                        }
                        else if (newState.getBlock().isAir(newState, worldIn, newPos))
                        {
                            IBlockState stateDown = worldIn.getBlockState(newPos.down());
                            if (stateDown.getBlock().canSustainPlant(stateDown, worldIn, newPos.down(), EnumFacing.UP, this))
                            {
                                // Spawn a crop on the new block
                                worldIn.setBlockState(newPos, getDefaultState().withProperty(getStageProperty(), currentGrowthStage / 2));
                                TECropSpreading newTile = Helpers.getTE(worldIn, newPos, TECropSpreading.class);
                                if (newTile != null)
                                {
                                    newTile.setMaxGrowthStage(tile.getMaxGrowthStage() + 2);
                                    newTile.setBaseAge(tile.getBaseAge() + currentGrowthStage);
                                    newTile.setSeedPlant(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
