/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemFireStarter extends ItemTFC
{
    /**
     * Causes ignition of fire based devices, consume or damage items if valid
     *
     * @param stack the itemstack that is used to cause ignition
     * @return true if item is a valid item to ignite devices
     */
    public static boolean onIgnition(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return false;
        }
        if (OreDictionaryHelper.doesStackMatchOre(stack, "fireStarter"))
        {
            if (stack.getItem().isDamageable())
            {
                Helpers.damageItem(stack);
            }
            else
            {
                stack.shrink(1);
            }
            return true;
        }
        // Infinite items aren't damaged or consumed
        return OreDictionaryHelper.doesStackMatchOre(stack, "infiniteFire");
    }

    /**
     * Checks if item can cause ignition
     *
     * @param stack the itemstack that is used to cause ignition
     * @return true if item is a valid item to ignite devices
     */
    public static boolean canIgnite(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return false;
        }
        return OreDictionaryHelper.doesStackMatchOre(stack, "fireStarter") || OreDictionaryHelper.doesStackMatchOre(stack, "infiniteFire");
    }

    public ItemFireStarter()
    {
        setMaxDamage(8);
        setMaxStackSize(1);
        setNoRepair();
        OreDictionaryHelper.register(this, "fire", "starter");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand != EnumHand.MAIN_HAND || worldIn.isRemote)
        {
            return EnumActionResult.PASS;
        }
        if (canStartFire(worldIn, player) == null)
        {
            return EnumActionResult.FAIL;
        }
        player.setActiveHand(hand);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int countLeft)
    {
        if (!(entityLivingBase instanceof EntityPlayer)) return;
        final EntityPlayer player = (EntityPlayer) entityLivingBase;
        final RayTraceResult result = canStartFire(player.world, player);
        if (result == null)
        {
            player.resetActiveHand();
            return;
        }
        final int total = getMaxItemUseDuration(stack);
        final int count = total - countLeft;
        final BlockPos pos = result.getBlockPos().add(0, 1, 0);
        final World world = player.world;
        // Base chance
        float chance = (float) ConfigTFC.General.MISC.fireStarterChance;
        // Raining reduces chance by half
        if (world.isRainingAt(pos))
        {
            chance *= 0.5;
        }

        if (world.isRemote) // Client
        {
            if (itemRand.nextFloat() + 0.3 < count / (double) total)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, result.hitVec.x, result.hitVec.y, result.hitVec.z, 0.0F, 0.1F, 0.0F);
            }
            if (countLeft < 10 && itemRand.nextFloat() + 0.3 < count / (double) total)
            {
                world.spawnParticle(EnumParticleTypes.FLAME, result.hitVec.x, result.hitVec.y, result.hitVec.z, 0.0F, 0.0F, 0.0F);
            }

            if (count % 3 == 1)
            {
                player.playSound(TFCSounds.FIRE_STARTER, 0.5f, 0.05F);
            }
        }
        else if (countLeft == 1) // Server, and last tick of use
        {
            stack.damageItem(1, player);
            final IBlockState state = world.getBlockState(pos.down());
            if (state.getBlock() == BlocksTFC.LOG_PILE)
            {
                // Log pile
                if (itemRand.nextFloat() < chance)
                {
                    world.setBlockState(pos.down(), state.withProperty(LIT, true));
                    TELogPile te = Helpers.getTE(world, pos.down(), TELogPile.class);
                    if (te != null)
                    {
                        te.light();
                        TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                    }
                    if (Blocks.FIRE.canPlaceBlockAt(world, pos))
                    {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
            else if (state.getBlock() == BlocksTFC.PIT_KILN)
            {
                // Pit Kiln
                if (itemRand.nextFloat() < chance)
                {
                    TEPitKiln te = Helpers.getTE(world, pos.down(), TEPitKiln.class);
                    if (te != null)
                    {
                        te.tryLight();
                        TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                    }
                }
            }
            else
            {
                // Try to make a fire pit

                final List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));
                final List<EntityItem> stuffToUse = new ArrayList<>();

                int sticks = 0, kindling = 0;
                EntityItem log = null;

                for (EntityItem entity : items)
                {
                    if (OreDictionaryHelper.doesStackMatchOre(entity.getItem(), "stickWood"))
                    {
                        sticks += entity.getItem().getCount();
                        stuffToUse.add(entity);
                    }
                    else if (OreDictionaryHelper.doesStackMatchOre(entity.getItem(), "kindling"))
                    {
                        kindling += entity.getItem().getCount();
                        stuffToUse.add(entity);
                    }
                    else if (log == null && OreDictionaryHelper.doesStackMatchOre(entity.getItem(), "logWood"))
                    {
                        log = entity;
                    }
                }

                if (sticks >= 3 && log != null)
                {
                    final float kindlingModifier = Math.min(0.1f * (float) kindling, 0.5f);
                    if (itemRand.nextFloat() < chance + kindlingModifier)
                    {
                        world.setBlockState(pos, BlocksTFC.FIREPIT.getDefaultState().withProperty(LIT, true));
                        TEFirePit te = Helpers.getTE(world, pos, TEFirePit.class);
                        if (te != null)
                        {
                            te.onCreate(log.getItem());
                        }
                        stuffToUse.forEach(Entity::setDead);
                        log.getItem().shrink(1);
                        if (log.getItem().getCount() == 0)
                        {
                            log.setDead();
                        }
                        TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, world.getBlockState(pos).getBlock()); // Trigger lit block
                    }
                }
                else
                {
                    // Can't make fire pit, so start a fire
                    if (Blocks.FIRE.canPlaceBlockAt(world, pos))
                    {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL; // Stored anywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT; // Stacksize is always 1, don't need to change this
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return false;
    }

    @Nullable
    private RayTraceResult canStartFire(World world, EntityPlayer player)
    {
        RayTraceResult result = rayTrace(world, player, true);
        //noinspection ConstantConditions
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = result.getBlockPos();
            final IBlockState current = world.getBlockState(pos);
            if (result.sideHit == EnumFacing.UP && current.isSideSolid(world, pos, EnumFacing.UP) && !current.getMaterial().isLiquid())
            {
                if (world.isAirBlock(pos.up()))
                {
                    return result;
                }
            }
        }
        return null;
    }
}
