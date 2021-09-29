package net.dries007.tfc.common.blocks.soil;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface HoeOverlayBlock
{
    void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text);
}
