package net.dries007.tfc.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendar;

public class EggBlock extends TurtleEggBlock implements IForgeBlockProperties
{
    private final Supplier<? extends EntityType<? extends TurtleEntity>> type;
    private final ForgeBlockProperties properties;

    public EggBlock(ForgeBlockProperties properties, Supplier<? extends EntityType<? extends TurtleEntity>> type)
    {
        super(properties.properties());
        this.type = type;
        this.properties = properties;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);

        if (te != null && te.getTicksSinceUpdate() > (long) Calendar.TICKS_IN_DAY * TFCConfig.SERVER.eggDays.get() && onSand(world, pos))
        {
            int i = state.getValue(HATCH);
            if (i < 2)
            {
                world.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + rand.nextFloat() * 0.2F);
                world.setBlock(pos, state.setValue(HATCH, i + 1), 2);
            }
            else
            {
                world.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + rand.nextFloat() * 0.2F);
                world.removeBlock(pos, false);

                for (int j = 0; j < state.getValue(EGGS); ++j)
                {
                    world.levelEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getId(state));
                    TurtleEntity entity = type.get().create(world);
                    Validate.notNull(entity);
                    entity.setAge(-24000);
                    entity.setHomePos(pos);
                    entity.moveTo(pos.getX() + 0.3D + j * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
                    world.addFreshEntity(entity);
                }
            }
        }

    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    private boolean shouldUpdateHatchLevel(World world)
    {
        final float dayTime = world.getTimeOfDay(1.0F);
        return dayTime < 0.69D && dayTime > 0.65D || world.random.nextInt(500) == 0;
    }
}
