package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CalendarClockBlockEntity extends TickableBlockEntity
{
    private float monthAngle;
    private float minuteAngle;
    private float hourAngle;
    private boolean needsUpdate = false;

    protected CalendarClockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public CalendarClockBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CALENDAR_CLOCK.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CalendarClockBlockEntity clock)
    {
        if (clock.needsUpdate)
        {
            clock.markForSync();
            clock.needsUpdate = false;
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CalendarClockBlockEntity clock)
    {
        if (level.getGameTime() % 20 == 0)
        {
        }
    }

    public int getRedstoneSignal()
    {
        return 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("minuteAngle", minuteAngle);
        tag.putFloat("hourAngle", hourAngle);
        tag.putFloat("monthAngle", monthAngle);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        hourAngle = tag.getFloat("hourAngle");
        minuteAngle = tag.getFloat("minuteAngle");
        monthAngle = tag.getFloat("monthAngle");
        needsUpdate = true;
    }
}
