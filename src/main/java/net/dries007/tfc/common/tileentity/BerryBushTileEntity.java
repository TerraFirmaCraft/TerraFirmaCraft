package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public class BerryBushTileEntity extends TickCounterTileEntity
{
    private boolean isGrowing;
    private boolean harvested;
    private int useTicks;
    private int deathTicks;

    public BerryBushTileEntity()
    {
        this(TFCTileEntities.BERRY_BUSH.get());
    }

    protected BerryBushTileEntity(TileEntityType<?> type)
    {
        super(type);
        harvested = true;
        useTicks = 0;
        deathTicks = 0;
        isGrowing = true;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        isGrowing = nbt.getBoolean("isGrowing");
        harvested = nbt.getBoolean("harvested");
        useTicks = nbt.getInt("useTicks");
        deathTicks = nbt.getInt("deathTicks");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putBoolean("isGrowing", isGrowing);
        nbt.putBoolean("harvested", harvested);
        nbt.putInt("useTicks", useTicks);
        nbt.putInt("deathTicks", deathTicks);
        return super.save(nbt);
    }

    public void setGrowing(boolean growing)
    {
        isGrowing = growing;
    }

    public boolean isGrowing()
    {
        return isGrowing;
    }

    public void setHarvested(boolean isHarvested)
    {
        harvested = isHarvested;
    }

    public boolean isHarvested()
    {
        return harvested;
    }

    public void use()
    {
        useTicks++;
    }

    public void stopUsing()
    {
        useTicks = 0;
    }

    public boolean willStopUsing()
    {
        return useTicks > 20;
    }

    public void addDeath()
    {
        deathTicks++;
    }

    public int getDeath()
    {
        return deathTicks;
    }

    public boolean willDie()
    {
        return deathTicks > 15;
    }

    public void resetDeath()
    {
        deathTicks = 0;
    }
}
