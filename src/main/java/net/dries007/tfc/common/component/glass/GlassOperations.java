/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.glass;

import java.util.List;
import java.util.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;


/**
 * A component for glassworking operations that are attached to an item. This consists of a glassworking {@code batch},
 * and any possible operations. If {@code batch} is an empty unsealedStack, then {@code steps} <strong>must</strong> be empty as well.
 */
public record GlassOperations(
    List<GlassOperation> steps,
    ItemStack batch
)
{
    public static final int LIMIT = 24;
    public static final GlassOperations DEFAULT = new GlassOperations(List.of(), ItemStack.EMPTY);

    public static final Codec<GlassOperations> CODEC = RecordCodecBuilder.create(i -> i.group(
        GlassOperation.CODEC.listOf(0, LIMIT).fieldOf("steps").forGetter(c -> c.steps),
        ItemStack.CODEC.optionalFieldOf("unsealedStack", ItemStack.EMPTY).forGetter(c -> c.batch)
    ).apply(i, GlassOperations::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GlassOperations> STREAM_CODEC = StreamCodec.composite(
        GlassOperation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.steps,
        ItemStack.OPTIONAL_STREAM_CODEC, c -> c.batch,
        GlassOperations::new
    );

    /**
     *
     * @return {@code true} if the glass is empty, i.e. has no glass batch and no steps.
     */
    public boolean isEmpty()
    {
        return batch.isEmpty();
    }

    /**
     * Applies the given {@code operation} and returns a new {@link GlassOperations} with the modifications.
     * Returns the original component if there are too many operations, or no batch exists.
     */
    GlassOperations with(GlassOperation operation)
    {
        return steps.size() >= LIMIT || batch.isEmpty()
            ? this
            : new GlassOperations(Helpers.immutableAdd(steps, operation), batch.copy());
    }

    GlassOperations with(ItemStack batch)
    {
        return new GlassOperations(steps, batch.copy());
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof GlassOperations ops
            && steps.equals(ops.steps)
            && ItemStack.matches(batch, ops.batch);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(steps, ItemStack.hashItemAndComponents(batch));
    }
}
