/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class QuernBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOT_HANDSTONE = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.quern");

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuernBlockEntity quern)
    {
        if (quern.rotationTimer > 0)
        {
            quern.rotationTimer--;
            if (quern.rotationTimer == 0)
            {
                quern.finishGrinding();
                Helpers.playSound(level, pos, SoundEvents.ARMOR_STAND_FALL);
                Helpers.damageItem(quern.inventory.getStackInSlot(SLOT_HANDSTONE), 1);

                if (!quern.hasHandstone())
                {
                    Helpers.playSound(level, pos, SoundEvents.STONE_BREAK);
                    Helpers.playSound(level, pos, SoundEvents.ITEM_BREAK);
                }
                quern.setAndUpdateSlots(SLOT_HANDSTONE);
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, QuernBlockEntity quern)
    {
        if (quern.rotationTimer > 0)
        {
            final ItemStack inputStack = quern.inventory.getStackInSlot(SLOT_INPUT);
            if (!inputStack.isEmpty())
            {
                addParticle(level, pos, inputStack);
            }

            quern.rotationTimer--;
            if (quern.rotationTimer == 0)
            {
                // Simulate the damage on client side, to see if it would break. If it does, we create particle shower to indicate the handstone breaking.
                final ItemStack undamagedHandstoneStack = quern.inventory.getStackInSlot(SLOT_HANDSTONE);
                if (!undamagedHandstoneStack.isEmpty())
                {
                    final ItemStack handstoneStack = undamagedHandstoneStack.copy();
                    Helpers.damageItem(handstoneStack, 1);
                    if (handstoneStack.isEmpty())
                    {
                        for (int i = 0; i < 15; i++)
                        {
                            addParticle(level, pos, handstoneStack);
                        }
                    }
                }
            }
        }
    }

    private static void addParticle(Level level, BlockPos pos, ItemStack item)
    {
        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + 0.5D, pos.getY() + 0.875D, pos.getZ() + 0.5D, Helpers.triangle(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.triangle(level.random) / 2.0D);
    }

    private int rotationTimer;

    public QuernBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.QUERN.get(), pos, state, defaultInventory(3), NAME);
        rotationTimer = 0;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return slot == SLOT_HANDSTONE ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return slot != SLOT_HANDSTONE || TFCTags.Items.HANDSTONE.contains(stack.getItem());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        rotationTimer = nbt.getInt("rotationTimer");
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putInt("rotationTimer", rotationTimer);
        return super.save(nbt);
    }

    @Override
    public boolean canInteractWith(Player player)
    {
        return super.canInteractWith(player) && rotationTimer == 0;
    }

    public int getRotationTimer()
    {
        return rotationTimer;
    }

    public boolean isGrinding()
    {
        return rotationTimer > 0;
    }

    public boolean hasHandstone()
    {
        return !inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty();
    }

    /**
     * Attempts to start grinding. Returns {@code true} if it did.
     */
    public boolean startGrinding()
    {
        assert level != null;
        if (!level.isClientSide)
        {
            final ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);

            if (!inputStack.isEmpty())
            {
                final ItemStackInventory wrapper = new ItemStackInventory(inputStack);
                final QuernRecipe recipe = QuernRecipe.getRecipe(level, wrapper);
                if (recipe != null && recipe.matches(wrapper, level))
                {
                    rotationTimer = 90;
                    markForSync();
                    return true;
                }
            }
        }
        return false;
    }

    private void finishGrinding()
    {
        assert level != null;
        if (!level.isClientSide)
        {
            final ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
            if (!inputStack.isEmpty())
            {
                final ItemStackInventory wrapper = new ItemStackInventory(inputStack);
                final QuernRecipe recipe = QuernRecipe.getRecipe(level, wrapper);
                if (recipe != null && recipe.matches(wrapper, level))
                {
                    inputStack.shrink(1);

                    ItemStack outputStack = recipe.assemble(wrapper);
                    outputStack = Helpers.mergeInsertStack(inventory, SLOT_OUTPUT, outputStack);
                    if (!outputStack.isEmpty())
                    {
                        Helpers.spawnItem(level, worldPosition, outputStack);
                    }
                    markForSync();
                }
            }
        }
    }
}
