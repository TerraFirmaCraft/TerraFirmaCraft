/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.Arrays;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;

import static net.dries007.tfc.Constants.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemGoldPan extends ItemTFC
{
    private static final String[] TYPES = new String[] {"empty", "sand", "gravel", "clay", "dirt"};

    @SuppressWarnings("ConstantConditions")
    @SideOnly(Side.CLIENT)
    public static void registerModels()
    {
        for (int meta = 0; meta < TYPES.length; meta++)
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.GOLDPAN, meta, new ModelResourceLocation(MOD_ID + ":goldpan/" + TYPES[meta]));
        ModelLoader.registerItemVariants(ItemsTFC.GOLDPAN, Arrays.stream(TYPES).map(e -> new ResourceLocation(MOD_ID, "goldpan/" + e)).toArray(ResourceLocation[]::new));
    }

    public ItemGoldPan()
    {
        setMaxDamage(0);
        setMaxStackSize(1);
        setNoRepair();
        setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        //todo: move to public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (handIn != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (canPan(worldIn, playerIn) == null) return new ActionResult<>(EnumActionResult.FAIL, stack);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + "." + TYPES[stack.getItemDamage()];
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;

        for (int meta = 0; meta < TYPES.length; meta++)
            items.add(new ItemStack(this, 1, meta));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int countLeft)
    {
        if (!(entityLivingBase instanceof EntityPlayer)) return;
        final EntityPlayer player = ((EntityPlayer) entityLivingBase);
        final RayTraceResult result = canPan(player.world, player);
        if (result == null)
        {
            player.resetActiveHand();
            return;
        }
        final int total = getMaxItemUseDuration(stack);
        final int count = total - countLeft;
        final BlockPos pos = result.getBlockPos().add(0, 1, 0);
        final World world = player.world;
        final float chance = world.rand.nextFloat();

        //todo: this is a copypaste from firestarter, it needs to pan, not start fires.
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

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Nullable
    private RayTraceResult canPan(World world, EntityPlayer player)
    {
        RayTraceResult result = rayTrace(world, player, true);
        //noinspection ConstantConditions
        if (result == null) return null;
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return null;
        BlockPos pos = result.getBlockPos();
        final IBlockState current = player.world.getBlockState(pos);
        pos = pos.add(0, 1, 0);
        if (world.getBlockState(pos).getMaterial() != Material.WATER) return null;
        return result;
    }
}
