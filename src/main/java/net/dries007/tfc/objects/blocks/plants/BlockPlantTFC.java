/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockPlantTFC extends BlockBush implements IItemSize
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    /* Growth Stage of the plant, tied to the month of year */
    public final static PropertyInteger GROWTHSTAGE = PropertyInteger.create("stage", 0, 11);
    /* Time of day, used for rendering plants that bloom at different times */
    public final static PropertyInteger DAYPERIOD = PropertyInteger.create("dayperiod", 0, 3);
    static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
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

        plant.getOreDictName().ifPresent(name -> OreDictionaryHelper.register(this, name));

        this.plant = plant;
        this.setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.Month.MARCH.id()));
    }

    public int getDayPeriod()
    {
        return Math.floorDiv(CalenderTFC.getHourOfDay(), CalenderTFC.HOURS_IN_DAY / 4);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DAYPERIOD, getDayPeriod()).withProperty(AGE, meta).withProperty(GROWTHSTAGE, CalenderTFC.Month.MARCH.id());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
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
        int expectedStage = CalenderTFC.getMonthOfYear().id();
        int currentTime = state.getValue(DAYPERIOD);
        int expectedTime = getDayPeriod();

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
        world.setBlockState(pos, state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
        checkAndDropBlock(world, pos, state);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (!plant.getOreDictName().isPresent()) return Items.AIR;
        return Item.getItemFromBlock(this);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!plant.getOreDictName().isPresent() && !worldIn.isRemote && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(this, 1));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROWTHSTAGE, DAYPERIOD, AGE);
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
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && this.canSustainBush(soil);
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        if (plant.getIsClayMarking()) return BlocksTFC.isClay(state);
        if (plant.getPlantType() == Plant.PlantType.CACTUS) return BlocksTFC.isSand(state);
        if (plant.getPlantType() == Plant.PlantType.DESERT) return BlocksTFC.isSand(state);
        if (plant.getPlantType() == Plant.PlantType.DESERT_TALL_PLANT) return BlocksTFC.isSand(state);
        if (plant.getPlantType() == Plant.PlantType.DRY) return BlocksTFC.isSand(state) || BlocksTFC.isDryGrass(state);
        if (plant.getPlantType() == Plant.PlantType.DRY_TALL_PLANT) return BlocksTFC.isSand(state) || BlocksTFC.isDryGrass(state);
        if (plant.getPlantType() == Plant.PlantType.REED) return BlocksTFC.isSand(state) || BlocksTFC.isSoil(state);
        if (plant.getPlantType() == Plant.PlantType.TALL_REED) return BlocksTFC.isSand(state) || BlocksTFC.isSoil(state);
        if (plant.getPlantType() == Plant.PlantType.WATER_SEA) return BlocksTFC.isSand(state) || BlocksTFC.isSoilOrGravel(state);
        if (plant.getPlantType() == Plant.PlantType.WATER) return BlocksTFC.isSoilOrGravel(state);
        if (plant.getPlantType() == Plant.PlantType.TALL_WATER_SEA) return BlocksTFC.isSand(state) || BlocksTFC.isSoilOrGravel(state);
        if (plant.getPlantType() == Plant.PlantType.TALL_WATER) return BlocksTFC.isSoilOrGravel(state);
        if (plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER_SEA) return BlocksTFC.isSand(state) || BlocksTFC.isSoilOrGravel(state);
        if (plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER) return BlocksTFC.isSoilOrGravel(state);
        return BlocksTFC.isSoil(state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (CalenderTFC.getCalendarTime() > Math.multiplyExact(CalenderTFC.TICKS_IN_DAY, CalenderTFC.getDaysInMonth()) &&
            (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < plant.getGrowthTemp() - 5 || !plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up()))))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j > 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > plant.getGrowthTemp() && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if (j < 15)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }

        checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        if (state.getBlock() == this)
        {
            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) && plant.isValidTemp(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos)) && plant.isValidRain(ChunkDataTFC.getRainfall(worldIn, pos));
        }
        return this.canSustainBush(soil);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return PLANT_AABB.offset(state.getOffset(source, pos));
    }

    @Override
    @Nonnull
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        if (plant.getPlantType() == Plant.PlantType.DESERT) return EnumPlantType.Desert;
        if (plant.getPlantType() == Plant.PlantType.DESERT_TALL_PLANT) return EnumPlantType.Desert;
        if (plant.getPlantType() == Plant.PlantType.CACTUS) return EnumPlantType.Desert;
        if (plant.getPlantType() == Plant.PlantType.FLOATING) return EnumPlantType.Water;
        if (plant.getPlantType() == Plant.PlantType.FLOATING_SEA) return EnumPlantType.Water;
        return EnumPlantType.Plains;
    }

    @Nonnull
    public Plant.EnumPlantTypeTFC getPlantTypeTFC()
    {
        if (plant.getIsClayMarking()) return Plant.EnumPlantTypeTFC.Clay;
        if (plant.getPlantType() == Plant.PlantType.DRY) return Plant.EnumPlantTypeTFC.Dry;
        if (plant.getPlantType() == Plant.PlantType.DRY_TALL_PLANT) return Plant.EnumPlantTypeTFC.Dry;
        if (plant.getPlantType() == Plant.PlantType.REED) return Plant.EnumPlantTypeTFC.FreshBeach;
        if (plant.getPlantType() == Plant.PlantType.REED_SEA) return Plant.EnumPlantTypeTFC.SaltBeach;
        if (plant.getPlantType() == Plant.PlantType.TALL_REED) return Plant.EnumPlantTypeTFC.FreshBeach;
        if (plant.getPlantType() == Plant.PlantType.TALL_REED_SEA) return Plant.EnumPlantTypeTFC.SaltBeach;
        if (plant.getPlantType() == Plant.PlantType.WATER) return Plant.EnumPlantTypeTFC.FreshWater;
        if (plant.getPlantType() == Plant.PlantType.WATER_SEA) return Plant.EnumPlantTypeTFC.SaltWater;
        if (plant.getPlantType() == Plant.PlantType.TALL_WATER) return Plant.EnumPlantTypeTFC.FreshWater;
        if (plant.getPlantType() == Plant.PlantType.TALL_WATER_SEA) return Plant.EnumPlantTypeTFC.SaltWater;
        if (plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER) return Plant.EnumPlantTypeTFC.FreshWater;
        if (plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER_SEA) return Plant.EnumPlantTypeTFC.SaltWater;
        return Plant.EnumPlantTypeTFC.None;
    }
}
