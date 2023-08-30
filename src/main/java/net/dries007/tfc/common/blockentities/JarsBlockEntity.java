package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.JarsBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class JarsBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.jars");

    public JarsBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.JARS.get(), pos, state);
    }

    public boolean use(Player player, ItemStack held, BlockHitResult result)
    {
        final int slot = PlacedItemBlockEntity.getSlotSelected(result);
        final ItemStack current = inventory.getStackInSlot(slot);
        if (held.isEmpty() && !current.isEmpty())
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(slot, 1, false));
            BlockState state = JarsBlock.updateStateValues(player.level(), getBlockPos(), getBlockState());
            player.level().setBlockAndUpdate(getBlockPos(), JarsBlock.isEmpty(state) ? Blocks.AIR.defaultBlockState() : state);
            return true;
        }
        else if (current.isEmpty() && isItemValid(slot, held))
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.insertItem(slot, held.split(1), false));
            BlockState state = JarsBlock.updateStateValues(player.level(), getBlockPos(), getBlockState());
            player.level().setBlockAndUpdate(getBlockPos(), JarsBlock.isEmpty(state) ? Blocks.AIR.defaultBlockState() : state);
            return true;
        }
        return false;
    }

    public JarsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(4), NAME);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.JARS);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

}
