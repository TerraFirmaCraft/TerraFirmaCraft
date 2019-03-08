/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.CalendarTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockPlantTFC extends BlockBush implements IItemSize
{
    /* Growth Stage of the plant, tied to the month of year */
    public final static PropertyInteger GROWTHSTAGE = PropertyInteger.create("stage", 0, 11);
    /* Time of day, used for rendering plants that bloom at different times */
    public final static PropertyInteger DAYPERIOD = PropertyInteger.create("dayperiod", 0, 3);
    private static final Map<Plant, BlockPlantTFC> MAP = new HashMap<>();

    public static BlockPlantTFC get(Plant plant)
    {
        return MAP.get(plant);
    }

    protected final Plant plant;

    public BlockPlantTFC(Plant plant)
    {
        super(plant.getMaterial());
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");

        this.plant = plant;
        this.setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalendarTFC.Month.MARCH.id()));
    }

    public int getCurrentTime(World world)
    {
        return Math.floorDiv(Math.toIntExact(world.getWorldTime() % 24000), 6000);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(GROWTHSTAGE, CalendarTFC.getMonthOfYear().id());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(GROWTHSTAGE));
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(GROWTHSTAGE, CalendarTFC.getMonthOfYear().id());
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;
        int currentStage = state.getValue(GROWTHSTAGE);
        int expectedStage = CalendarTFC.getMonthOfYear().id();
        int currentTime = state.getValue(DAYPERIOD);
        int expectedTime = getCurrentTime(worldIn);

        if (currentTime != expectedTime)
        {
            worldIn.setBlockState(pos, state.withProperty(DAYPERIOD, expectedTime).withProperty(GROWTHSTAGE, currentStage));
        }
        if (currentStage != expectedStage && random.nextDouble() < 0.5)
        {
            worldIn.setBlockState(pos, state.withProperty(DAYPERIOD, expectedTime).withProperty(GROWTHSTAGE, expectedStage));
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
        world.setBlockState(pos, state.withProperty(DAYPERIOD, getCurrentTime(world)).withProperty(GROWTHSTAGE, CalendarTFC.getMonthOfYear().id()));
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROWTHSTAGE, DAYPERIOD);
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }

    public Plant getPlant()
    {
        return plant;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    public double getGrowthRate(World world, BlockPos pos)
    {
        if (world.isRainingAt(pos)) return ConfigTFC.GENERAL.plantGrowthRate * 2;
        else return ConfigTFC.GENERAL.plantGrowthRate;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        if (plant.getPlantType() == Plant.PlantType.DESERT) return BlocksTFC.isSand(state);
        return BlocksTFC.isSoil(state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;
        if (!canBlockStay(worldIn, pos, state))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        if (state.getBlock() == this)
        {
            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) && plant.isValidTempRain(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos), ChunkDataTFC.getRainfall(worldIn, pos));
        }
        return this.canSustainBush(soil);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
    }

    @Override
    @Nonnull
    public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos.down());
        if (plant.getPlantType() == Plant.PlantType.DESERT && BlocksTFC.isSand(iblockstate))
        {
            return EnumPlantType.Desert;
        }
        else
        {
            return EnumPlantType.Plains;
        }

    }
}
