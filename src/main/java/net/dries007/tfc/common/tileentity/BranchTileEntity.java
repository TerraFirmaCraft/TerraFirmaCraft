package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class BranchTileEntity extends TickCounterTileEntity
{
    private int saplings;

    public BranchTileEntity()
    {
        super(TFCTileEntities.BRANCH.get());
        saplings = 0;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        saplings = nbt.getInt("saplings");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("saplings", saplings);
        return super.save(nbt);
    }

    public int getSaplings()
    {
        return saplings;
    }

    public void addSaplings(int saplingsIn)
    {
        saplings += saplingsIn;
    }
}
