/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.rock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;
import net.dries007.tfc.objects.items.ItemQuiver;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemRockJavelin extends ItemTool implements IItemSize, IRockObject
{
    private static final Map<RockCategory, ItemRockJavelin> MAP = new HashMap<>();

    public static ItemRockJavelin get(RockCategory category)
    {
        return MAP.get(category);
    }

    public final RockCategory category;

    public ItemRockJavelin(RockCategory category)
    {
        // Vanilla ItemTool constructor actually treats this as "bonus attack damage", and as a result, adds + getAttackDamage(). So for our purposes, this is 0.7 * attack damage.
        super(-0.3f * category.getToolMaterial().getAttackDamage(), -1.8f, category.getToolMaterial(), ImmutableSet.of());
        this.category = category;
        if (MAP.put(category, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }

        setMaxDamage((int) (category.getToolMaterial().getMaxUses() * 0.1));

        OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
        OreDictionaryHelper.register(this, "javelin");
        OreDictionaryHelper.register(this, "javelin", "stone");
        OreDictionaryHelper.register(this, "javelin", "stone", category);
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.LARGE; // Stored only in chests
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

    @Nullable
    @Override
    public Rock getRock(ItemStack stack)
    {
        return null;
    }

    @Nonnull
    @Override
    public RockCategory getRockCategory(ItemStack stack)
    {
        return category;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
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
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            if (charge > 5)
            {
                float f = ItemBow.getArrowVelocity(charge); //Same charge time as bow

                if (!worldIn.isRemote)
                {
                    EntityThrownJavelin javelin = new EntityThrownJavelin(worldIn, player);
                    javelin.setDamage(2.5f * attackDamage); // When thrown, it does approx 1.8x the tool material (attack damage is already 0.7x of the tool). This makes it slightly more damaging than axes but more difficult to use
                    javelin.setWeapon(stack);
                    javelin.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 1.5F, 0.5F);
                    worldIn.spawnEntity(javelin);
                    worldIn.playSound(null, player.posX, player.posY, player.posZ, TFCSounds.ITEM_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (Constants.RNG.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                }
                player.inventory.deleteStack(stack);
                player.addStat(StatList.getObjectUseStats(this));
                ItemQuiver.replenishJavelin(player.inventory); //Use a quiver if possible
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return false;
    }
}
