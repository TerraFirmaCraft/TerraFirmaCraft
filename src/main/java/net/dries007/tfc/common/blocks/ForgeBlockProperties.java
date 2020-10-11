package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.tileentity.TileEntity;

public class ForgeBlockProperties
{
    private final AbstractBlock.Properties properties;

    @Nullable
    private Supplier<? extends TileEntity> tileEntityFactory;
    private int flammability;
    private int fireSpreadSpeed;

    public ForgeBlockProperties(AbstractBlock.Properties properties)
    {
        this.properties = properties;

        tileEntityFactory = null;
        flammability = 0;
        fireSpreadSpeed = 0;
    }

    public ForgeBlockProperties tileEntity(Supplier<? extends TileEntity> tileEntityFactory)
    {
        this.tileEntityFactory = tileEntityFactory;
        return this;
    }

    public ForgeBlockProperties flammable(int flammability, int fireSpreadSpeed)
    {
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
        return this;
    }

    public AbstractBlock.Properties properties()
    {
        return properties;
    }

    boolean hasTileEntity()
    {
        return tileEntityFactory != null;
    }

    @Nullable
    TileEntity createTileEntity()
    {
        return tileEntityFactory != null ? tileEntityFactory.get() : null;
    }

    int getFlammability()
    {
        return flammability;
    }

    int getFireSpreadSpeed()
    {
        return fireSpreadSpeed;
    }
}
