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
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class BlockTallGrassTFC extends BlockBush implements IGrowable, net.minecraftforge.common.IShearable, IItemSize
{
    public static final PropertyEnum<EnumGrassType> TYPE = PropertyEnum.<EnumGrassType>create("type", EnumGrassType.class);
    protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    public BlockTallGrassTFC()
    {
        super();
        this.setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumGrassType.STANDARD));
    }

    public double getGrowthRate()
    {
        return ConfigTFC.GENERAL.grassGrowthRate;
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return BlocksTFC.DOUBLE_TALL_GRASS.canPlaceBlockAt(worldIn, pos) && ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 20 && ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 35;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        BlocksTFC.DOUBLE_TALL_GRASS.placeAt(worldIn, pos, BlocksTFC.DOUBLE_TALL_GRASS.getBiomePlantType(worldIn, pos), 2);
    }

    public boolean canShrink(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 0;
    }

    public void shrink(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockToAir(pos);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, EnumGrassType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(TYPE)).getMeta();
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (canGrow(worldIn, pos, state, worldIn.isRemote) && random.nextDouble() < getGrowthRate())
        {
            grow(worldIn, random, pos, state);
        }
        else if (canShrink(worldIn, pos, state, worldIn.isRemote) && random.nextDouble() < getGrowthRate())
        {
            shrink(worldIn, random, pos, state);
        }
        else
        {
            worldIn.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(worldIn, pos)));
        }

        this.updateTick(worldIn, pos, state, random);
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 3;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(world, pos)));
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTFC.TALL_GRASS, 1));
        }
        else if (!worldIn.isRemote && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 1));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this, 1);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) { return true; }

    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(BlocksTFC.TALL_GRASS, 1));
    }

    public EnumGrassType getBiomePlantType(World world, BlockPos pos)
    {
        final float temperature = ClimateTFC.getHeightAdjustedBiomeTemp(world, pos);
        final float rainfall = ChunkDataTFC.getRainfall(world, pos);

        if (temperature > 15f)
        {
            if (rainfall > 350f)
            {
                return EnumGrassType.LUSH;
            }
            else if (rainfall > 150f)
            {
                return EnumGrassType.STANDARD;
            }
            else if (rainfall < 75f || BlocksTFC.isSand(world.getBlockState(pos.down())))
            {
                return EnumGrassType.DESERT;
            }
        }
        return EnumGrassType.SPARSE;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return BlocksTFC.isSoil(state) || BlocksTFC.isSand(state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TALL_GRASS_AABB;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos.down());
        if (BlocksTFC.isSand(iblockstate))
        {
            return EnumPlantType.Desert;
        }
        else
        {
            return EnumPlantType.Plains;
        }
    }

    public enum EnumGrassType implements IStringSerializable
    {
        SPARSE(0, "sparse_grass"),
        STANDARD(1, "standard_grass"),
        DESERT(2, "desert_grass"),
        LUSH(3, "lush_grass");

        private static final EnumGrassType[] META_LOOKUP = new EnumGrassType[values().length];

        static
        {
            for (EnumGrassType blocktallgrass$enumtype : values())
            {
                META_LOOKUP[blocktallgrass$enumtype.getMeta()] = blocktallgrass$enumtype;
            }
        }

        public static EnumGrassType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        private final int meta;
        private final String name;

        private EnumGrassType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}