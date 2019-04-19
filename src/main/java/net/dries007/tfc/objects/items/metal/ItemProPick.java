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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;

import static net.dries007.tfc.objects.blocks.stone.BlockOreTFC.GRADE;

public class ItemProPick extends ItemMetalTool
{
    private BlockPos activeBlockPos = null;
    private int timesUsedOnBlock = 0;
    private final static int cooldown = 40;
    private Random random = new Random();
    private float efficiency;
    private Map<String, ProspectResult> results = new HashMap<>();

    public ItemProPick(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        efficiency = material.getEfficiency() * 0.5F;
    }

    /**
     * Fragile backup pick, take extra durability damage and reduce speed
     */
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote && (double) state.getBlockHardness(worldIn, pos) != 0.0D)
        {
            stack.damageItem(5, entityLiving);
        }
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        return canHarvestBlock(state, stack) ? efficiency : 1.0f;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState bState = worldIn.getBlockState(pos);

        if (hand != EnumHand.MAIN_HAND || facing == null || !isValidProspectBlock(bState))
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

        // Play a block hit sound, reduce dirt block volume because its loud on default.
        SoundType soundType = bState.getBlock().getSoundType(bState, worldIn, pos, player);
        if (soundType.equals(SoundType.GROUND))
            worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume()-0.5F, soundType.getPitch()-0.5F); //Think this sends to all except player
        else
            worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume(), soundType.getPitch()); //Think this sends to all except player

        if (!worldIn.isRemote)
        {
            ItemStack itemStack = getOreBlock(bState);
            if (itemStack != null)
            {
                //Current block is ore, tell result
                player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found").appendText(" " + itemStack.getDisplayName()),true);
                player.getHeldItem(hand).damageItem(1, player);
                setCooldown(player);
            }
            else
            {
                //Hit block 5 times to get a search result.
                if (activeBlockPos == null || !activeBlockPos.equals(pos))
                {
                    activeBlockPos = pos;
                    timesUsedOnBlock = 0;
                }

                timesUsedOnBlock += 1;
                player.getHeldItem(hand).damageItem(1, player);

                if (timesUsedOnBlock >= 5)
                {
                    activeBlockPos = null;
                    timesUsedOnBlock = 0;

                    scanSurroundingBlocks(worldIn, pos);

                    if (results.isEmpty())
                    {
                        //Found nothing, tell nothing
                        player.sendStatusMessage(new TextComponentTranslation("tfc.propick.found_nothing"),true);
                        setCooldown(player);
                    }
                    else
                    {
                        ProspectResult[] list = results.values().toArray(new ProspectResult[0]);
                        // todo: made show grades/multiple results if skilled prospector
                        int index = results.size()== 1 ? 0 : random.nextInt(results.size()-1);
                        double score = list[index].score;
                        //remove grade from ore
                        String trKey = new ItemStack(list[index].ore.getItem(),1,0).getTranslationKey() + ".name";

                        ITextComponent msg;
                        if (score < 20)
                            msg = new TextComponentTranslation("tfc.propick.found_signs");
                        else if (score < 40)
                            msg = new TextComponentTranslation("tfc.propick.found_traces").appendText(" ").appendSibling(new TextComponentTranslation(trKey));
                        else if (score < 80)
                            msg = new TextComponentTranslation("tfc.propick.found_small_sample").appendText(" ").appendSibling(new TextComponentTranslation(trKey));
                        else
                            msg = new TextComponentTranslation("tfc.propick.found_large_sample").appendText(" ").appendSibling(new TextComponentTranslation(trKey));
                        player.sendStatusMessage(msg,true);
                        setCooldown(player);

                       //for (int i = 0; i < results.size(); i++)
                       //    player.sendStatusMessage(new TextComponentString(list[i].ore.getDisplayName() + ": " + String.format("%.02f", list[i].score)), false);
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

    private void scanSurroundingBlocks(World world, BlockPos center)
    {
        int rad = 15;
        double maxDistSquared = rad*rad;
        int step = 2;
        int maxScore = 9;
        results.clear();
        // Loop trough every 8th block in a sphere, find ores and give them a value 1 -> maxScore+1 based on distance.
        for (int x = -rad; x <= rad; x+=step)
        {
            for (int y = -rad; y <= rad; y+=step)
            {
                for (int z = -rad; z <= rad; z+=step)
                {
                    double distSquared = x*x + y*y + z*z;
                    if (distSquared < maxDistSquared)
                    {
                        BlockPos pos = new BlockPos(x,y,z).add(center);
                        ItemStack iStack = getOreBlock(world.getBlockState(pos));

                        if (iStack != null)
                        {
                            double score = Math.pow(((distSquared-maxDistSquared)/maxDistSquared), 2) * maxScore + 1;
                            String oreName = iStack.getDisplayName();


                            if (results.containsKey(oreName))
                                results.get(oreName).score+= score;
                            else
                                results.put(oreName, new ProspectResult(iStack, score));
                        }
                    }
                }
            }
        }
    }

    // todo: This works for BlockOreTFC but not anything else (addons for example,fix?)
    private ItemStack getOreBlock(IBlockState blockState)
    {
        if (blockState == null) return null;
        Block block = blockState.getBlock();
        if (block instanceof BlockOreTFC) //
        {
            int grade = blockState.getValue(GRADE).getMeta();
            return new ItemStack(block.getItemDropped(blockState, null,0), 1, grade);
        }
        return null;
    }

    private boolean isValidProspectBlock(IBlockState state)
    {
        return BlocksTFC.isGround(state) || (getOreBlock(state) != null);
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
