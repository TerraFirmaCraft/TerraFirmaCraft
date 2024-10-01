/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.QuernBlock;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.network.RotationOwner;

public class QuernBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOT_HANDSTONE = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final int MANUAL_TICKS = 90;
    private static final float MANUAL_PROGRESS_PER_TICK = 1f / MANUAL_TICKS; // Exactly 90 ticks
    private static final float NETWORK_PROGRESS_PER_SPEED_PER_TICK = MANUAL_PROGRESS_PER_TICK / Mth.TWO_PI; // progress / radian

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuernBlockEntity quern)
    {
        final ServerLevel serverLevel = (ServerLevel) level;
        final @Nullable RotationOwner owner = quern.getConnectedNetworkOwner(level);

        quern.checkForLastTickSync();
        if (quern.needsStateUpdate)
        {
            quern.updateBlockState();
            if (owner != null && quern.progress == 0)
            {
                // If there is a rotation owner, re-check the recipe, and if so, set the progress to active
                quern.tryStartGrinding();
            }
        }

        if (tickProgress(level, quern, owner))
        {
            quern.completeRecipeAndUpdateInventory(serverLevel, pos);
            if (owner != null)
            {
                // Immediately start grinding with another recipe, if we are connected to a network.
                quern.tryStartGrinding();
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, QuernBlockEntity quern)
    {
        tickProgress(level, quern, quern.getConnectedNetworkOwner(level));
    }

    private static boolean tickProgress(Level level, QuernBlockEntity quern, @Nullable RotationOwner owner)
    {
        final float rotationSpeed = owner != null
            ? NETWORK_PROGRESS_PER_SPEED_PER_TICK * RotationOwner.getRotationSpeed(owner)
            : quern.powered
                ? MANUAL_PROGRESS_PER_TICK
                : 0f;

        if (quern.progress > 0 && rotationSpeed > 0)
        {
            quern.progress -= rotationSpeed;
            if (quern.progress <= 0)
            {
                quern.powered = false;
                quern.progress = 0;
                return true;
            }
        }
        return false;
    }

    private static void sendParticle(ServerLevel level, BlockPos pos, ItemStack item, int count)
    {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + 0.5D, pos.getY() + 0.875D, pos.getZ() + 0.5D, count, Helpers.triangle(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.triangle(level.random) / 2.0D, 0.15f);
    }

    private boolean needsStateUpdate = false;
    private boolean powered = false; // If true, the quern was hand powered and will continue grinding until the item is complete
    private float progress = 0; // [1, 0] indicating the progress grinding the current item

    public QuernBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.QUERN.get(), pos, state, defaultInventory(3));

        if (TFCConfig.SERVER.quernEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(SLOT_INPUT, SLOT_HANDSTONE), Direction.Plane.HORIZONTAL)
                .on(new PartialItemHandler(inventory).extract(SLOT_OUTPUT), Direction.DOWN);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsStateUpdate = true;
        markForSync();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return slot == SLOT_HANDSTONE ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return slot != SLOT_HANDSTONE || Helpers.isItem(stack.getItem(), TFCTags.Items.QUERN_HANDSTONES);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        progress = tag.getFloat("progress");
        powered = tag.getBoolean("powered");
        needsStateUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("progress", progress);
        tag.putBoolean("powered", powered);
    }

    /**
     * @return {@code true} if the quern handle can be interacted with, because it has no connected network, and it is not currently powered
     * by a previous interaction
     */
    public boolean canInteractWithHandle()
    {
        assert level != null;
        return !powered && getConnectedNetworkOwner(level) == null;
    }

    public boolean hasHandstone()
    {
        return !inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty();
    }

    /**
     * Attempts to start grinding manually. Returns {@code true} if it did.
     */
    public boolean startGrinding()
    {
        assert level != null;

        if (tryStartGrinding())
        {
            powered = true; // This was manual
            level.playSound(null, worldPosition, TFCSounds.QUERN_DRAG.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16)); // And play the sound
            return true;
        }
        return false;
    }

    public void setHandstoneFromOutsideWorld()
    {
        inventory.setStackInSlot(SLOT_HANDSTONE, new ItemStack(TFCItems.HANDSTONE.get()));
    }

    /**
     * @param owner The owner identified by {@link #getConnectedNetworkOwner}
     * @return The current rotation angle, including partial tick, of the quern at this position. Will use either network, or non-network rotation
     * as necessary.
     */
    public float getRotationAngle(@Nullable RotationOwner owner, float partialTick)
    {
        return owner != null
            ? RotationOwner.getRotationAngle(owner, partialTick)
            : Mth.TWO_PI * (1f - progress);
    }

    /**
     * @return A network owner, connected above this quern, if it exists. This queries the world, so it is viable on both sides.
     */
    @Nullable
    public RotationOwner getConnectedNetworkOwner(Level level)
    {
        return level.getBlockEntity(worldPosition.above()) instanceof RotationOwner owner
            && owner.getRotationNode().connections().contains(Direction.DOWN)
                ? owner
                : null;
    }

    private boolean tryStartGrinding()
    {
        assert level != null;

        final ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
        if (!inputStack.isEmpty() && hasHandstone())
        {
            final QuernRecipe recipe = QuernRecipe.getRecipe(inputStack);
            if (recipe != null && recipe.matches(inputStack))
            {
                progress = 1f;
                markForSync();
                return true;
            }
        }
        return false;
    }

    private void updateBlockState()
    {
        assert level != null;

        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(QuernBlock.HAS_HANDSTONE, hasHandstone()));
        needsStateUpdate = false;
    }

    private void completeRecipeAndUpdateInventory(ServerLevel level, BlockPos pos)
    {
        final ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
        if (!inputStack.isEmpty())
        {
            final QuernRecipe recipe = QuernRecipe.getRecipe(inputStack);
            if (recipe != null && recipe.matches(inputStack))
            {
                ItemStack outputStack = recipe.assemble(inputStack);
                outputStack = Helpers.mergeInsertStack(inventory, SLOT_OUTPUT, outputStack);
                if (!outputStack.isEmpty() && !level.isClientSide)
                {
                    Helpers.spawnItem(level, pos, outputStack);
                }

                // Shrink the input stack after the recipe is done assembling
                inputStack.shrink(1);

                // Play "clunk" sound
                Helpers.playSound(level, pos, SoundEvents.ARMOR_STAND_FALL);

                // Damage the handstone, possibly breaking it
                final ItemStack handstone = inventory.getStackInSlot(SLOT_HANDSTONE);
                final ItemStack undamagedHandstoneStack = handstone.copy();

                Helpers.damageItem(handstone, level);

                if (handstone.isEmpty())
                {
                    Helpers.playSound(level, pos, SoundEvents.STONE_BREAK);
                    Helpers.playSound(level, pos, SoundEvents.ITEM_BREAK);
                    sendParticle(level, pos, undamagedHandstoneStack, 15);
                }

                // Update slots, mark for state update, and also marks for sync
                setAndUpdateSlots(SLOT_HANDSTONE);
            }
        }
    }
}
