/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.world.classic.ClimateTFC;

public class BlockDoubleTallGrassTFC extends BlockTallGrassTFC implements net.minecraftforge.common.IShearable
{
    public static final PropertyEnum<BlockDoubleTallGrassTFC.EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);

    public BlockDoubleTallGrassTFC()
    {
        super();
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumGrassType.STANDARD).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            boolean flag = state.getValue(HALF) == EnumBlockHalf.UPPER;
            BlockPos blockpos = flag ? pos : pos.up();
            BlockPos blockpos1 = flag ? pos.down() : pos;
            Block block = (Block) (flag ? this : worldIn.getBlockState(blockpos).getBlock());
            Block block1 = (Block) (flag ? worldIn.getBlockState(blockpos1).getBlock() : this);

            if (!flag) this.dropBlockAsItem(worldIn, pos, state, 0); //Forge move above the setting to air.

            if (block == this)
            {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            }

            if (block1 == this)
            {
                worldIn.setBlockState(blockpos1, BlocksTFC.TALL_GRASS.getBlockState().getBaseState().withProperty(TYPE, getBiomePlantType(worldIn, blockpos1)));
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getBlock() != this)
            return super.canBlockStay(worldIn, pos, state); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
        if (state.getValue(HALF) == EnumBlockHalf.UPPER)
        {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.up());
            return iblockstate.getBlock() == this && super.canBlockStay(worldIn, pos, iblockstate);
        }
    }

    public void placeAt(World worldIn, BlockPos lowerPos, EnumGrassType type, int flags)
    {
        worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(TYPE, type), flags);
        worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER), flags);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(HALF) == EnumBlockHalf.UPPER)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(TYPE, iblockstate.getValue(TYPE));
            }
        }

        return state;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER), 2);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean canShrink(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 15 || ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 35 || !worldIn.canSeeSky(pos);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
    }

    @Override
    public void shrink(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (state.getValue(HALF) == EnumBlockHalf.UPPER)
        {
            worldIn.setBlockToAir(pos);
        }
        else if (BlocksTFC.TALL_GRASS.canPlaceBlockAt(worldIn, pos))
        {
            worldIn.setBlockState(pos, BlocksTFC.TALL_GRASS.getBlockState().getBaseState().withProperty(TYPE, getBiomePlantType(worldIn, pos)));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER) : this.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(TYPE, EnumGrassType.byMetadata(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HALF) == EnumBlockHalf.UPPER ? 8 : (state.getValue(TYPE)).getMeta();
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (canShrink(worldIn, pos, state, worldIn.isRemote) && random.nextDouble() < getGrowthRate(worldIn))
        {
            shrink(worldIn, random, pos, state);
        }
        else
        {
            worldIn.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(worldIn, pos)).withProperty(HALF, state.getValue(HALF)));
        }

        this.updateTick(worldIn, pos, state, random);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(world, pos)).withProperty(HALF, state.getValue(HALF)));
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            if (state.getValue(HALF) == EnumBlockHalf.UPPER)
            {
                spawnAsEntity(worldIn, pos, new ItemStack(BlocksTFC.TALL_GRASS, 1));
            }
            else
            {
                spawnAsEntity(worldIn, pos, new ItemStack(BlocksTFC.DOUBLE_TALL_GRASS, 1));
            }
        }
        else if (!worldIn.isRemote && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            if (state.getValue(HALF) == EnumBlockHalf.UPPER)
            {
                spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 1));
            }
            else
            {
                spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 2));
            }
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {HALF, TYPE});
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        if (world.getBlockState(pos).getValue(HALF) == EnumBlockHalf.UPPER)
        {
            return NonNullList.withSize(1, new ItemStack(BlocksTFC.TALL_GRASS, 1));
        }
        else
        {
            return NonNullList.withSize(1, new ItemStack(BlocksTFC.DOUBLE_TALL_GRASS, 1));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    public static enum EnumBlockHalf implements IStringSerializable
    {
        UPPER,
        LOWER;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this == UPPER ? "upper" : "lower";
        }
    }
}