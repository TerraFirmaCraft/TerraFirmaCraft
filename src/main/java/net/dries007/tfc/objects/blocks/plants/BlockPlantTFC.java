/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
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

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.CalenderTFC;

public class BlockPlantTFC extends BlockBush implements IItemSize
{
    public final static PropertyInteger GROWTHSTAGE = PropertyInteger.create("stage", 0, 11);
    private static final Map<Plant, EnumMap<Plant.PlantType, BlockPlantTFC>> TABLE = new HashMap<>();

    public final Plant plant;
    public final Plant.PlantType type;

    public BlockPlantTFC(Plant plant, Plant.PlantType type)
    {
        super();

        if (!TABLE.containsKey(plant))
            TABLE.put(plant, new EnumMap<>(Plant.PlantType.class));
        TABLE.get(plant).put(type, this);

        this.plant = plant;
        this.type = type;
        this.setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
    }

    public static BlockPlantTFC get(Plant plant, Plant.PlantType type)
    {
        return TABLE.get(plant).get(type);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(GROWTHSTAGE));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this,  new IProperty[] {GROWTHSTAGE});
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
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
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
    protected boolean canSustainBush(IBlockState state)
    {
        return BlocksTFC.isSoil(state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Plains;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.randomTick(worldIn, pos, state, random);
        // Check the current time against the current stage
        int currentStage = state.getValue(GROWTHSTAGE);
        int expectedStage = CalenderTFC.getMonthOfYear().id();
        // If it is late and should grow, and some randomness
        if (currentStage != expectedStage && random.nextDouble() < 0.5)
        {
            // Update to the next stage
            worldIn.setBlockState(pos, state.withProperty(GROWTHSTAGE, expectedStage));
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
    }
}
