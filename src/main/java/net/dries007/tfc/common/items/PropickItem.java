/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.network.ProspectedPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.ProspectedEvent;

public class PropickItem extends ToolItem
{
    public static final int RADIUS = 12;
    public static final int COOLDOWN = 10;

    private static final Map<Block, Block> REPRESENTATIVE_BLOCKS = new IdentityHashMap<>();

    /**
     * Marks a certain block as being a "representative" block of others. This is used to collect similar ores in the same result before returning prospector pick results, i.e. different grades of metal ores.
     * In TFC, this is used to count rich/normal/poor ores all together.
     * <p>
     * This function is safe to call during parallel mod loading.
     */
    public static synchronized void registerRepresentative(Block representative, Block... blocks)
    {
        for (Block block : blocks)
        {
            REPRESENTATIVE_BLOCKS.put(block, representative);
        }
    }

    /**
     * @return The representative block of {@code block}, or the block itself if it has no representative.
     */
    public static Block getRepresentative(Block block)
    {
        return REPRESENTATIVE_BLOCKS.getOrDefault(block, block);
    }

    public static void registerDefaultRepresentativeBlocks()
    {
        TFCBlocks.GRADED_ORES.forEach((rock, ores) -> ores.forEach((ore, blocks) -> registerRepresentative(
            blocks.get(Ore.Grade.NORMAL).get(),
            blocks.get(Ore.Grade.RICH).get(),
            blocks.get(Ore.Grade.POOR).get()
        )));
    }

    public static Object2IntMap<Block> scanAreaFor(Level level, BlockPos center, int radius, TagKey<Block> tag)
    {
        final Object2IntMap<Block> results = new Object2IntOpenHashMap<>();
        for (BlockPos cursor : BlockPos.betweenClosed(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius))
        {
            final Block block = getRepresentative(level.getBlockState(cursor).getBlock());
            if (Helpers.isBlock(block, tag))
            {
                results.mergeInt(block, 1, Integer::sum);
            }
        }
        return results;
    }

    private final float falseNegativeChance;

    public PropickItem(LevelTier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_PROPICK, properties);

        this.falseNegativeChance = 0.3f - Mth.clamp(tier.level(), 0, 5) * (0.3f / 5f);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);

        if (player instanceof ServerPlayer serverPlayer)
        {
            final SoundType sound = state.getSoundType(level, pos, player);
            final Random random = new Random();

            level.playSound(player, pos, sound.getHitSound(), SoundSource.PLAYERS, sound.getVolume(), sound.getPitch());

            Helpers.damageItem(context.getItemInHand(), player, context.getHand());
            player.getCooldowns().addCooldown(this, COOLDOWN);

            ProspectResult result;
            Block found = state.getBlock();
            random.setSeed(Helpers.hash(19827384739241223L, pos));
            if (Helpers.isBlock(state, TFCTags.Blocks.PROSPECTABLE))
            {
                // Found
                result = ProspectResult.FOUND;
            }
            else if (random.nextFloat() < falseNegativeChance)
            {
                // False Negative (Nothing)
                result = ProspectResult.NOTHING;
            }
            else
            {
                final Object2IntMap<Block> states = scanAreaFor(level, pos, RADIUS, TFCTags.Blocks.PROSPECTABLE);
                if (states.isEmpty())
                {
                    // Nothing
                    result = ProspectResult.NOTHING;
                }
                else
                {
                    // Found Traces
                    final ArrayList<Block> stateKeys = new ArrayList<>(states.keySet());
                    found = stateKeys.get(random.nextInt(stateKeys.size()));
                    final int amount = states.getOrDefault(found, 1);

                    if (amount < 10) result = ProspectResult.TRACES;
                    else if (amount < 20) result = ProspectResult.SMALL;
                    else if (amount < 40) result = ProspectResult.MEDIUM;
                    else if (amount < 80) result = ProspectResult.LARGE;
                    else result = ProspectResult.VERY_LARGE;
                }
            }

            NeoForge.EVENT_BUS.post(new ProspectedEvent(player, result, found));
            PacketDistributor.sendToPlayer(serverPlayer, new ProspectedPacket(found, result));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        if (flag.isAdvanced())
        {
            tooltip.add(Component.translatable("tfc.tooltip.propick.accuracy", (int) (100 * (1 - falseNegativeChance))).withStyle(ChatFormatting.GRAY));
        }
    }
}