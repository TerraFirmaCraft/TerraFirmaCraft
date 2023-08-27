package net.dries007.tfc.common.blockentities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.HotPouredGlassBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class HotPouredGlassBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, HotPouredGlassBlockEntity glass)
    {
        glass.checkForLastTickSync();
        if (!glass.initialized)
        {
            return;
        }
        if (glass.animationTicks > 0)
        {
            glass.animationTicks--;
        }
        else
        {
            if (glass.isInitialTransition)
            {
                glass.isInitialTransition = false;
                glass.markForSync();
                if (glass.capacity > 0)
                {
                    floodfill(level, pos, state, glass);
                }
            }
            else
            {
                level.setBlockAndUpdate(pos, glass.getInternalBlock());
                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                final var random = level.getRandom();
                Supplier<Vec3> supplier = () -> new Vec3(Mth.nextDouble(level.getRandom(), -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F));
                ParticleUtils.spawnParticlesOnBlockFace(level, pos.below(), ParticleTypes.SMOKE, UniformInt.of(4, 10), Direction.UP, supplier, 0.6);
            }
        }
    }

    private static void floodfill(Level level, BlockPos pos, BlockState state, HotPouredGlassBlockEntity center)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Set<BlockPos> filled = new HashSet<>();
        final LinkedList<BlockPos> queue = new LinkedList<>();

        filled.add(pos);
        queue.addFirst(pos);

        while (!queue.isEmpty())
        {
            BlockPos posAt = queue.removeFirst();
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                cursor.set(posAt).move(direction);
                if (!filled.contains(cursor))
                {
                    if (center.capacity > 0 && canFloodFillAt(level, cursor))
                    {
                        // Valid flood fill location
                        BlockPos posNext = cursor.immutable();
                        queue.addFirst(posNext);
                        filled.add(posNext);
                        center.capacity -= 1;
                    }
                }
            }
        }

        for (BlockPos fillPos : filled)
        {
            level.setBlockAndUpdate(fillPos, state.setValue(HotPouredGlassBlock.FLAT, true));
            if (level.getBlockEntity(fillPos) instanceof HotPouredGlassBlockEntity side)
            {
                side.isInitialTransition = false;
                side.animationTicks = 40 + (fillPos.distManhattan(pos) * 10);
                side.initialized = true;
                side.setGlassItem(center.getGlassItem().copy());
                side.markForSync();
            }
        }
    }

    private static boolean canFloodFillAt(Level level, BlockPos.MutableBlockPos cursor)
    {
        if (level.getBlockState(cursor).isAir())
        {
            cursor.move(0, -1, 0);
            if (Helpers.isBlock(level.getBlockState(cursor), TFCTags.Blocks.GLASS_POURING_TABLE))
            {
                cursor.move(0, 1, 0);
                return true;
            }
            cursor.move(0, 1, 0);
        }
        return false;
    }

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.hot_poured_glass");

    private int capacity = 0;
    private boolean isInitialTransition = true;
    private int animationTicks = 0;
    private boolean initialized = false;

    public HotPouredGlassBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.HOT_POURED_GLASS.get(), pos, state);
    }

    public HotPouredGlassBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        capacity = nbt.getInt("capacity");
        isInitialTransition = nbt.getBoolean("isInitialTransition");
        animationTicks = nbt.getInt("animationTicks");
        initialized = nbt.getBoolean("initialized");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("capacity", capacity);
        nbt.putBoolean("isInitialTransition", isInitialTransition);
        nbt.putInt("animationTicks", animationTicks);
        nbt.putBoolean("initialized", initialized);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof BlockItem;
    }

    public void setGlassItem(ItemStack stack)
    {
        inventory.setStackInSlot(0, stack);
    }

    public void flattenFirstBlock()
    {
        animationTicks = 20;
        initialized = true;
        capacity = 16;
        markForSync();
    }

    private ItemStack getGlassItem()
    {
        return inventory.getStackInSlot(0);
    }

    private BlockState getInternalBlock()
    {
        return ((BlockItem) inventory.getStackInSlot(0).getItem()).getBlock().defaultBlockState();
    }

    public boolean isInitialTransition()
    {
        return isInitialTransition;
    }

    public int getAnimationTicks()
    {
        return animationTicks;
    }

    public boolean isInitialized()
    {
        return initialized;
    }
}