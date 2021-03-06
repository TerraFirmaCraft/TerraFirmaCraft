/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * todo: this whole thing needs to be rewritten, possibly sometime after 1.14
 * - metadata variants need to be changed to use separate items
 * - clay / dirt variants can be re-added / enabled
 * - each gold pan operation (clay, sand, gravel, etc.) should be a json (in 1.14), possibly even using a loot table
 * - if multiple options are loaded, it should choose one randomly (weighted?)
 * - we also need to supply an implementation of a loot table / json that can access the chunk ore (current functionality)
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemGoldPan extends ItemTFC
{
    public static final String[] TYPES = new String[] {"empty", "sand", "gravel", "clay", "dirt"};

    public ItemGoldPan()
    {
        setMaxDamage(0);
        setMaxStackSize(1);
        setNoRepair();
        setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.MAIN_HAND || player.isHandActive())
        {
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
        if (player.isSneaking()) // We first check if the player is trying to dump the contents of the pan
        {
            if (stack.getItemDamage() > 0) // There is indeed contents in the pan, thus we dump it and exit
            {
                stack.setItemDamage(0);
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
        RayTraceResult result = rayTrace(world, player, true);
        if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) // If we don't find a block when raytracing, return
        {
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
        if (stack.getItemDamage() > 0 && world.getBlockState(result.getBlockPos()).getMaterial() == Material.WATER)
        {
            // When there is contents in the pan and when the raytrace finds a block with the water material, we setActiveHand in preparation for onItemUseFinish
            player.setActiveHand(hand);
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand != EnumHand.MAIN_HAND)
        {
            return EnumActionResult.PASS;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() > 0)
        {
            if (world.getBlockState(pos.offset(facing)).getMaterial() == Material.WATER)
            {
                player.setActiveHand(hand);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
        IBlockState state = world.getBlockState(pos);
        ItemStack stackAt = new ItemStack(Item.getItemFromBlock(state.getBlock()));
        if (OreDictionaryHelper.doesStackMatchOre(stackAt, "sand"))
        {
            stack.setItemDamage(1);
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stackAt, "gravel"))
        {
            stack.setItemDamage(2);
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stackAt, "blockClayDirt") || OreDictionaryHelper.doesStackMatchOre(stackAt, "blockClayGrass"))
        {
            stack.setItemDamage(3);
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stackAt, "dirt") || OreDictionaryHelper.doesStackMatchOre(stackAt, "grass"))
        {
            stack.setItemDamage(4);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (stack.getItemDamage() > 0)
            {
                RayTraceResult result = rayTrace(world, player, true);
                if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK)
                {
                    return stack;
                }
                BlockPos pos = result.getBlockPos();
                // Check if player still is looking at water
                if (world.getBlockState(pos).getMaterial() == Material.WATER)
                {
                    // Only pan for native nuggets in sand + gravel - TODO: loot tables
                    int damage = stack.getItemDamage();
                    final BlockPos position = player.getPosition();
                    if (!world.isRemote)
                    {
                        Chunk chunk = world.getChunk(position);
                        ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(chunk);
                        if (chunkDataTFC.canWork(6))
                        {
                            if (damage == 1 || damage == 2)
                            {
                                Random rand = new Random(world.getSeed() + chunk.getPos().x * 241179128412L + chunk.getPos().z * 327910215471L);
                                TFCRegistries.ORES.getValuesCollection()
                                    .stream()
                                    .filter(Ore::canPan)
                                    .filter(x -> rand.nextDouble() < x.getChunkChance())
                                    .forEach(x -> {
                                        if (Constants.RNG.nextDouble() < x.getPanChance())
                                        {
                                            Helpers.spawnItemStack(world, position, new ItemStack(ItemSmallOre.get(x)));
                                        }
                                    });
                                // player.inventory.setInventorySlotContents(player.inventory.currentItem, stack); //only way to get it to refresh! <- do we really *need* this?
                            }
                            else if (damage == 3 || damage == 4)
                            {
                                Rock rock = chunkDataTFC.getRockHeight(position);
                                if (Constants.RNG.nextDouble() < 0.35)
                                {
                                    Helpers.spawnItemStack(world, position, new ItemStack(ItemRock.get(rock), 1));
                                }
                                else if (damage == 3 && Constants.RNG.nextDouble() < 0.1)
                                {
                                    Helpers.spawnItemStack(world, position, new ItemStack(Items.BONE, 1));
                                }
                                else if (damage != 3 && Constants.RNG.nextDouble() < 0.1)
                                {
                                    Helpers.spawnItemStack(world, position, new ItemStack(Items.STICK, 1));
                                }
                            }
                            chunkDataTFC.addWork(6);
                        }
                        else
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.goldpan.chunkworked"));
                        }
                    }
                    stack.setItemDamage(0); // Set damage to an empty pan no matter what
                    if (Constants.RNG.nextFloat() < 0.01) // 1/100 chance, same as 1.7.10
                    {
                        stack.shrink(1);
                        world.playSound(null, entityLiving.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    }
                    else
                    {
                        player.getCooldownTracker().setCooldown(stack.getItem(), ConfigTFC.Devices.GOLD_PAN.cooldownTicks);
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return super.getTranslationKey(stack) + "." + TYPES[stack.getItemDamage()];
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 54;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            for (int meta = 0; meta < TYPES.length; meta++)
            {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL; // Stored in large vessels and chests
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }
}
