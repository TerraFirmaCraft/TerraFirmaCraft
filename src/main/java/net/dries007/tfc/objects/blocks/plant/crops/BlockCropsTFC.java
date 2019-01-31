package net.dries007.tfc.objects.blocks.plant.crops;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
        return ((Integer)state.getValue(this.getStageProperty())).intValue() >= crop.getGrowthStages();
    }

    //public boolean isPickable() { return crop.isPickable(); }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    { return true; }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    { return true; }

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
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, 0);

        if (isMaxStage(state))
        {
            drops.add(new ItemStack(Items.EXPERIENCE_BOTTLE, 1, 0));
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

                EntityItem entityitem = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ItemSeedsTFC.get(crop)));

                worldIn.spawnEntity(entityitem);

                if (!(playerIn instanceof FakePlayer))
                {
                    entityitem.onCollideWithPlayer(playerIn);
                }
                return true;
            }
        }

        return false;
    }


}
