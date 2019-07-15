/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockBerryBush extends Block
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4); //last one is for fruits

    private static final Map<IBerryBush, BlockBerryBush> MAP = new HashMap<>();

    public static BlockBerryBush get(IBerryBush bush)
    {
        return MAP.get(bush);
    }

    public final IBerryBush bush;

    public BlockBerryBush(IBerryBush bush)
    {
        super(Material.LEAVES, Material.LEAVES.getMaterialMapColor());
        this.bush = bush;
        if (MAP.put(bush, this) != null) throw new IllegalStateException("There can only be one.");
        Blocks.FIRE.setFireInfo(this, 30, 60);
        setTickRandomly(true);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote)
        {
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                float temp = ClimateTFC.getTemp(world, pos);
                float rainfall = ChunkDataTFC.getRainfall(world, pos);
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > bush.getGrowthTime() && bush.isValidForGrowth(temp, rainfall))
                {
                    int stage = world.getBlockState(pos).getValue(STAGE);
                    if(stage < 3)
                    {
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, ++stage));
                    }
                    if(stage == 3 && bush.isHarvestMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
                    {
                        //Fruiting
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, 4));
                    }
                    te.resetCounter();
                }
            }
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
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
        return new TETickCounter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.getBlockState(pos).getValue(STAGE) == 4)
        {
            if (!worldIn.isRemote)
            {
                Helpers.spawnItemStack(worldIn, pos, bush.getFoodDrop());
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(STAGE, 3));
                TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
                if (te != null)
                {
                    te.resetCounter();
                }
            }
            return true;
        }
        return false;
    }
}
