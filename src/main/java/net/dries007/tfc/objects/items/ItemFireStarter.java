/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.BlockFirePit.LIT;
import static net.dries007.tfc.objects.blocks.wood.BlockLogPile.ONFIRE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemFireStarter extends ItemTFC
{
    private static final Predicate<EntityItem> IS_STICK = OreDictionaryHelper.createPredicateItemEntity("stickWood");
    private static final Predicate<EntityItem> IS_KINDLING = OreDictionaryHelper.createPredicateItemEntity("kindling", "paper", "hay");

    public static boolean canIgnite(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        //noinspection ConstantConditions
        return item == ItemsTFC.FIRESTARTER || item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE || item instanceof ItemFlintAndSteel;
    }

    public ItemFireStarter()
    {
        setMaxDamage(8);
        setMaxStackSize(1);
        setNoRepair();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        //todo: move to public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (handIn != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (canStartFire(worldIn, playerIn) == null) return new ActionResult<>(EnumActionResult.FAIL, stack);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
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
    @SuppressWarnings("ConstantConditions")
    public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int countLeft)
    {
        if (!(entityLivingBase instanceof EntityPlayer)) return;
        final EntityPlayer player = ((EntityPlayer) entityLivingBase);
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
        final float chance = world.rand.nextFloat(); // todo: raining etc?

        if (world.isRemote) // Client
        {
            if (chance > 0.7 && world.rand.nextFloat() < count / (double) total)
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, result.hitVec.x, result.hitVec.y, result.hitVec.z, 0.0F, 0.1F, 0.0F);
            if (chance > 0.7 && countLeft < 10 && world.rand.nextFloat() < count / (double) total)
                world.spawnParticle(EnumParticleTypes.FLAME, result.hitVec.x, result.hitVec.y, result.hitVec.z, 0.0F, 0.0F, 0.0F);
        }
        else if (countLeft == 1) // Server, and last tick of use
        {
            final IBlockState state = world.getBlockState(pos.down());
            if (state.getBlock() == BlocksTFC.LOG_PILE)
            {
                // Log pile
                if (Math.random() < chance)
                {
                    world.setBlockState(pos.down(), state.withProperty(ONFIRE, true));
                    TELogPile te = Helpers.getTE(world, pos.down(), TELogPile.class);
                    if (te != null) te.light();
                    if (!Blocks.FIRE.canPlaceBlockAt(world, pos))
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
            else if (state.getBlock() == BlocksTFC.PIT_KILN)
            {
                // Pit Kiln
                if (Math.random() < chance)
                {
                    TEPitKiln te = Helpers.getTE(world, pos.down(), TEPitKiln.class);
                    if (te != null) te.tryLight();
                }
            }
            else
            {
                // Fire pit
                stack.damageItem(1, player);

                final List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 1), i -> IS_KINDLING.test(i) || IS_STICK.test(i));

                int sticks = list.stream().filter(IS_STICK).mapToInt(e -> e.getItem().getCount()).sum();
                int kindling = list.stream().filter(IS_KINDLING).mapToInt(e -> e.getItem().getCount()).sum();

                if (sticks < 3) return;

                if (world.rand.nextFloat() < chance + Math.min(kindling * 0.1, 0.5))
                {
                    //noinspection ConstantConditions
                    world.setBlockState(pos, BlocksTFC.FIREPIT.getDefaultState().withProperty(LIT, true), 11); //todo: fire
                    list.forEach(Entity::setDead);
                }
            }
        }
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Nullable
    private RayTraceResult canStartFire(World world, EntityPlayer player)
    {
        RayTraceResult result = rayTrace(world, player, true);
        //noinspection ConstantConditions
        if (result == null) return null;
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return null;
        BlockPos pos = result.getBlockPos();
        final IBlockState current = player.world.getBlockState(pos);
        if (result.sideHit != EnumFacing.UP) return null;
        if (!current.isSideSolid(world, pos, EnumFacing.UP)) return null;
        if (current.getMaterial() == Material.WATER) return null;
        pos = pos.add(0, 1, 0);
        if (!world.isAirBlock(pos)) return null;
        return result;
    }
}
