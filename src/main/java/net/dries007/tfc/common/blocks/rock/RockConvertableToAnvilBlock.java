/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.registry.RegistryRock;

public class RockConvertableToAnvilBlock extends RawRockBlock
{
    public static Block createForIgneousOnly(Properties properties, RegistryRock rock, boolean naturallySupported)
    {
        return rock.category() == RockCategory.IGNEOUS_EXTRUSIVE || rock.category() == RockCategory.IGNEOUS_INTRUSIVE
            ? new RockConvertableToAnvilBlock(properties, rock.getAnvil(), naturallySupported, rock.displayCategory().createTooltip())
            : new RawRockBlock(properties, naturallySupported, rock.displayCategory().createTooltip());
    }

    private final Supplier<? extends Block> anvil;

    public RockConvertableToAnvilBlock(Properties properties, Supplier<? extends Block> anvil, boolean naturallySupported, @Nullable Component rockTypeTooltip)
    {
        super(properties, naturallySupported, rockTypeTooltip);
        this.anvil = anvil;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (Helpers.isItem(stack, TFCTags.Items.HAMMERS)
            && !Helpers.isItem(player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND), TFCTags.Items.CHISELS)
            && hitResult.getDirection() == Direction.UP
            && level.getBlockState(pos.above()).isAir()
        )
        {
            final BlockState block = anvil.get().defaultBlockState();
            level.setBlockAndUpdate(pos, block);
            if (player instanceof ServerPlayer serverPlayer)
            {
                TFCAdvancements.ROCK_ANVIL.trigger(serverPlayer, block);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
