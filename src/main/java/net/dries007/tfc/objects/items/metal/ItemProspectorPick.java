/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public class ItemProspectorPick extends ItemMetalTool
{
    private static final int PROSPECT_RADIUS = 12;
    // todo: balance. 40 ticks feels really long, especially for the preciseness you want from the propick
    private static final int COOLDOWN = 10;
    private static final Random RANDOM = new Random();

    public ItemProspectorPick(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, @Nullable EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (facing != null)
        {
            SoundType soundType = state.getBlock().getSoundType(state, worldIn, pos, player);
            worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, 1.0f, soundType.getPitch());

            if (!worldIn.isRemote)
            {
                // Damage item and set cooldown
                player.getHeldItem(hand).damageItem(1, player);
                player.getCooldownTracker().setCooldown(this, COOLDOWN);

                RANDOM.setSeed(pos.toLong());
                ItemStack targetStack = getOreStack(state, false);
                if (targetStack != null)
                {
                    // Just clicked on an ore block
                    player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found").appendText(" " + targetStack.getDisplayName()), ConfigTFC.CLIENT.propickOutputToActionBar);
                }
                else if (RANDOM.nextFloat() < 0.4)
                {
                    // False negative
                    player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found_nothing"), ConfigTFC.CLIENT.propickOutputToActionBar);
                }
                else
                {
                    Collection<ProspectResult> results = scanSurroundingBlocks(worldIn, pos);
                    if (results.isEmpty())
                    {
                        // Found nothing
                        player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found_nothing"), ConfigTFC.CLIENT.propickOutputToActionBar);
                    }
                    else
                    {
                        // Found something
                        ProspectResult result = (ProspectResult) results.toArray()[RANDOM.nextInt(results.size())];

                        String translationKey;
                        if (result.score < 10)
                        {
                            translationKey = "tfc.propick.found_traces";
                        }
                        else if (result.score < 20)
                        {
                            translationKey = "tfc.propick.found_small";
                        }
                        else if (result.score < 40)
                        {
                            translationKey = "tfc.propick.found_medium";
                        }
                        else if (result.score < 80)
                        {
                            translationKey = "tfc.propick.found_large";
                        }
                        else
                        {
                            translationKey = "tfc.propick.found_very_large";
                        }

                        player.sendStatusMessage(new TextComponentTranslation(translationKey).appendText(" ").appendSibling(new TextComponentTranslation(result.ore.getDisplayName())), ConfigTFC.CLIENT.propickOutputToActionBar);

                        if (ConfigTFC.GENERAL.debug)
                        {
                            for (int i = 0; i < results.size(); i++)
                            {
                                player.sendStatusMessage(new TextComponentString(result.ore.getDisplayName() + ": " + String.format("%.02f", result.score)), false);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            //client side, add hit particles
            addHitBlockParticle(worldIn, pos, facing, state);
        }

        return EnumActionResult.SUCCESS;
    }

    /**
     * Loops through every block in a 25x25x25 cube around the center
     *
     * @param world  The world
     * @param center The center position
     * @return the collection of results
     */
    @Nonnull
    private Collection<ProspectResult> scanSurroundingBlocks(World world, BlockPos center)
    {
        Map<String, ProspectResult> results = new HashMap<>();
        for (BlockPos.MutableBlockPos pos : BlockPos.MutableBlockPos.getAllInBoxMutable(center.add(-PROSPECT_RADIUS, -PROSPECT_RADIUS, -PROSPECT_RADIUS), center.add(PROSPECT_RADIUS, PROSPECT_RADIUS, PROSPECT_RADIUS)))
        {
            ItemStack stack = getOreStack(world.getBlockState(pos), true);
            if (stack != null)
            {
                String oreName = stack.getDisplayName();
                if (results.containsKey(oreName))
                {
                    results.get(oreName).score += 1;
                }
                else
                {
                    results.put(oreName, new ProspectResult(stack, 1));
                }
            }
        }
        return results.values();
    }

    @Nullable
    private ItemStack getOreStack(IBlockState blockState, boolean ignoreGrade)
    {
        if (blockState == null || BlocksTFC.isGround(blockState))
        {
            return null;
        }
        for (VeinType vein : VeinRegistry.INSTANCE.getVeins().values())
            if (vein.isOreBlock(blockState))
            {
                Block block = blockState.getBlock();
                if (vein.ore != null)
                    if (vein.ore.isGraded() && !ignoreGrade)
                        return new ItemStack(block.getItemDropped(blockState, null, 0), 1, block.getMetaFromState(blockState));
                    else
                        return new ItemStack(block.getItemDropped(blockState, null, 0), 1, 0);
                else
                    return new ItemStack(Item.getItemFromBlock(block), 1, block.getMetaFromState(blockState));
            }
        return null;
    }

    private void addHitBlockParticle(World world, BlockPos pos, EnumFacing side, IBlockState state)
    {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
        for (int i = 0; i < 2; i++)
        {
            double xOffset = x + RANDOM.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2D) + 0.1D + axisalignedbb.minX;
            double yOffset = y + RANDOM.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2D) + 0.1D + axisalignedbb.minY;
            double zOffset = z + RANDOM.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2D) + 0.1D + axisalignedbb.minZ;

            switch (side)
            {
                case WEST:
                    xOffset = x + axisalignedbb.minX - 0.1D;
                    break;
                case EAST:
                    xOffset = x + axisalignedbb.maxX + 0.1D;
                    break;
                case DOWN:
                    yOffset = y + axisalignedbb.minY - 0.1D;
                    break;
                case UP:
                    yOffset = y + axisalignedbb.maxY + 0.1D;
                    break;
                case NORTH:
                    zOffset = z + axisalignedbb.minZ - 0.1D;
                    break;
                case SOUTH:
                    zOffset = z + axisalignedbb.maxZ + 0.1D;
                    break;
            }

            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
        }
    }

    private static final class ProspectResult
    {
        private final ItemStack ore;
        private double score;

        ProspectResult(ItemStack itemStack, double num)
        {
            ore = itemStack;
            score = num;
        }

    }
}
