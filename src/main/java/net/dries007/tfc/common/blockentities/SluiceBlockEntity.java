/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Sluiceable;
import net.dries007.tfc.util.loot.TFCLoot;

import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class SluiceBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_SOIL = 32;

    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.sluice");

    public static void serverTick(Level level, BlockPos pos, BlockState state, SluiceBlockEntity sluice)
    {
        if (!state.getValue(SluiceBlock.UPPER))
        {
            return; // only tick the top block
        }

        final State sluiceState = sluice.getRepresentativeState();
        if (sluiceState == State.NONE)
        {
            return; // Skip all updates
        }
        sluice.checkForLastTickSync();

        final boolean activeTick = level.getGameTime() % 20 == 0;

        // If the state is both, aka fully structured, then perform sluice operation
        if (sluiceState == State.BOTH && activeTick)
        {
            // Consume inputs, once per second
            Helpers.gatherAndConsumeItems(level, new AABB(-0.2f, 0.5f, -0.2f, 1.2f, 1.25f, 1.2f).move(pos), sluice.inventory, 0, MAX_SOIL - 1, 1);
        }
        if (sluiceState == State.BOTH && --sluice.ticksRemaining <= 0)
        {
            // Produce ores, every time the sluice finishes
            boolean itemUsed = false;
            for (ItemStack stack : Helpers.iterate(sluice.inventory))
            {
                if (stack.isEmpty())
                {
                    continue;
                }
                final Sluiceable sluiceable = Sluiceable.get(stack);
                if (sluiceable != null && level instanceof ServerLevel serverLevel)
                {
                    final var table = level.getServer().getLootTables().get(sluiceable.getLootTable());
                    final var builder = new LootContext.Builder(serverLevel)
                        .withRandom(level.random)
                        .withOptionalParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
                    final List<ItemStack> items = table.getRandomItems(builder.create(LootContextParamSets.EMPTY));
                    items.forEach(item -> Helpers.spawnItem(level, Vec3.atCenterOf(sluice.getWaterOutputPos()), item));
                }
                stack.setCount(0);
                itemUsed = true;
                break;
            }
            if (itemUsed) Helpers.playSound(level, sluice.getBlockPos(), SoundEvents.ITEM_PICKUP);
            sluice.ticksRemaining = TFCConfig.SERVER.sluiceTicks.get();
            sluice.markForSync();
        }
        if (!activeTick)
        {
            return;
        }

        if (sluiceState == State.INPUT_ONLY)
        {
            final Fluid fluid = level.getFluidState(sluice.getWaterInputPos()).getType();
            if (!fluid.isSame(Fluids.EMPTY) && sluice.isFluidValid(fluid))
            {
                final BlockPos outputPos = sluice.getWaterOutputPos();
                if (level.getBlockState(outputPos).getMaterial().isReplaceable())
                {
                    FluidHelpers.setSourceBlock(level, outputPos, fluid);
                }
            }
        }
        if (sluiceState == State.OUTPUT_ONLY)
        {
            // Assume that we created the output here, and we want to remove it.
            final BlockPos fluidOutputPos = sluice.getWaterOutputPos();
            final Fluid fluid = level.getFluidState(fluidOutputPos).getType();
            if (sluice.isFluidValid(fluid))
            {
                FluidHelpers.pickupFluid(level, fluidOutputPos, level.getBlockState(fluidOutputPos), IFluidHandler.FluidAction.EXECUTE, f -> {});
            }
        }
    }

    private int ticksRemaining;

    public SluiceBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SLUICE.get(), pos, state, defaultInventory(MAX_SOIL), NAME);
        ticksRemaining = 0;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        ticksRemaining = nbt.getInt("ticksRemaining");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("ticksRemaining", ticksRemaining);
        super.saveAdditional(nbt);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Sluiceable.get(stack) != null;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    public Direction getFacing()
    {
        return getBlockState().getValue(SluiceBlock.FACING);
    }

    public BlockPos getWaterOutputPos()
    {
        return SluiceBlock.getFluidOutputPos(getBlockState(), getBlockPos());
    }

    public BlockPos getWaterInputPos()
    {
        return getBlockPos().above().relative(getFacing().getOpposite());
    }

    @Nullable
    public Fluid getFlow()
    {
        assert level != null;

        final FluidState inputState = level.getFluidState(getWaterInputPos());
        final Fluid input = inputState.getType();
        final Fluid output = level.getFluidState(getWaterOutputPos()).getType();
        if (inputState.hasProperty(FlowingFluid.LEVEL) && inputState.getValue(FlowingFluid.LEVEL) == 1)
        {
            if (isFluidValid(input) && output.isSame(input))
            {
                return input;
            }
        }
        return null;
    }

    private State getRepresentativeState()
    {
        assert level != null;

        final FluidState inputState = level.getFluidState(getWaterInputPos());
        final Fluid input = inputState.getType();
        final Fluid output = level.getFluidState(getWaterOutputPos()).getType();

        final State state = isFluidValid(output) ? State.OUTPUT_ONLY : State.NONE;
        if (inputState.hasProperty(FlowingFluid.LEVEL) && inputState.getValue(FlowingFluid.LEVEL) == 1)
        {
            if (state == State.OUTPUT_ONLY && output.isSame(input))
            {
                return State.BOTH;
            }
            return State.INPUT_ONLY;
        }
        return state;
    }

    public boolean isFluidValid(Fluid fluid)
    {
        return Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_SLUICE);
    }

    enum State
    {
        NONE, INPUT_ONLY, OUTPUT_ONLY, BOTH;
    }
}
