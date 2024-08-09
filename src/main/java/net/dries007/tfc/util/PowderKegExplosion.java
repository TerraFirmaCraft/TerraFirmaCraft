/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;

public class PowderKegExplosion extends Explosion
{
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity source;
    private final float size;

    public PowderKegExplosion(Level level, @Nullable Entity entity, double x, double y, double z, float size)
    {
        super(level, null, x, y, z, size, false, BlockInteraction.DESTROY);
        this.level = level;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.source = entity;
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     *
     * (Forgive the Mojang copypasta)
     */
    @Override
    public void finalizeExplosion(boolean spawnParticles)
    {
        if (this.level.isClientSide)
        {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (size >= 2.0F)
        {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0d, 0.d, 0.0d);
        }
        else
        {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        }

        final List<BlockPos> affectedBlockPositions = this.getToBlow();
        final ObjectArrayList<Pair<ItemStack, BlockPos>> allDrops = new ObjectArrayList<>();
        Collections.shuffle(affectedBlockPositions, new Random());

        final boolean easyMode = TFCConfig.SERVER.powderKegOnlyBreaksNaturalBlocks.get();

        for (BlockPos pos : affectedBlockPositions)
        {
            final BlockState state = level.getBlockState(pos);

            if (!easyMode)
            {
                if (Helpers.isBlock(state, TFCTags.Blocks.POWDERKEG_CANNOT_BREAK))
                    continue;
            }
            else
            {
                if (!Helpers.isBlock(state, TFCTags.Blocks.POWDERKEG_CAN_BREAK))
                    continue;
            }

            if (spawnParticles)
            {
                final double x = (pos.getX() + this.level.random.nextFloat());
                final double y = (pos.getY() + this.level.random.nextFloat());
                final double z = (pos.getZ() + this.level.random.nextFloat());
                double dx = x - this.x;
                double dy = y - this.y;
                double dz = z - this.z;
                double distance = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
                dx = dx / distance;
                dy = dy / distance;
                dz = dz / distance;
                double scaledPower = 0.5d / (distance / (double) this.size + 0.1d);
                scaledPower = scaledPower * (double) (this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3f);
                dx = dx * scaledPower;
                dy = dy * scaledPower;
                dz = dz * scaledPower;
                level.addParticle(ParticleTypes.EXPLOSION, (x + this.x) / 2.0d, (y + this.y) / 2.0d, (z + this.z) / 2.0d, dx, dy, dz);
                level.addParticle(ParticleTypes.EXPLOSION, x, y, z, dx, dy, dz);
            }

            if (!state.isAir())
            {
                final BlockPos dropPos = pos.immutable();
                this.level.getProfiler().push("explosion_blocks");
                if (state.canDropFromExplosion(this.level, pos, this) && this.level instanceof ServerLevel)
                {
                    final BlockEntity blockentity = state.hasBlockEntity() ? this.level.getBlockEntity(pos) : null;
                    final LootParams.Builder lootContext = (new LootParams.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);

                    state.getDrops(lootContext).forEach((drop) -> addBlockDrops(allDrops, drop, dropPos));
                }

                state.onBlockExploded(this.level, pos, this);
                this.level.getProfiler().pop();
            }

        }

        for (Pair<ItemStack, BlockPos> pair : allDrops)
        {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> allDrops, ItemStack drop, BlockPos dropPos)
    {
        int i = allDrops.size();

        for (int j = 0; j < i; ++j)
        {
            Pair<ItemStack, BlockPos> pair = allDrops.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, drop))
            {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, drop, 16);
                allDrops.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (drop.isEmpty())
                {
                    return;
                }
            }
        }

        allDrops.add(Pair.of(drop, dropPos));
    }

}