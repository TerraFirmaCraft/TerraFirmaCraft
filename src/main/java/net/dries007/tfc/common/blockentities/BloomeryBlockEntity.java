package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.bloomery");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {

    }

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, defaultInventory(1), NAME);
    }
}
