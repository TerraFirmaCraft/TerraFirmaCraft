package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tileentity.TileEntityType;

public class FarmlandTileEntity extends TFCTileEntity
{
    private BlockState dirt;

    public FarmlandTileEntity()
    {
        this(TFCTileEntities.FARMLAND.get());
    }

    protected FarmlandTileEntity(TileEntityType<?> type)
    {
        super(type);

        dirt = Blocks.AIR.defaultBlockState();
    }

    public BlockState getDirt()
    {
        return dirt;
    }

    public void setDirt(BlockState dirt)
    {
        this.dirt = dirt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        dirt = BlockState.CODEC.decode(NBTDynamicOps.INSTANCE, nbt.get("dirt")).getOrThrow(false, LOGGER::error).getFirst();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("dirt", NBTDynamicOps.INSTANCE.withEncoder(BlockState.CODEC).apply(dirt).getOrThrow(false, LOGGER::error));
        return super.save(nbt);
    }
}
