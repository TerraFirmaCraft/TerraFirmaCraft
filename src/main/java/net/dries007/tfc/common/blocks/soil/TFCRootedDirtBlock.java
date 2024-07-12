/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

/**
 * Dirt that doesn't let grass spread to it but can still be interacted on
 */
public class TFCRootedDirtBlock extends Block implements IMudBlock
{
    private final Supplier<? extends Block> dirt;
    @Nullable private final Supplier<? extends Block> mud;

    public TFCRootedDirtBlock(Properties properties, Supplier<? extends Block> dirt, @Nullable Supplier<? extends Block> mud)
    {
        super(properties);
        this.dirt = dirt;
        this.mud = mud;
    }

    TFCRootedDirtBlock(Properties properties, SoilBlockType dirtType, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(dirtType), variant.getBlock(SoilBlockType.MUD));
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
        if (context.getItemInHand().canPerformAction(action) && action == ItemAbilities.HOE_TILL && TFCConfig.SERVER.enableRootedDirtToDirtCreation.get())
        {
            return dirt.get().defaultBlockState();
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (mud != null)
        {
            return transformToMud(mud.get().defaultBlockState(), level, pos, player, hand);
        }

        return InteractionResult.PASS;
    }
}
