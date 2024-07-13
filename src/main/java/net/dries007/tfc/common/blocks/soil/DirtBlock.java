/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

public class DirtBlock extends Block implements IDirtBlock, IMudBlock
{
    public static boolean emptyBlockAbove(UseOnContext context)
    {
        return context.getClickedFace() != Direction.DOWN && FluidHelpers.isAirOrEmptyFluid(context.getLevel().getBlockState(context.getClickedPos().above()));
    }

    private final Supplier<? extends Block> grass;
    @Nullable private final Supplier<? extends Block> path;
    @Nullable private final Supplier<? extends Block> farmland;
    private final Supplier<? extends Block> rooted;
    private final Supplier<? extends Block> mud;

    public DirtBlock(Properties properties, Supplier<? extends Block> grass, @Nullable Supplier<? extends Block> path, @Nullable Supplier<? extends Block> farmland, Supplier<? extends Block> rooted, Supplier<? extends Block> mud)
    {
        super(properties);

        this.grass = grass;
        this.path = path;
        this.farmland = farmland;
        this.rooted = rooted;
        this.mud = mud;
    }

    DirtBlock(Properties properties, SoilBlockType grassType, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(grassType), variant.getBlock(SoilBlockType.GRASS_PATH), variant.getBlock(SoilBlockType.FARMLAND), variant.getBlock(SoilBlockType.ROOTED_DIRT), variant.getBlock(SoilBlockType.MUD));
    }

    public BlockState getGrass()
    {
        return grass.get().defaultBlockState();
    }

    public BlockState getRooted()
    {
        return rooted.get().defaultBlockState();
    }

    @Override
    public BlockState getMud()
    {
        return mud.get().defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action))
        {
            if (action == ItemAbilities.SHOVEL_FLATTEN && path != null && TFCConfig.SERVER.enableGrassPathCreation.get())
            {
                return path.get().defaultBlockState();
            }
            if (action == ItemAbilities.HOE_TILL && farmland != null && TFCConfig.SERVER.enableFarmlandCreation.get() && DirtBlock.emptyBlockAbove(context))
            {
                return farmland.get().defaultBlockState();
            }
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return transformToMud(mud.get().defaultBlockState(), level, pos, player, hand);
    }
}