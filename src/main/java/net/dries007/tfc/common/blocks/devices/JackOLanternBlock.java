package net.dries007.tfc.common.blocks.devices;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class JackOLanternBlock extends CarvedPumpkinBlock implements EntityBlockExtension
{
    private final ExtendedProperties properties;
    private final Supplier<? extends Block> dead;

    public JackOLanternBlock(ExtendedProperties properties, Supplier<? extends Block> dead)
    {
        super(properties.properties());
        this.properties = properties;
        this.dead = dead;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            final int jackTicks = TFCConfig.SERVER.jackOLanternTicks.get();
            if (counter.getTicksSinceUpdate() > jackTicks && jackTicks > 0)
            {
                level.setBlockAndUpdate(pos, Helpers.copyProperty(dead.get().defaultBlockState(), state, HorizontalDirectionalBlock.FACING));
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving)
    {
        // no golems
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
