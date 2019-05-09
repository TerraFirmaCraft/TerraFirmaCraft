/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

public class ItemProPick extends ItemMetalTool
{
    private BlockPos activeBlockPos = null;
    private int timesUsedOnBlock = 0;
    private final static int cooldown = 40;
    private Random random = new Random();

    public ItemProPick(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState bState = worldIn.getBlockState(pos);

        if (hand != EnumHand.MAIN_HAND || facing == null || !isValidProspectBlock(bState))
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

        // Play a block hit sound, reduce dirt block volume because its loud on default.
        SoundType soundType = bState.getBlock().getSoundType(bState, worldIn, pos, player);
        if (soundType.equals(SoundType.GROUND))
            worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume() - 0.5F, soundType.getPitch() - 0.5F);
        else
            worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume(), soundType.getPitch());

        if (!worldIn.isRemote)
        {
            ItemStack itemStack = getOreDrop(bState, true);
            if (itemStack != null)
            {
                //Current block is ore, tell result
                player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found").appendText(" " + itemStack.getDisplayName()),true);
                player.getHeldItem(hand).damageItem(1, player);
                setCooldown(player);
            }
            else
            {
                //Hit block X times to get a search result.
                if (activeBlockPos == null || !activeBlockPos.equals(pos))
                {
                    activeBlockPos = pos;
                    timesUsedOnBlock = 0;
                }

                timesUsedOnBlock += 1;

                if (timesUsedOnBlock >= 3)
                {
                    activeBlockPos = null;
                    timesUsedOnBlock = 0;

                    Map<String, ProspectResult> results = scanSurroundingBlocks(worldIn, pos, false);

                    if (results.isEmpty())
                    {
                        //Found nothing, tell nothing
                        player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found_nothing"),true);
                        setCooldown(player);
                    }
                    else
                    {
                        random.setSeed(pos.toLong());

                        ProspectResult[] list = results.values().toArray(new ProspectResult[0]);
                        int index = results.size()== 1 ? 0 : random.nextInt(results.size()-1);
                        double score = list[index].score;

                        String msg;
                        if (score < 10)
                            msg = "tfc.propick.found_traces";
                        else if (score < 20)
                            msg = "tfc.propick.found_small";
                        else if (score < 40)
                            msg = "tfc.propick.found_medium";
                        else if (score < 80)
                            msg = "tfc.propick.found_large";
                        else
                            msg = "tfc.propick.found_very_large";

                        player.sendStatusMessage(new TextComponentTranslation(msg).appendText(" ").appendSibling(new TextComponentTranslation(list[index].ore.getDisplayName())), true);
                        setCooldown(player);
                        player.getHeldItem(hand).damageItem(1, player);

                        if (ConfigTFC.GENERAL.debug)
                            for (int i = 0; i < results.size(); i++)
                                player.sendStatusMessage(new TextComponentString(list[i].ore.getDisplayName() + ": " + String.format("%.02f", list[i].score)), false);
                    }
                }
            }
        }
        else //client side, add hit particles
        {
            addHitBlockParticle(worldIn, pos, facing, bState);
        }

        return EnumActionResult.SUCCESS;
    }

    private Map<String, ProspectResult> scanSurroundingBlocks(World world, BlockPos center, boolean getGrade)
    {
        Map<String, ProspectResult> results = new HashMap<>();
        int rad = 12;
        int step = 1;
        // Loop trough every block in a 25^3 cube
        for (int x = -rad; x <= rad; x+=step)
        {
            for (int y = -rad; y <= rad; y+=step)
            {
                for (int z = -rad; z <= rad; z+=step)
                {
                    BlockPos pos = new BlockPos(x, y, z).add(center);
                    ItemStack iStack = getOreDrop(world.getBlockState(pos), getGrade);

                    if (iStack != null)
                    {
                        String oreName = iStack.getDisplayName();

                        if (results.containsKey(oreName))
                            results.get(oreName).score += 1;
                        else
                            results.put(oreName, new ProspectResult(iStack, 1));
                    }
                }
            }
        }
        return results;
    }

    // todo: 1.13/14 block metadata
    private ItemStack getOreDrop(IBlockState blockState, boolean getGrade)
    {
        if (blockState == null || BlocksTFC.isGround(blockState)) return null;
        for (VeinType vein : VeinRegistry.INSTANCE.getVeins().values())
            if (vein.isOreBlock(blockState))
            {
                Block block = blockState.getBlock();
                if (vein.ore != null)
                    if (vein.ore.isGraded() && getGrade)
                        return new ItemStack(block.getItemDropped(blockState, null, 0), 1, block.getMetaFromState(blockState));
                    else
                        return new ItemStack(block.getItemDropped(blockState, null, 0), 1, 0);
                else
                    return new ItemStack(Item.getItemFromBlock(block), 1, block.getMetaFromState(blockState));
            }
        return null;
    }

    private boolean isValidProspectBlock(IBlockState state)
    {
        return BlocksTFC.isGround(state) || (getOreDrop(state, false) != null);
    }

    private void setCooldown(EntityPlayer player)
    {
        player.getCooldownTracker().setCooldown(this, cooldown);
    }

    private void addHitBlockParticle(World world, BlockPos pos, EnumFacing side, IBlockState state)
    {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
        for (int i = 0; i < 2; i++)
        {
            double d0 = (double)x + random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2D) + 0.1D + axisalignedbb.minX;
            double d1 = (double)y + random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2D) + 0.1D + axisalignedbb.minY;
            double d2 = (double)z + random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2D) + 0.1D + axisalignedbb.minZ;

            switch (side)
            {
                case WEST:
                    d0 = (double)x + axisalignedbb.minX - 0.1D;
                    break;
                case EAST:
                    d0 = (double)x + axisalignedbb.maxX + 0.1D;
                    break;
                case DOWN:
                    d1 = (double)y + axisalignedbb.minY - 0.1D;
                    break;
                case UP:
                    d1 = (double)y + axisalignedbb.maxY + 0.1D;
                    break;
                case NORTH:
                    d2 = (double)z + axisalignedbb.minZ - 0.1D;
                    break;
                case SOUTH:
                    d2 = (double)z + axisalignedbb.maxZ + 0.1D;
                    break;
            }

            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
        }
    }

    private class ProspectResult
    {
        ItemStack ore;
        double score;

        ProspectResult(ItemStack itemStack, double num)
        {
            ore = itemStack;
            score = num;
        }

    }
}
