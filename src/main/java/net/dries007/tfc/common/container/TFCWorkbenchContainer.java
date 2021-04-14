package net.dries007.tfc.common.container;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.dries007.tfc.common.TFCTags;

/**
 * Copy of {@link WorkbenchContainer} for proper insertion of the container type.
 */
public class TFCWorkbenchContainer extends RecipeBookContainer<CraftingInventory>
{
    final CraftingInventory craftSlots = new CraftingInventory(this, 3, 3);
    private final CraftResultInventory resultSlots = new CraftResultInventory();
    private final IWorldPosCallable access;
    private final PlayerEntity player;

    public TFCWorkbenchContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, IWorldPosCallable.NULL);
    }

    public TFCWorkbenchContainer(ContainerType<?> type, int id, PlayerInventory inv, IWorldPosCallable worldPosCallable_)
    {
        super(type, id);
        this.access = worldPosCallable_;
        this.player = inv.player;
        this.addSlot(new CraftingResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 124, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlot(new Slot(inv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlot(new Slot(inv, l, 8 + l * 18, 142));
        }

    }

    @Override
    public void fillCraftSlotsStackedContents(RecipeItemHelper itemHelperIn)
    {
        this.craftSlots.fillStackedContents(itemHelperIn);
    }

    @Override
    public void clearCraftingContent()
    {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(IRecipe<? super CraftingInventory> recipeIn)
    {
        return recipeIn.matches(this.craftSlots, this.player.level);
    }

    @Override
    public int getResultSlotIndex()
    {
        return 0;
    }

    @Override
    public int getGridWidth()
    {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight()
    {
        return this.craftSlots.getHeight();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getSize()
    {
        return 10;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RecipeBookCategory getRecipeBookType()
    {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if (index == 0)
            {
                this.access.execute((world, pos) -> slotStack.getItem().onCraftedBy(slotStack, world, playerIn));
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) return ItemStack.EMPTY;

                slot.onQuickCraft(slotStack, returnStack);
            }
            else if (index >= 10 && index < 46)
            {
                if (!this.moveItemStackTo(slotStack, 1, 10, false))
                {
                    if (index < 37)
                    {
                        if (!this.moveItemStackTo(slotStack, 37, 46, false)) return ItemStack.EMPTY;
                    }
                    else if (!this.moveItemStackTo(slotStack, 10, 37, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.moveItemStackTo(slotStack, 10, 46, false))
            {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) return ItemStack.EMPTY;

            ItemStack dropStack = slot.onTake(playerIn, slotStack);
            if (index == 0)
            {
                playerIn.drop(dropStack, false);
            }
        }

        return returnStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn)
    {
        return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public void removed(PlayerEntity playerIn)
    {
        super.removed(playerIn);
        this.access.execute((world, pos) -> this.clearContainer(playerIn, world, this.craftSlots));
    }

    @Override
    public void slotsChanged(IInventory inventoryIn)
    {
        this.access.execute((world, pos) -> slotChangedCraftingGrid(this.containerId, world, this.player, this.craftSlots, this.resultSlots));
    }

    /**
     * Determines whether supplied player can use this container
     * TFC: use a tag instead of hardcoding
     */
    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return access.evaluate((world, pos) -> world.getBlockState(pos).is(TFCTags.Blocks.WORKBENCH) && playerIn.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    protected static void slotChangedCraftingGrid(int id, World worldIn, PlayerEntity player, CraftingInventory inventoryIn, CraftResultInventory inventoryResult)
    {
        if (!worldIn.isClientSide)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
            ItemStack itemstack = ItemStack.EMPTY;
            MinecraftServer server = worldIn.getServer();
            if (server == null) return; // tfc: add null check
            Optional<ICraftingRecipe> optional = server.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, inventoryIn, worldIn);
            if (optional.isPresent())
            {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (inventoryResult.setRecipeUsed(worldIn, serverplayerentity, icraftingrecipe))
                {
                    itemstack = icraftingrecipe.assemble(inventoryIn);
                }
            }

            inventoryResult.setItem(0, itemstack);
            serverplayerentity.connection.send(new SSetSlotPacket(id, 0, itemstack));
        }
    }
}
