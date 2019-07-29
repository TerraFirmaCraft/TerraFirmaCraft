/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TECropSpreading;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.util.agriculture.Crop.STAGE_8;

@ParametersAreNonnullByDefault
public class BlockCropSpreading extends BlockCropTFC
{
    private static final int MAX_SPREAD_AGE = 16;

    public BlockCropSpreading(ICrop crop)
    {
        super(crop);

        setDefaultState(getBlockState().getBaseState().withProperty(WILD, false).withProperty(STAGE_8, 0));
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
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
                int currentGrowthStage = state.getValue(STAGE_8);
                // Should the crop grow at all?
                if (tile.getBaseAge() + currentGrowthStage < MAX_SPREAD_AGE)
                {
                    if (currentGrowthStage < tile.getMaxGrowthStage())
                    {
                        // grow normally
                        worldIn.setBlockState(pos, state.withProperty(STAGE_8, currentGrowthStage + 1));
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
                                worldIn.setBlockState(newPos, getDefaultState().withProperty(STAGE_8, currentGrowthStage / 2));
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

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(WILD, meta > 7).withProperty(STAGE_8, meta & 7);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(STAGE_8) + (state.getValue(WILD) ? 8 : 0);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        // Seed dropping happens here because it needs to access the TE, which is gone by the time getDrops is called
        TECropSpreading tile = Helpers.getTE(worldIn, pos, TECropSpreading.class);
        if (tile != null && tile.isSeedPlant())
        {
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemSeedsTFC.get(crop)));
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, WILD, STAGE_8);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.clear();
        TECropSpreading tile = Helpers.getTE(world, pos, TECropSpreading.class);
        if (tile != null && tile.isSeedPlant())
        {
            drops.add(new ItemStack(ItemSeedsTFC.get(crop)));
        }
        // todo: adjust food drops based on player agriculture skill. For now just go with 2 for initial balance
        ItemStack foodDrop = crop.getFoodDrop(state.getValue(STAGE_8));
        if (!foodDrop.isEmpty())
        {
            foodDrop.setCount(2);
            drops.add(foodDrop);
        }
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
    public PropertyInteger getStageProperty()
    {
        return STAGE_8;
    }

    @Override
    protected ItemStack getDeathItem(TETickCounter cropTE)
    {
        if (crop instanceof TECropSpreading && ((TECropSpreading) cropTE).isSeedPlant())
        {
            return super.getDeathItem(cropTE);
        }
        return ItemStack.EMPTY;
    }
}
