/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class DouseFireEvent extends Event implements ICancellableEvent
{
    public static boolean douse(Level level, BlockPos pos, @Nullable Player player)
    {
        return NeoForge.EVENT_BUS.post(new DouseFireEvent(level, pos, level.getBlockState(pos), new AABB(pos), player)).isCanceled();
    }

    public static void douse(Level level, AABB bounds, @Nullable Player player)
    {
        if (!level.isClientSide) // follows vanilla pattern in ThrownPotion
        {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, bounds, ThrownPotion.WATER_SENSITIVE_OR_ON_FIRE))
            {
                if (entity.isOnFire() && entity.isAlive())
                {
                    entity.extinguishFire();
                }
            }

            for (Axolotl axolotl : level.getEntitiesOfClass(Axolotl.class, bounds))
            {
                axolotl.rehydrate();
            }
        }

        BlockPos.betweenClosedStream(bounds).forEach(pos -> NeoForge.EVENT_BUS.post(new DouseFireEvent(level, pos, level.getBlockState(pos), bounds, player)));
    }

    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final AABB bounds;
    @Nullable private final Player player;

    private DouseFireEvent(Level level, BlockPos pos, BlockState state, AABB bounds, @Nullable Player player)
    {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.bounds = bounds;
        this.player = player;
    }

    public Level getLevel()
    {
        return level;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public AABB getBounds()
    {
        return bounds;
    }

    public BlockState getState()
    {
        return state;
    }

    @Nullable
    public Player getPlayer()
    {
        return player;
    }
}
