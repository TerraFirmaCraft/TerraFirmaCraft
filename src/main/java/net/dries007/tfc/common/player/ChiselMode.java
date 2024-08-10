/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.IngameOverlays;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public abstract class ChiselMode
{
    public static final ResourceKey<Registry<ChiselMode>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("chisel_mode"));
    public static final DefaultedRegistry<ChiselMode> REGISTRY = (DefaultedRegistry<ChiselMode>) new RegistryBuilder<>(KEY)
        .sync(true)
        .defaultKey(Helpers.identifier("smooth"))
        .create();
    public static final DeferredRegister<ChiselMode> MODES = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    /**
     * Populates the chisel mode order. These are ordered by priority, then by name, then the {@link #next} field is assigned
     * to each based on the computed order. Addons desiring to register additional chisel modes may change the priority, keeping
     * in mind the existing TFC priorities.
     */
    public static void setupOrdering()
    {
        final List<ChiselMode> order = REGISTRY.entrySet()
            .stream()
            .sorted(Comparator.<Map.Entry<ResourceKey<ChiselMode>, ChiselMode>>comparingInt(e -> e.getValue().priority)
                .thenComparing(Map.Entry::getKey))
            .map(Map.Entry::getValue)
            .toList();
        for (int i = 0; i < order.size(); i++)
        {
            order.get(i == 0 ? order.size() - 1 : i - 1).next = order.get(i);
        }
    }

    public static final DeferredHolder<ChiselMode, ChiselMode> SMOOTH = register("smooth", new ChiselMode(0) {
        @Override
        public BlockState modifyStateForPlacement(BlockState state, BlockState chiseled, Player player, BlockHitResult hit)
        {
            return state;
        }

        @Override
        public <T> T createIcon(IconCallback<T> callback)
        {
            return callback.accept(IngameOverlays.TEXTURE, 0, 58, 20, 20);
        }

        @Override
        public void createHotbarIcon(HotbarIconCallback callback)
        {
            callback.accept(IngameOverlays.TEXTURE, 0, 58);
        }
    });
    public static final DeferredHolder<ChiselMode, ChiselMode> SLAB = register("slab", new ChiselMode(100) {
        @Override
        @Nullable
        public BlockState modifyStateForPlacement(BlockState state, BlockState chiseled, Player player, BlockHitResult hit)
        {
            // Slabs run into an issue where, chiseling adjacent to a slab, with the placement context, infers it to be a double
            // slab (wrong + duplication glitch). So, we copy the slab contextual placement and write it correctly - there's no
            // good way to perform this simulation without modifying the world, or having a whole simulation world which I do
            // *not* want to do. Copied from SlabBlock.getStateForPlacement, but avoids placing double slabs
            final Direction hitFace = hit.getDirection();
            final SlabType slabType = hitFace != Direction.DOWN && (hitFace == Direction.UP || !(hit.getLocation().y - hit.getBlockPos().getY() > 0.5D))
                ? SlabType.BOTTOM
                : SlabType.TOP;

            chiseled = chiseled.setValue(SlabBlock.TYPE, slabType);
            chiseled = FluidHelpers.fillWithFluid(chiseled, state.getFluidState().getType());
            return chiseled;
        }

        @Override
        public <T> T createIcon(IconCallback<T> callback)
        {
            return callback.accept(IngameOverlays.TEXTURE, 40, 58, 20, 20);
        }

        @Override
        public void createHotbarIcon(HotbarIconCallback callback)
        {
            callback.accept(IngameOverlays.TEXTURE, 40, 58);
        }
    });
    public static final DeferredHolder<ChiselMode, ChiselMode> STAIR = register("stair", new ChiselMode(200) {
        @Override
        @Nullable
        public BlockState modifyStateForPlacement(BlockState state, BlockState chiseled, Player player, BlockHitResult hit)
        {
            // We use `getStateForPlacement`. This is NOT ACCURATE, as the block in question is querying the wrong world position for
            // i.e. the fluid position. We can fix this, however, after the fact. Stairs also don't have any issues with placing on
            // top of one another. Just sanity check we are only calling this method for `StairBlock`s
            if (chiseled.getBlock() instanceof StairBlock stair)
            {
                // Use the stair placement state, but fill with fluid after the fact
                chiseled = stair.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(stair), hit));
                if (chiseled != null)
                {
                    chiseled = FluidHelpers.fillWithFluid(chiseled, state.getFluidState().getType());
                }
            }
            return chiseled;
        }

        @Override
        public <T> T createIcon(IconCallback<T> callback)
        {
            return callback.accept(IngameOverlays.TEXTURE, 20, 58, 20, 20);
        }

        @Override
        public void createHotbarIcon(HotbarIconCallback callback)
        {
            callback.accept(IngameOverlays.TEXTURE, 20, 58);
        }
    });

    private static <T extends ChiselMode> DeferredHolder<ChiselMode, T> register(String name, T mode)
    {
        return MODES.register(name, () -> mode);
    }

    private final int priority;
    private @Nullable ChiselMode next = null; // Initialized by setup()

    protected ChiselMode(int priority)
    {
        this.priority = priority;
    }

    public ChiselMode next()
    {
        return Objects.requireNonNull(next);
    }

    /**
     * When a chisel recipe is being performed, this allows modifying the state placed, based on the chisel mode. It is used in
     * TFC for slabs and stairs, which have contextually-dependent state placement.
     * @param state The original block state
     * @param chiseled The state the recipe wishes to place
     * @param player The player doing the chiseling
     * @param hit The hit of the player targeting the block
     * @return The new chiseled state, or {@code null} if the placement is not possible
     */
    @Nullable
    public abstract BlockState modifyStateForPlacement(BlockState state, BlockState chiseled, Player player, BlockHitResult hit);

    /**
     * Used to interact with JEI. Since this is independent of JEI being present, we don't require that the JEI API exists here,
     * however, a chisel mode must be able to create an icon.
     */
    public abstract <T> T createIcon(IconCallback<T> callback);

    public interface IconCallback<T>
    {
        T accept(ResourceLocation texture, int u, int v, int width, int height);
    }

    /**
     * Used to render an icon for the current chisel mode in the hotbar. This is client-only, and is just forwarded to a
     * {@code graphics.blit} call.
     * @see IngameOverlays#CHISEL
     */
    public abstract void createHotbarIcon(HotbarIconCallback callback);

    public interface HotbarIconCallback
    {
        void accept(ResourceLocation texture, int u, int v);
    }
}
