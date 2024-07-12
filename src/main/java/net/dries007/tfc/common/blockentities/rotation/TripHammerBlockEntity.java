/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import com.mojang.math.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.items.HammerItem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;

import static net.dries007.tfc.TerraFirmaCraft.*;


public class TripHammerBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, TripHammerBlockEntity hammer)
    {
        if (hammer.cooldownTicks-- > 0)
        {
            return;
        }
        final ItemStack item = hammer.inventory.getStackInSlot(0);
        if (item.isEmpty())
            return;
        final Rotation rotation = hammer.getRotation();
        if (rotation != null)
        {
            final float angle = hammer.getRealRotationDegrees(rotation, 1f);
            if (angle > 180f && angle < 183f)
            {
                if (rotation.positiveDirection() != state.getValue(TripHammerBlock.FACING).getClockWise())
                {
                    level.destroyBlock(pos, true);
                    return;
                }

                final BlockPos anvilPos = pos.relative(state.getValue(TripHammerBlock.FACING));
                // instanceof AnvilBlock is a check that this isn't a rock anvil block, which are incompatible
                if (level.getBlockEntity(anvilPos) instanceof AnvilBlockEntity anvil && level.getBlockState(anvilPos).getBlock() instanceof AnvilBlock)
                {
                    if (!anvil.workRemotely(ForgeStep.HIT_LIGHT, 12, true))
                    {
                        if (anvil.getCapability(Capabilities.ITEM).map(inv -> !inv.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN).isEmpty()).orElse(false))
                        {
                            level.playSound(null, pos, TFCSounds.ANVIL_HIT.get(), SoundSource.BLOCKS, 0.4f, 0.2f);
                        }
                    }
                    else
                    {
                        Helpers.damageItem(item, 1);
                        anvil.markForSync();
                    }
                    hammer.cooldownTicks = Mth.ceil(0.8f * Mth.TWO_PI / rotation.positiveSpeed());
                }
            }
        }
    }

    private int cooldownTicks = 10;

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.trip_hammer");

    public TripHammerBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.TRIP_HAMMER.get(), pos, state);
    }

    public TripHammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof HammerItem hammer && hammer.getMetalTexture() != null;
    }

    public float getRealRotationDegrees(Rotation rotation, float partialTick)
    {
        return Constants.RAD_TO_DEG * rotation.angle(partialTick);
    }

    @Nullable
    public Rotation getRotation()
    {
        assert level != null;
        if (level.getBlockEntity(worldPosition.above()) instanceof BladedAxleBlockEntity axle)
        {
            return axle.getRotationNode().rotation();
        }
        return null;
    }
}
