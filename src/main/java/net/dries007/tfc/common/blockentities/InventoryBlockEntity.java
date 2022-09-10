/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Random;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstraction for a block entity containing at least, an inventory (item handler) capability
 * However, the inventory itself is generic.
 */
public abstract class InventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TFCBlockEntity implements ISlotCallback, MenuProvider, Clearable
{
    public static InventoryFactory<ItemStackHandler> defaultInventory(int slots)
    {
        return self -> new InventoryItemHandler(self, slots);
    }

    /**
     * Provided as public API for setting the loot table of an inventory through code.
     * In vanilla, this is often done through structure pieces classes or features, see usages of {@link RandomizableContainerBlockEntity#setLootTable(BlockGetter, Random, BlockPos, ResourceLocation)}
     *
     * However, you may want to use loot tables through datapacks, in which case you can set the loot table by adding one or two tags to the NBT:
     * - "LootTable": the resource location ID of the loot table
     * - "LootTableSeed": the random seed of the loot table. You should omit this if you want every chest to be different.
     *
     * An example of setting a block with this NBT: /setblock ~ ~ ~ minecraft:chest{LootTable:"modid:example_loot_table"}
     * The block entity can be picked up and replace with ctrl+middle-click, aka the special pick block key.
     *
     * Adding a loot table requires the block entity to load the loot table when its inventory is first accessed. That is to say it is handled automatically, and you don't need to do further handling.
     * @author EERussianguy
     */
    public static void setLootTable(BlockGetter level, RandomSource random, BlockPos pos, ResourceLocation id)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof InventoryBlockEntity<?> inventory)
        {
            inventory.setLootTable(id, random.nextLong());
        }
    }

    protected final C inventory;
    protected final SidedHandler.Builder<IItemHandler> sidedInventory;
    @Nullable protected Component customName;
    protected Component defaultName;

    @Nullable protected ResourceLocation lootTable;
    protected long lootTableSeed;

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component defaultName)
    {
        super(type, pos, state);

        this.inventory = inventoryFactory.create(this);
        this.sidedInventory = new SidedHandler.Builder<>(InventoryBlockEntity.this.inventory);
        this.defaultName = defaultName;
    }

    public void setLootTable(ResourceLocation id, long seed)
    {
        this.lootTable = id;
        this.lootTableSeed = seed;
    }

    protected boolean tryLoadLootTable(CompoundTag tag)
    {
        if (tag.contains(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, Tag.TAG_STRING))
        {
            this.lootTable = new ResourceLocation(tag.getString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG));
            this.lootTableSeed = tag.getLong(RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG);
            return true;
        }
        return false;
    }

    protected boolean trySaveLootTable(CompoundTag tag)
    {
        if (lootTable == null) return false;
        tag.putString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, lootTable.toString());
        if (lootTableSeed != 0L)
        {
            tag.putLong(RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG, lootTableSeed);
        }
        return true;
    }

    public void unpackLootTable(@Nullable Player player)
    {
        if (lootTable != null && level != null && level.getServer() != null)
        {
            final LootTable loot = this.level.getServer().getLootTables().get(this.lootTable);
            if (player instanceof ServerPlayer serverPlayer)
            {
                CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, this.lootTable);
            }

            this.lootTable = null;
            final LootContext.Builder builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null)
            {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }

            Helpers.fillContainerLoot(loot, inventory, builder.create(LootContextParamSets.CHEST));
            markForSync();
        }
    }

    @Override
    public Component getDisplayName()
    {
        return customName == null ? defaultName : customName;
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return null;
    }

    public boolean canOpen(Player player)
    {
        if (lootTable == null || !player.isSpectator())
        {
            unpackLootTable(player);
            return true;
        }
        return false;
    }

    @Override
    public void slotChecked()
    {
        unpackLootTable(null);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        if (nbt.contains("CustomName"))
        {
            customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }
        if (!tryLoadLootTable(nbt))
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        if (customName != null)
        {
            nbt.putString("CustomName", Component.Serializer.toJson(customName));
        }
        if (!trySaveLootTable(nbt))
        {
            nbt.put("inventory", inventory.serializeNBT());
        }
        super.saveAdditional(nbt);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.ITEM)
        {
            return sidedInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void ejectInventory()
    {
        assert level != null;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                Helpers.spawnItem(level, worldPosition, stack);
            }
        }
    }

    public void invalidateCapabilities()
    {
        sidedInventory.invalidate();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        setChanged();
    }

    public boolean canInteractWith(Player player)
    {
        if (level == null || level.getBlockEntity(worldPosition) != this)
        {
            return false;
        }
        else
        {
            return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64;
        }
    }

    /**
     * A factory interface for the inventory field, allows self references in the constructor
     */
    @FunctionalInterface
    public interface InventoryFactory<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>>
    {
        C create(InventoryBlockEntity<C> entity);
    }
}
