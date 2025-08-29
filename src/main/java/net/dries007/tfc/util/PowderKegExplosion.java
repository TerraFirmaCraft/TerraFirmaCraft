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
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
     * This will only be called on the logical server. The client side particles and sounds are
     * handled by {@link sendExplosionPacketToClients}, which should be called
     * immediately after this.
     *
     * (Forgive the Mojang copypasta)
     */
    @Override
    public void finalizeExplosion(boolean spawnParticles)
    {
        assert !level.isClientSide;

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

    /**
     * Notifies clients of the powderkeg explosion to play sounds and show particles.
     * and particles. Should be called after {@link PowderKegExplosion#finalizeExplosion},
     * since we don't use the vanilla explosion code that sends packets for us.
     */
    public void sendExplosionPacketToClients()
    {
        // Since we don't use the vanilla explosion logic in ServerLevel#explode,
        // we must send explosion packets to clients ourselves
        if (level instanceof ServerLevel serverLevel)
        {
            for (ServerPlayer serverplayer : serverLevel.players()) {
                if (serverplayer.distanceToSqr(x, y, z) < 4096.0) {
                    serverplayer.connection
                        .send(
                            new ClientboundExplodePacket(
                                x,
                                y,
                                z,
                                size,
                                getToBlow(),
                                getHitPlayers().get(serverplayer),
                                getBlockInteraction(),
                                getSmallExplosionParticles(),
                                getLargeExplosionParticles(),
                                getExplosionSound()
                            )
                        );
                }
            }
        }
    }
}