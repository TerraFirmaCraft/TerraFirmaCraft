/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.glass;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class GlassWorkData
{
    private static final String KEY = "tfc:glass_work_data";

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final GlassWorkData data = get(stack);
        if (data != null)
        {
            tooltips.add(data.batch.getHoverName());
            if (!data.operations.getSteps().isEmpty())
            {
                tooltips.add(Component.translatable("tfc.tooltip.glass.title").withStyle(ChatFormatting.AQUA));
                for (GlassOperation operation : data.operations.getSteps())
                {
                    tooltips.add(Component.literal("- ").append(Helpers.translateEnum(operation)));
                }
            }
        }
    }

    @Nullable
    public static GlassWorkData get(ItemStack stack)
    {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY, Tag.TAG_COMPOUND))
        {
            return new GlassWorkData().read(tag.getCompound(KEY));
        }
        return null;
    }

    public static void apply(ItemStack stack, GlassOperation operation)
    {
        GlassWorkData data = get(stack);
        if (data == null)
        {
            data = attach(stack);
        }
        data.getOperations().apply(operation);
        stack.getOrCreateTag().put(KEY, data.write());
    }

    public static void createNewBatch(ItemStack stack, ItemStack glass)
    {
        GlassWorkData data = new GlassWorkData();
        data.batch = glass.copy();
        stack.getOrCreateTag().put(KEY, data.write());
    }

    public static void clear(ItemStack stack)
    {
        stack.removeTagKey(KEY);
    }

    private static GlassWorkData attach(ItemStack stack)
    {
        final GlassWorkData data = new GlassWorkData();
        stack.getOrCreateTag().put(KEY, data.write());
        return data;
    }

    private final GlassOperations operations;
    private ItemStack batch;

    private GlassWorkData()
    {
        operations = new GlassOperations();
        batch = ItemStack.EMPTY;
    }

    public GlassOperations getOperations()
    {
        return operations;
    }

    public ItemStack getBatch()
    {
        return batch;
    }

    private CompoundTag write()
    {
        var tag = new CompoundTag();
        operations.write(tag);
        tag.put("stack", batch.save(new CompoundTag()));
        return tag;
    }

    private GlassWorkData read(CompoundTag tag)
    {
        operations.read(tag);
        batch = ItemStack.of(tag.getCompound("stack"));
        return this;
    }
}
