/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;

/**
 * An abstraction for a tile entity containing at least, an inventory (item handler) capability
 * However, the inventory itself is generic.
 */
public abstract class InventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TFCBlockEntity implements ISlotCallback, MenuProvider, Clearable
{
    public static InventoryFactory<ItemStackHandler> defaultInventory(int slots)
    {
        return self -> new InventoryItemHandler(self, slots);
    }

    protected final C inventory;
    protected final SidedHandler<IItemHandlerModifiable> sidedInventory;
    protected @Nullable Component customName;
    protected final Component defaultName;
    protected @Nullable ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;

    protected InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory)
    {
        this(type, pos, state, inventoryFactory, TerraFirmaCraft.MOD_ID);
    }

    protected InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, String modId)
    {
        super(type, pos, state);

        this.inventory = inventoryFactory.create(this);
        this.sidedInventory = new SidedHandler<>(InventoryBlockEntity.this.inventory);
        this.defaultName = Component.translatable(modId + ".block_entity." + Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).getPath());
    }

    /**
     * Returns an internal view of the inventory of this block. This is used for internal operations within TFC that are coded specifically to
     * this block, for example, the implementation of a block entity renderer. <strong>DO NOT</strong> use for interactions that are meant to be
     * interoperable with other mods, or other devices within TFC, for instance inserting on right-click.
     */
    public C getInventory()
    {
        return inventory;
    }

    /**
     * Returns the internal view of the sided capability. This is used to implement capability providers in {@link BlockCapabilities}. Note
     * that this only returns the internal {@link IItemHandler}, as opposed to {@code C}, as different capabilities exposed by the same
     * inventory may want to return different handling for different sides.
     */
    @Nullable
    public IItemHandler getSidedInventory(@Nullable Direction context)
    {
        return sidedInventory.get(context);
    }

    /**
     * Returns the display name of this block. This is set when placed by an item with a custom name component, and is accessed as part
     * of the {@link MenuProvider} that this block entity implements. The mechanic is based on {@link BaseContainerBlockEntity}
     */
    @Override
    public Component getDisplayName()
    {
        return customName == null ? defaultName : customName;
    }

    /**
     * Called when this block entity is placed in the world, with components from an item stack. The <strong>implicit components</strong>
     * are ones that nominally should exist on the block entity, but are constructed and applied only when the entity is constructed (or
     * broken). This includes something like, the saved content of the block entity (which normally, is in mutable form as the block entity
     * inventory). This then gets copied to a component when dropped via, for example {@link CopyComponentsFunction}.
     * <p>
     * Note that any components <em>referenced</em> here, from {@code components}, will not be added to the block entities' components. Thus,
     * it is important that any components produced in {@link #collectImplicitComponents} are consumed here.
     *
     * @param components The components, containing both original, and new components.
     */
    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        customName = components.get(DataComponents.CUSTOM_NAME);
        final @Nullable SeededContainerLoot loot = components.get(DataComponents.CONTAINER_LOOT);
        if (loot != null)
        {
            lootTable = loot.lootTable();
            lootTableSeed = loot.seed();
        }
    }

    /**
     * Produces any components that are implicitly present on this block entity. This is invoked, typically when dropped as an item via
     * a particular loot function, or when saved to an item. The components produced here should match exactly the components consumed
     * in {@link #applyImplicitComponents}.
     *
     * @param builder The component builder, to add components to.
     */
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        builder.set(DataComponents.CUSTOM_NAME, customName);
        if (lootTable != null)
        {
            builder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(lootTable, lootTableSeed));
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (nbt.contains("CustomName"))
        {
            customName = parseCustomNameSafe(nbt.getString("CustomName"), provider);
        }
        if (nbt.contains(RandomizableContainer.LOOT_TABLE_TAG, Tag.TAG_STRING))
        {
            lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(nbt.getString(RandomizableContainer.LOOT_TABLE_TAG)));
            lootTableSeed = nbt.getLong(RandomizableContainer.LOOT_TABLE_SEED_TAG);
        }
        else
        {
            lootTable = null;
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
        }
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (customName != null)
        {
            nbt.putString("CustomName", Component.Serializer.toJson(customName, provider));
        }
        if (lootTable != null)
        {
            nbt.putString(RandomizableContainer.LOOT_TABLE_TAG, lootTable.location().toString());
            if (lootTableSeed != 0L)
            {
                nbt.putLong(RandomizableContainer.LOOT_TABLE_SEED_TAG, lootTableSeed);
            }
        }
        else
        {
            nbt.put("inventory", inventory.serializeNBT(provider));
        }
        super.saveAdditional(nbt, provider);
    }

    @Override
    protected void onLoadAdditional()
    {
        unpackLootTable();
    }

    /**
     * Sets a loot table to fill this inventory with. It is rolled immediately if this block entity is in a server level, otherwise (during
     * world generation) when it is loaded into one. The same can be done with data, by adding {@code LootTable} and optionally
     * {@code LootTableSeed} tags to the block entity NBT, for example in a structure file.
     *
     * @param seed The seed used to roll the table, or {@code 0} to use the level's random.
     */
    public void setLootTable(ResourceKey<LootTable> lootTable, long seed)
    {
        this.lootTable = lootTable;
        this.lootTableSeed = seed;
        unpackLootTable();
    }

    public void unpackLootTable()
    {
        if (lootTable != null && level instanceof ServerLevel serverLevel)
        {
            final LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(lootTable);
            final SimpleContainer rolled = new SimpleContainer(inventory.getSlots());

            lootTable = null;
            table.fill(rolled, new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition)).create(LootContextParamSets.CHEST), lootTableSeed);
            for (int slot = 0; slot < inventory.getSlots(); slot++)
            {
                final ItemStack stack = rolled.getItem(slot);
                if (!stack.isEmpty() && inventory.getStackInSlot(slot).isEmpty())
                {
                    insertLoot(slot, stack);
                }
            }
            markForSync();
        }
    }

    protected void insertLoot(int slot, ItemStack stack)
    {
        if (inventory.isItemValid(slot, stack))
        {
            inventory.setStackInSlot(slot, stack.copyWithCount(Math.min(stack.getCount(), inventory.getSlotLimit(slot))));
        }
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

    @Override
    public void setAndUpdateSlots(int slot)
    {
        setChanged();
        markForSync();
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
