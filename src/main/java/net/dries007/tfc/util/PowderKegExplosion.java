/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

        final List<Pair<ItemStack, BlockPos>> allDrops = new ArrayList<>();
        final boolean easyMode = TFCConfig.SERVER.powderKegOnlyBreaksNaturalBlocks.get();

        level.getProfiler().push("explosion_blocks");
        Util.shuffle(getToBlow(), level.random);

        for (BlockPos pos : getToBlow())
        {
            final BlockState state = level.getBlockState(pos);
            if (easyMode ? !Helpers.isBlock(state, TFCTags.Blocks.POWDERKEG_CAN_BREAK) : Helpers.isBlock(state, TFCTags.Blocks.POWDERKEG_CANNOT_BREAK))
                continue;

            state.onExplosionHit(level, pos, this, (drop, dropPos) -> addBlockDrops(allDrops, drop, dropPos));
        }

        for (Pair<ItemStack, BlockPos> pair : allDrops)
        {
            Block.popResource(level, pair.getSecond(), pair.getFirst());
        }

        level.getProfiler().pop();
    }

    @Nullable
    @Override
    public Entity getDirectSourceEntity()
    {
        return source;
    }

    @Nullable
    @Override
    public LivingEntity getIndirectSourceEntity()
    {
        return source instanceof LivingEntity living ? living : null;
    }

    private static void addBlockDrops(List<Pair<ItemStack, BlockPos>> allDrops, ItemStack drop, BlockPos dropPos)
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