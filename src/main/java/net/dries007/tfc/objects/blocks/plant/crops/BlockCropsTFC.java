package net.dries007.tfc.objects.blocks.plant.crops;

import java.util.EnumMap;
import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Agriculture.Crop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;

public class BlockCropsTFC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 7);

    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.375D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.625D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D)};


    private static final EnumMap<Crop, BlockCropsTFC> MAP = new EnumMap<>(Crop.class);

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

    public int getMaxStage()
    {
        return crop.maxStage;
    }

    public boolean isMaxStage(IBlockState state)
    {
        return ((Integer)state.getValue(this.getStageProperty())).intValue() >= this.getMaxStage();
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    { return true; }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    { return true; }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        int i = this.getStage(state) + 1;
        int j = this.getMaxStage();

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
        int stage = getStage(state);
        Random rand = world instanceof World ? ((World)world).rand : new Random();

        if (stage >= getMaxStage())
        {
            TerraFirmaCraft.getLog().info("getDrops " + ItemSeedsTFC.get(crop));

            int k = 3 + fortune;

            for (int i = 0; i < 3 + fortune; ++i)
            {
                if (rand.nextInt(2 * getMaxStage()) <= stage)
                {
                    drops.add(new ItemStack(ItemSeedsTFC.get(crop), 1, 0));
                }
            }
        }
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);

        if (false && !worldIn.isRemote) // Forge: NOP all this.
        {
            TerraFirmaCraft.getLog().info("dropBlockAsItemWithChance");

            int i = this.getStage(state);

            if (i >= this.getMaxStage())
            {
                int j = 3 + fortune;

                for (int k = 0; k < j; ++k)
                {
                    if (worldIn.rand.nextInt(2 * this.getMaxStage()) <= i)
                    {
                        spawnAsEntity(worldIn, pos, new ItemStack(ItemSeedsTFC.get(crop)));
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        TerraFirmaCraft.getLog().info("getItemDropped " + ItemSeedsTFC.get(crop));
        return this.isMaxStage(state) ? ItemSeedsTFC.get(crop) : ItemSeedsTFC.get(crop);

    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        TerraFirmaCraft.getLog().info("getItem");

        return new ItemStack(ItemSeedsTFC.get(crop));
    }
}
