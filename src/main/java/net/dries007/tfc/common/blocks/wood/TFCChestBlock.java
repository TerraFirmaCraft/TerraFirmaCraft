package net.dries007.tfc.common.blocks.wood;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class TFCChestBlock extends ChestBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final String textureLocation;
    private final ExtendedProperties extendedProperties;


    public TFCChestBlock(ExtendedProperties properties, String textureLocation)
    {
        this(properties, textureLocation, TFCBlockEntities.CHEST::get); // () -> x.get() passes type check but casting (Sup<BEType<ChestBE>>) fails. Thanks compiler
    }

    public TFCChestBlock(ExtendedProperties properties, String textureLocation, Supplier<BlockEntityType<? extends ChestBlockEntity>> typeSupplier)
    {
        super(properties.properties(), typeSupplier);
        this.textureLocation = textureLocation;
        this.extendedProperties = properties;
    }

    public String getTextureLocation()
    {
        return textureLocation;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return extendedProperties;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return getExtendedProperties().newBlockEntity(pos, state);
    }

    @Nullable
    @SuppressWarnings("deprecation")
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType)
    {
        return getExtendedProperties().getTicker(level, state, givenType);
    }
}
