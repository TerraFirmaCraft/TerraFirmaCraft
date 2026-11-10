/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCDamageTypes;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.LobsterTrapBlock;
import net.dries007.tfc.common.blocks.LobsterTrapBlock.TrapState;
import net.dries007.tfc.util.Helpers;

public class LobsterTrapBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int CATCH_SLOTS = 6;
    public static final int BAIT_PER_ITEM = 3;
    public static final float JUNK_CHANCE = 0.1f;

    private int bait = 0;

    public LobsterTrapBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.LOBSTER_TRAP.get(), pos, state);
    }

    protected LobsterTrapBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(CATCH_SLOTS));
    }

    public void tryBait(Player player, ItemStack stack)
    {
        if (!Helpers.isItem(stack, TFCTags.Items.LOBSTER_BAIT) || bait > 0 || !Helpers.isEmpty(Helpers.iterate(inventory)))
            return;
        bait = BAIT_PER_ITEM;
        stack.consume(1, player);
        if (level instanceof ServerLevel server)
        {
            Helpers.playSound(server, worldPosition, SoundEvents.BUNDLE_INSERT);
            bubbles(server, 4);
        }
        updateTrapState();
    }

    public void takeContents(Player player)
    {
        boolean tookAny = false;
        for (int slot = 0; slot < CATCH_SLOTS; slot++)
        {
            final ItemStack stack = Helpers.removeStack(inventory, slot);
            if (!stack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                tookAny = true;
            }
        }
        if (!tookAny && bait > 0)
        {
            bait = 0;
            tookAny = true;
        }
        if (tookAny)
        {
            updateTrapState();
        }
    }

    public int getBait()
    {
        return bait;
    }

    public boolean canCatch()
    {
        return getBlockState().getValue(LobsterTrapBlock.TRAP) == TrapState.BAITED && Helpers.hasOpenSlot(inventory);
    }

    /**
     * Kills the mob using the {@link TFCDamageTypes#LOBSTER_TRAP}, which satisfies "killed by player" loot conditions via {@link TFCTags.DamageTypes#COUNTS_AS_PLAYER_KILL}.
     */
    public void catchEntity(ServerLevel level, Mob mob)
    {
        if (level.random.nextFloat() < JUNK_CHANCE)
        {
            Helpers.randomItem(TFCTags.Items.LOBSTER_TRAP_JUNK, level.random).ifPresent(item -> Helpers.insertAllSlots(inventory, new ItemStack(item)));
        }
        else
        {
            final LootTable table = level.getServer().reloadableRegistries().getLootTable(mob.getLootTable());
            final LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, TFCDamageTypes.lobsterTrap(level))
                .create(LootContextParamSets.ENTITY);

            for (ItemStack stack : table.getRandomItems(params))
            {
                Helpers.insertAllSlots(inventory, stack);
            }
        }

        bait = Math.max(0, bait - 1);
        bubbles(level, 12);
        mob.discard();
        updateTrapState();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    private void bubbles(ServerLevel level, int count)
    {
        level.sendParticles(TFCParticles.BUBBLE.get(), worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, count, 0.3, 0.3, 0.3, 0.05);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        bait = nbt.getInt("bait");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.putInt("bait", bait);
    }

    private void updateTrapState()
    {
        assert level != null;

        final boolean hasBait = bait > 0;
        final TrapState state = !Helpers.isEmpty(Helpers.iterate(inventory)) && (!hasBait || !Helpers.hasOpenSlot(inventory))
            ? TrapState.CLOSED
            : hasBait ? TrapState.BAITED : TrapState.OPEN;

        final BlockState current = getBlockState();
        if (current.getValue(LobsterTrapBlock.TRAP) != state)
        {
            level.setBlockAndUpdate(worldPosition, current.setValue(LobsterTrapBlock.TRAP, state));
        }
        markForSync();
    }
}
