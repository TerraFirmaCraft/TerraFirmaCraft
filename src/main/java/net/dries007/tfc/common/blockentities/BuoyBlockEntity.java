/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.LobsterTrapBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class BuoyBlockEntity extends TFCBlockEntity
{
    private static final float WIND_LERP = 0.02f;

    public static void clientTick(Level level, BlockPos pos, BlockState state, BuoyBlockEntity buoy)
    {
        if (!buoy.hasSampledWind || level.getGameTime() % 40 == 0)
        {
            final Vec2 wind = Climate.get(level).getWind(level, pos);
            buoy.targetWindX = wind.x;
            buoy.targetWindZ = wind.y;
            if (!buoy.hasSampledWind)
            {
                buoy.windX = wind.x;
                buoy.windZ = wind.y;
                buoy.hasSampledWind = true;
            }
        }

        buoy.windX = Mth.lerp(WIND_LERP, buoy.windX, buoy.targetWindX);
        buoy.windZ = Mth.lerp(WIND_LERP, buoy.windZ, buoy.targetWindZ);

        // Advance the animation by a variable amount, rather than scaling time directly, so changes in wind speed don't cause the buoy to jump
        buoy.prevPhase = buoy.phase;
        buoy.phase += 1 + buoy.getWindStrength() * 1.5f;
    }

    /**
     * @return {@code true} if a lobster trap could be moved into {@code pos}, which must be a plain source block of a fluid the trap can hold.
     */
    public static boolean canTrapOccupy(Level level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        final FluidState fluid = state.getFluidState();
        return state.getBlock() instanceof LiquidBlock && fluid.isSource() && LobsterTrapBlock.FLUID.canContain(fluid.getType());
    }

    private float windX, windZ, targetWindX, targetWindZ;
    private double phase, prevPhase;
    private boolean hasSampledWind = false;

    private int ropes = 0;
    private boolean hasTrap = false;

    public BuoyBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BUOY.get(), pos, state);
    }

    protected BuoyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public float getWindX()
    {
        return windX;
    }

    public float getWindZ()
    {
        return windZ;
    }

    public float getWindStrength()
    {
        return Mth.clamp(Mth.sqrt(windX * windX + windZ * windZ) / 0.5f, 0f, 1f);
    }

    public double getPhase(float partialTick)
    {
        return Mth.lerp(partialTick, prevPhase, phase);
    }

    public int getRopes()
    {
        return ropes;
    }

    public BlockPos getTrapPos()
    {
        return worldPosition.below(1 + ropes);
    }

    /**
     * @return {@code true} if this buoy has a trap attached, and that trap is still where the line expects it to be.
     */
    public boolean isTrapConnected()
    {
        return hasTrap && level != null && Helpers.isBlock(level.getBlockState(getTrapPos()), TFCBlocks.LOBSTER_TRAP.get());
    }

    /**
     * @return {@code true} if this buoy is holding rope from a trap that has since been removed or moved.
     */
    public boolean isTrapDisconnected()
    {
        return hasTrap && !isTrapConnected();
    }

    public boolean hasTrap()
    {
        return hasTrap;
    }

    public void attachTrap()
    {
        hasTrap = true;
        ropes = 0;
        markForSync();
    }

    public void releaseLine(Player player)
    {
        assert level != null;
        Helpers.playSound(level, worldPosition, SoundEvents.LEASH_KNOT_BREAK);
        giveRopes(player, ropes);
        ropes = 0;
        hasTrap = false;
        markForSync();
    }

    public int sinkTrap(int maxRopes)
    {
        assert level != null;

        final BlockPos from = getTrapPos();
        final BlockPos.MutableBlockPos cursor = from.mutable().move(0, -1, 0);
        int steps = 0;
        while (steps < maxRopes && canTrapOccupy(level, cursor))
        {
            cursor.move(0, -1, 0);
            steps++;
        }
        if (steps > 0)
        {
            moveTrap(level, from, from.below(steps));
            lineEffects(SoundEvents.FISHING_BOBBER_SPLASH, from, from.below(steps));
            ropes += steps;
            markForSync();
        }
        return steps;
    }

    public boolean recallTrap(Player player)
    {
        assert level != null;

        final BlockPos from = getTrapPos();
        final BlockPos.MutableBlockPos cursor = from.mutable().move(0, 1, 0);
        int steps = 0;
        while (steps < ropes && canTrapOccupy(level, cursor))
        {
            cursor.move(0, 1, 0);
            steps++;
        }
        if (steps > 0)
        {
            moveTrap(level, from, from.above(steps));
            lineEffects(SoundEvents.FISHING_BOBBER_RETRIEVE, from, from.above(steps));
            ropes -= steps;
            giveRopes(player, steps);
            markForSync();
        }
        return steps > 0;
    }

    public void dropContents()
    {
        assert level != null;
        for (ItemStack stack : ropeStacks(ropes))
        {
            Block.popResource(level, worldPosition, stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putInt("ropes", ropes);
        tag.putBoolean("hasTrap", hasTrap);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        ropes = tag.getInt("ropes");
        hasTrap = tag.getBoolean("hasTrap");
    }

    private void moveTrap(Level level, BlockPos from, BlockPos to)
    {
        final BlockState trap = level.getBlockState(from);
        final BlockEntity entity = level.getBlockEntity(from);
        final CompoundTag data = entity != null ? entity.saveCustomOnly(level.registryAccess()) : null;
        final BlockState moved = FluidHelpers.fillWithFluid(trap, level.getFluidState(to).getType());
        if (moved == null)
        {
            return;
        }

        if (entity instanceof Clearable clearable)
            clearable.clearContent();
        level.setBlockAndUpdate(to, moved);
        level.setBlockAndUpdate(from, trap.getFluidState().createLegacyBlock());
        if (data != null && level.getBlockEntity(to) instanceof BlockEntity newEntity)
        {
            newEntity.loadCustomOnly(data, level.registryAccess());
            newEntity.setChanged();
        }
    }

    /**
     * A splash at the surface, and a trail of bubbles along the section of line the trap travelled through
     */
    private void lineEffects(SoundEvent sound, BlockPos from, BlockPos to)
    {
        if (level instanceof ServerLevel server)
        {
            final double x = worldPosition.getX() + 0.5, z = worldPosition.getZ() + 0.5;

            Helpers.playSound(server, worldPosition, sound);
            server.sendParticles(ParticleTypes.SPLASH, x, worldPosition.getY(), z, 8, 0.2, 0, 0.2, 0.1);
            for (int y = Math.min(from.getY(), to.getY()); y <= Math.max(from.getY(), to.getY()); y++)
            {
                server.sendParticles(TFCParticles.BUBBLE.get(), x, y + 0.5, z, 2, 0.1, 0.3, 0.1, 0.05);
            }
        }
    }

    private void giveRopes(Player player, int count)
    {
        if (player.isCreative())
            return;
        for (ItemStack stack : ropeStacks(count))
        {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    private static List<ItemStack> ropeStacks(int count)
    {
        final ItemStack template = new ItemStack(TFCItems.ROPE.get());
        final int maxStackSize = template.getMaxStackSize();
        final List<ItemStack> stacks = new ArrayList<>();
        while (count > 0)
        {
            final int size = Math.min(count, maxStackSize);
            stacks.add(template.copyWithCount(size));
            count -= size;
        }
        return stacks;
    }
}
