/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;
import net.dries007.tfc.objects.items.ItemQuiver;

public class ItemMetalJavelin extends ItemMetalTool
{
    public ItemMetalJavelin(Metal metal, Metal.ItemType type)
    {
        super(metal, type);

        ToolMaterial material = metal.getToolMetal();
        if (material != null)
        {
            setMaxDamage((int) (material.getMaxUses() * 0.1));
        }
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn)
    {
        if (type == Metal.ItemType.JAVELIN)
        {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof EntityPlayer && type == Metal.ItemType.JAVELIN)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            if (charge > 5)
            {
                float f = ItemBow.getArrowVelocity(charge); //Same charge time as bow

                if (!worldIn.isRemote)
                {
                    EntityThrownJavelin javelin = new EntityThrownJavelin(worldIn, player);
                    javelin.setDamage(2.5f * getAttackDamage());  // When thrown, it does approx 1.8x the tool material (attack damage is already 0.7x of the tool). This makes it slightly more damaging than axes but more difficult to use
                    javelin.setWeapon(stack);
                    javelin.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 1.5F, 0.5F);
                    worldIn.spawnEntity(javelin);
                    worldIn.playSound(null, player.posX, player.posY, player.posZ, TFCSounds.ITEM_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (Constants.RNG.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                }
                if (!((EntityPlayer) entityLiving).isCreative())
                    player.inventory.deleteStack(stack);
                player.addStat(StatList.getObjectUseStats(this));
                ItemQuiver.replenishJavelin(player.inventory); //Use a quiver if possible
            }
        }
    }
}
