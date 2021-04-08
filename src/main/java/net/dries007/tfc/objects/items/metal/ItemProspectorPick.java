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
import javax.annotation.ParametersAreNonnullByDefault;

import net.dries007.tfc.api.events.ProspectEvent;
import net.dries007.tfc.network.PacketProspectResult;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.util.skills.ProspectingSkill;
import net.dries007.tfc.util.skills.SkillType;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

import net.minecraftforge.common.MinecraftForge;

@ParametersAreNonnullByDefault
public class ItemProspectorPick extends ItemMetalTool
{
    private static final int PROSPECT_RADIUS = 12;
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
                ProspectEvent event;
                float falseNegativeChance = 0.3f; //Classic value was random(100) >= (60 + rank)
                ProspectingSkill skill = CapabilityPlayerData.getSkill(player, SkillType.PROSPECTING);
                if (skill != null)
                {
                    falseNegativeChance = 0.3f - (0.1f * skill.getTier().ordinal());
                }

                // Damage item and set cooldown
                player.getHeldItem(hand).damageItem(1, player);
                player.getCooldownTracker().setCooldown(this, COOLDOWN);

                /**
                 * We fix a terrible case of Random seeding with dx,dy=0 and small dz resulting in issue #1736
                 * where propick false negatives were rows in the z-axis, especially at 0 skill. setSeed() only uses 48 bits
                 * of pos.toLong(). Solved this by multiplying coordinates by primes and XOR's results. Verified produces
                 * more "random" results.
                 */
                RANDOM.setSeed((pos.getX() * 92853) ^ (pos.getY() * 1959302) ^ (pos.getZ() * 2839402));
                ItemStack targetStack = getOreStack(worldIn, pos, state, false);
                if (!targetStack.isEmpty())
                {
                    // Just clicked on an ore block
                    event = new ProspectEvent.Server(player, pos, ProspectResult.Type.FOUND, targetStack);

                    // Increment skill
                    if (skill != null)
                    {
                        skill.addSkill(pos);
                    }
                }
                else if (RANDOM.nextFloat() < falseNegativeChance)
                {
                    // False negative
                    event = new ProspectEvent.Server(player, pos, ProspectResult.Type.NOTHING, null);
                }
                else
                {
                    Collection<ProspectResult> results = scanSurroundingBlocks(worldIn, pos);
                    if (results.isEmpty())
                    {
                        // Found nothing
                        event = new ProspectEvent.Server(player, pos, ProspectResult.Type.NOTHING, null);
                    }
                    else
                    {
                        // Found something
                        ProspectResult result = (ProspectResult) results.toArray()[RANDOM.nextInt(results.size())];
                        event = new ProspectEvent.Server(player, pos, result.getType(), result.ore);

                        if (ConfigTFC.General.DEBUG.enable)
                        {
                            for (ProspectResult debugResult : results)
                            {
                                TerraFirmaCraft.getLog().debug(debugResult.ore.getDisplayName() + ": " + String.format("%.02f", debugResult.score));
                            }
                        }
                    }
                }

                MinecraftForge.EVENT_BUS.post(event);
                PacketProspectResult packet = new PacketProspectResult(event.getBlockPos(), event.getResultType(), event.getVein());
                TerraFirmaCraft.getNetwork().sendTo(packet, (EntityPlayerMP) player);
            }
            else
            {
                //client side, add hit particles
                addHitBlockParticle(worldIn, pos, facing, state);
            }
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
            ItemStack stack = getOreStack(world, pos, world.getBlockState(pos), true);
            if (!stack.isEmpty())
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

    @Nonnull
    private ItemStack getOreStack(World world, BlockPos pos, IBlockState state, boolean ignoreGrade)
    {
        for (VeinType vein : VeinRegistry.INSTANCE.getVeins().values())
        {
            if (vein.isOreBlock(state))
            {
                Block block = state.getBlock();
                if (vein.getOre() != null && vein.getOre().isGraded() && !ignoreGrade)
                {
                    ItemStack result = block.getPickBlock(state, null, world, pos, null);
                    result.setItemDamage(Ore.Grade.NORMAL.getMeta()); // ignore grade
                    return result;
                }
                else
                {
                    return block.getPickBlock(state, null, world, pos, null);
                }
            }
        }
        return ItemStack.EMPTY;
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

    public static final class ProspectResult
    {
        private final ItemStack ore;
        private double score;

        ProspectResult(ItemStack itemStack, double num)
        {
            ore = itemStack;
            score = num;
        }

        public Type getType()
        {
            if (score < 10)
            {
                return Type.TRACES;
            }
            else if (score < 20)
            {
                return Type.SMALL;
            }
            else if (score < 40)
            {
                return Type.MEDIUM;
            }
            else if (score < 80)
            {
                return Type.LARGE;
            }
            else
            {
                return Type.VERY_LARGE;
            }
        }

        public enum Type
        {
            VERY_LARGE("tfc.propick.found_very_large"),
            LARGE("tfc.propick.found_large"),
            MEDIUM("tfc.propick.found_medium"),
            SMALL("tfc.propick.found_small"),
            TRACES("tfc.propick.found_traces"),

            FOUND("tfc.propick.found"),         // right click on block
            NOTHING("tfc.propick.found_nothing"); // nothing interesting here

            private static final Type[] VALUES = values();
            public final String translation;

            Type(String translation)
            {
                this.translation = translation;
            }

            public static Type valueOf(int ordinal)
            {
                return VALUES[ordinal];
            }
        }
    }
}
