package net.dries007.tfc;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IFireable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEventHandler
{
    /**
     * Make leaves drop sticks
     */
    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final EntityPlayer harvester = event.getHarvester();
        final ItemStack heldItem = harvester == null ? ItemStack.EMPTY : harvester.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (!event.isSilkTouching() && block instanceof BlockLeaves)
        {
            // Done via event so it applies to all leaves.
            double chance = ConfigTFC.GENERAL.leafStickDropChance;
            if (!heldItem.isEmpty() && Helpers.containsAnyOfCaseInsensitive(heldItem.getItem().getToolClasses(heldItem), ConfigTFC.GENERAL.leafStickDropChanceBonusClasses))
                chance = ConfigTFC.GENERAL.leafStickDropChanceBonus;
            if (event.getWorld().rand.nextFloat() < chance)
                event.getDrops().add(new ItemStack(Items.STICK));
        }
    }

    /**
     * Place pit kiln block & add items
     *      Note: `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     *      We have this event already, might as well use it.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getFace() != EnumFacing.UP) return;
        if (!event.getEntityPlayer().isSneaking()) return;
        World world = event.getWorld();
//        if (world.isRemote) return;
        BlockPos pos = event.getPos();

        IFireable fireable = IFireable.fromItem(event.getItemStack().getItem());
        if (fireable == null) return;

        if (fireable.getFireableTiers().contains(Metal.Tier.TIER_I))
        {
            //noinspection ConstantConditions
            if (world.getBlockState(pos).getBlock() != BlocksTFC.PIT_KILN)
            {
                if (!world.isSideSolid(pos, EnumFacing.UP)) return;
                pos = pos.add(0, 1, 0); // also important for TE fetch
                if (!world.getBlockState(pos).getMaterial().isReplaceable()) return; // can't put down the block
                //noinspection ConstantConditions
                world.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
            }

            TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
            if (te == null) return; // todo: log? This shouldn't happen, since the block must be a pitkiln by now.
            te.onRightClick(event.getEntityPlayer(), event.getItemStack(), (event.getHitVec().x % 1) < .5, (event.getHitVec().z % 1) < .5);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
