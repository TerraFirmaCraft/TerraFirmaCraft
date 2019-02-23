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
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

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

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (new BlockTallGrassTFC().canPlaceBlockAt(worldIn, pos))
        {
            new BlockDoubleTallGrassTFC().placeAt(worldIn, pos, state.getValue(TYPE), 2);
        }
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, EnumGrassType.byMetadata(meta));
    }

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
    public int tickRate(World worldIn)
    {
        return 3;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(world, pos)));
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            player.addStat(StatList.getBlockStats(this));
            spawnAsEntity(worldIn, pos, new ItemStack(new BlockTallGrassTFC(), 1, (state.getValue(TYPE)).getMeta()));
        }
        else if (!worldIn.isRemote && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            player.addStat(StatList.getBlockStats(this));
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 1));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this, 1, state.getBlock().getMetaFromState(state));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }

    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) { return true; }

    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(new BlockTallGrassTFC(), 1, (world.getBlockState(pos).getValue(TYPE)).getMeta()));
    }

    public EnumGrassType getBiomePlantType(World world, BlockPos pos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, pos);
        if (data == null || !data.isInitialized()) return EnumGrassType.STANDARD;

        final float temperature = ClimateTFC.getHeightAdjustedBiomeTemp(world, pos);
        final float rainfall = ChunkDataTFC.getRainfall(world, pos);

        if ((temperature > 15f && rainfall < 75f) || BlocksTFC.isSand(world.getBlockState(pos.down())))
        {
            return EnumGrassType.DESERT;
        }
        else if (temperature > 20f)
        {
            if (rainfall > 300f)
            {
                return EnumGrassType.LUSH;
            }
            else if (rainfall > 150f)
            {
                return EnumGrassType.STANDARD;
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
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        world.setBlockState(pos, this.blockState.getBaseState().withProperty(TYPE, getBiomePlantType(world, pos)));
        this.checkAndDropBlock(world, pos, state);
    }

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