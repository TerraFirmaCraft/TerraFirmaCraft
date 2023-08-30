package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.JarsBlockEntity;
import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blocks.JarsBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class JarItem extends Item
{
    private final ResourceLocation model;

    public JarItem(Properties properties, String fruit)
    {
        this(properties, Helpers.identifier("block/jar/" + fruit));
    }

    public JarItem(Properties properties, ResourceLocation model)
    {
        super(properties);
        this.model = model;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final ItemStack held = context.getItemInHand();
        final BlockPos pos = context.getClickedPos();
        final BlockPos above = pos.above();
        final Direction dir = context.getClickedFace();
        if (dir == Direction.UP && BottomSupportedDeviceBlock.canSurvive(level, above))
        {
            level.setBlockAndUpdate(above, TFCBlocks.JARS.get().defaultBlockState());
            if (level.getBlockEntity(above) instanceof JarsBlockEntity jars)
            {
                final var inv = Helpers.getCapability(jars, Capabilities.ITEM);
                if (inv != null)
                {
                    final Vec3 location = context.getClickLocation();
                    final boolean x = Math.round(location.x) < location.x;
                    final boolean z = Math.round(location.z) < location.z;
                    final int slot = (x ? 1 : 0) + (z ? 2 : 0);

                    final ItemStack leftover = inv.insertItem(slot, held.split(1), false);
                    if (context.getPlayer() != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(context.getPlayer(), leftover);
                        level.setBlockAndUpdate(above, JarsBlock.updateStateValues(level, above, level.getBlockState(above)));
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        return new ItemStack(TFCItems.EMPTY_JAR.get());
    }

    public ResourceLocation getModel()
    {
        return model;
    }
}
