/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.FakePlayer;

import net.dries007.tfc.api.types.Crop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.te.TECropsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalenderTFC;

@ParametersAreNonnullByDefault

public class BlockCropsTFC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 7);

    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.375D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.625D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D)};

    private static final Map<Crop, BlockCropsTFC> MAP = new HashMap<>();


    //private static final EnumMap<Crop, BlockCropsTFC> MAP = new EnumMap<>(Crop.class);

    public static BlockCropsTFC get(Crop crop)
    {
        return MAP.get(crop);
    }


    public final Crop crop;

    public BlockCropsTFC(Crop crop)
    {
        super(Material.PLANTS);
        if (MAP.put(crop, this) != null) throw new IllegalStateException("There can only be one.");
        this.crop = crop;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TECropsTFC te = Helpers.getTE(worldIn, pos, TECropsTFC.class);
        if (te != null) te.onPlaced();
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPS_AABB[(state.getValue(this.getStageProperty())).intValue()];
    }


    protected PropertyInteger getStageProperty()
    {
        return STAGE;
    }

    protected int getStage(IBlockState state)
    {
        return (state.getValue(this.getStageProperty())).intValue();
    }

    public IBlockState withStage(int stage)
    {
        return this.getDefaultState().withProperty(this.getStageProperty(), Integer.valueOf(stage));
    }

    public boolean isMaxStage(IBlockState state)
    {
        return ((Integer)state.getValue(this.getStageProperty())).intValue() == crop.getGrowthStages();
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    { return true; }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (isMaxStage(state))
        {
            return false;
        }
        return true; }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        int i = this.getStage(state) + 1;
        int j = crop.getGrowthStages();

        if (i > j)
        {
            i = j;
        }

        worldIn.setBlockState(pos, this.withStage(i), 2);
    }

    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
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
        return new TECropsTFC();
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);

        if (!world.isRemote)
        {
            TECropsTFC te = Helpers.getTE(world, pos, TECropsTFC.class);
            if (te != null)
            {
                long hours = te.getHoursSincePlaced();
                if (hours > (((getStage(state) *crop.getMinStageGrowthTime()) + crop.getMinStageGrowthTime()) * CalenderTFC.HOURS_IN_DAY))
                {
                    grow(world, random, pos, state);
                }
            }
        }
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, 0);

        if (isMaxStage(state))
        {
            drops.add(new ItemStack(ItemFoodTFC.get(crop.getFoodItem())));
        }
        if (crop.getFoodItemEarly() != null)
        {
            if (state.getValue(this.getStageProperty()).intValue() == (crop.getGrowthStages() - 1))
            {
                drops.add(new ItemStack(ItemFoodTFC.get(crop.getFoodItemEarly())));
            }
        }

        drops.add(new ItemStack(ItemSeedsTFC.get(crop), 1, 0));

    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (crop.isPickable())
        {
            if (isMaxStage(state))
            {
                if (worldIn.isRemote)
                {
                    return true;
                }

                worldIn.setBlockState(pos, this.getDefaultState().withProperty(STAGE, Integer.valueOf(crop.getGrowthStages() - 2)), 3);

                EntityItem entityitem = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ItemFoodTFC.get(crop.getFoodItem())));

                worldIn.spawnEntity(entityitem);

                if (!(playerIn instanceof FakePlayer))
                {
                    entityitem.onCollideWithPlayer(playerIn);
                }
                return true;
            }
            if (crop.getFoodItemEarly() != null)
            {
                if (worldIn.isRemote)
                {
                    return true;
                }

                if (state.getValue(this.getStageProperty()).intValue() == (crop.getGrowthStages() - 1))
                {
                    worldIn.setBlockState(pos, this.getDefaultState().withProperty(STAGE, Integer.valueOf(crop.getGrowthStages() - 3)), 3);

                    EntityItem entityitem = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ItemFoodTFC.get(crop.getFoodItemEarly())));

                    worldIn.spawnEntity(entityitem);

                    if (!(playerIn instanceof FakePlayer))
                    {
                        entityitem.onCollideWithPlayer(playerIn);
                    }
                    return true;
                }
            }
        }

        return false;
    }


}
