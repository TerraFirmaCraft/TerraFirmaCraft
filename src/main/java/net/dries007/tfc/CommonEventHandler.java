/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import net.dries007.tfc.api.capability.ItemStickCapability;
import net.dries007.tfc.api.capability.player.CapabilityPlayer;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.network.PacketCalendarUpdate;
import net.dries007.tfc.network.PacketPlayerDataUpdate;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.util.DamageManager;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CommonEventHandler
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
            if (Constants.RNG.nextFloat() < chance)
                event.getDrops().add(new ItemStack(Items.STICK));
        }
    }

    @SubscribeEvent
    public static void onRightClickWater(PlayerInteractEvent event)
    {
        if (event.getSide() == Side.CLIENT) return;
        if (!(event instanceof PlayerInteractEvent.RightClickBlock || event instanceof PlayerInteractEvent.RightClickEmpty))
            return;
        EntityPlayer player = event.getEntityPlayer();
        if (!(player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty()))
            return; //Need both hands to drink water
        RayTraceResult result = Helpers.rayTrace(event.getWorld(), player, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos blockpos = result.getBlockPos();
            IBlockState state = event.getWorld().getBlockState(blockpos);
            IPlayerData cap = event.getEntityLiving().getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
            if (cap != null)
            {
                if (BlocksTFC.isFreshWater(state))
                {
                    if (cap.drink(15))
                    {
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap), (EntityPlayerMP) player);
                    }
                }
                else if (BlocksTFC.isSaltWater(state))
                {
                    if (cap.drink(-15))
                    {
                        //Salty water drains thirst bar.
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap), (EntityPlayerMP) player);
                    }
                }
            }
        }
    }

    /**
     * Handler for {@link IPlaceableItem}
     * To add a new placeable item effect, either implement {@link IPlaceableItem} or see {@link IPlaceableItem.Impl} for vanilla item usages
     * Notes:
     * 1) `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     * 2) This event handler is fired first with the main hand as event.getStack()
     * If nothing happens (as per vanilla behavior, even if this event causes something to happen),
     * The event will fire AGAIN with the offhand and offhand stack.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        IPlaceableItem placeable = IPlaceableItem.Impl.getPlaceable(stack);
        if (placeable != null)
        {
            if (placeable.placeItemInWorld(world, pos, stack, player, event.getFace(), event.getHitVec()))
            {
                if (placeable.consumeAmount() > 0)
                {
                    player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, placeable.consumeAmount()));
                }
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    /**
     * This is an extra handler for items that also have an active effect when right clicked in the air
     * Note: If you have an item that needs an active effect, use onItemRightClick(), or attach this via {@link IPlaceableItem.Impl}
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        IPlaceableItem placeable = IPlaceableItem.Impl.getUsable(stack);
        if (placeable != null)
        {
            if (placeable.placeItemInWorld(world, pos, stack, player, event.getFace(), null))
            {
                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
            }
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseHoe(UseHoeEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockRockVariant)
        {
            BlockRockVariant blockRock = (BlockRockVariant) state.getBlock();
            if (blockRock.getType() == Rock.Type.GRASS || blockRock.getType() == Rock.Type.DIRT)
            {
                if (!world.isRemote)
                {
                    world.playSound(event.getEntityPlayer(), pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.setBlockState(pos, BlockRockVariant.get(blockRock.getRock(), Rock.Type.FARMLAND).getDefaultState());
                }
                event.setResult(Event.Result.ALLOW);
            }
        }

    }


    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        //Starvation should not be scaled, you receive damage based on % anyway.
        if (event.getEntityLiving() instanceof EntityPlayer && !(event.getSource() == DamageSource.STARVE))
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IPlayerData cap = event.getEntityLiving().getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
            if (cap != null)
            {
                float damage = DamageManager.rescaleDamage(event.getAmount(), 20, ConfigTFC.GENERAL.playerBaseHealth);
                damage = DamageManager.applyArmor(damage, event.getSource(), player);
                //So you came here and ask, why cap.getMaxHealth() and not the config? because this way
                //we still use 20 as max health in vanilla, but our final damage is scaled to playerBaseHealth
                //Lets take an example: Player takes 20 damage(in vanilla). This is scaled to 1000(default)
                //armor is applied(for this example, let's assume player has no armor)
                //then 1000 damage is 1/3rd of players's max health(3000), so, the rescale function
                //will return 6.6667(1/3rd of 20) and apply that damage to the player. Voila :D
                float finalDamage = DamageManager.rescaleDamage(damage, cap.getMaxHealth(), 20);
                event.setAmount(finalDamage);
                event.getSource().setDamageBypassesArmor(); //Armor calculation is already done
            }
        }
    }


    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> e)
    {
        ItemStack stack = e.getObject();
        Item item = stack.getItem();

        // Item Size
        // Skip items with existing capabilities
        if (!stack.isEmpty() && CapabilityItemSize.getIItemSize(stack) == null)
        {

            boolean canStack = stack.getMaxStackSize() > 1; // This is necessary so it isn't accidentally overridden by a default implementation

            // todo: Add more items here
            if (item == Items.COAL)
                CapabilityItemSize.add(e, Items.COAL, Size.SMALL, Weight.MEDIUM, canStack);
            else if (item == Items.STICK)
                e.addCapability(ItemStickCapability.KEY, new ItemStickCapability(e.getObject().getTagCompound()));
            else if (item == Items.CLAY_BALL)
                CapabilityItemSize.add(e, item, Size.SMALL, Weight.MEDIUM, canStack);

                // Final checks for general item types
            else if (item instanceof ItemTool)
                CapabilityItemSize.add(e, item, Size.LARGE, Weight.MEDIUM, canStack);
            else if (item instanceof ItemArmor)
                CapabilityItemSize.add(e, item, Size.LARGE, Weight.HEAVY, canStack);
            else if (item instanceof ItemBlock)
                CapabilityItemSize.add(e, item, Size.SMALL, Weight.MEDIUM, canStack);
            else
                CapabilityItemSize.add(e, item, Size.VERY_SMALL, Weight.LIGHT, canStack);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // World Data (Calendar) Sync Handler
            TerraFirmaCraft.getNetwork().sendTo(new PacketCalendarUpdate(), player);

            // Player data
            IPlayerData cap = player.getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
            if (cap != null)
            {
                cap.onUpdate(player);
                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap), player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
        // Capability Sync Handler
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            final EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        // Capability Sync Handler
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            final EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            event.getContainer().addListener(new CapabilityContainerListener(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        World world = event.player.world;

        if (!world.isRemote && event.phase == TickEvent.Phase.START)
        {
            EntityPlayer player = event.player;
            IPlayerData cap = player.getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
            if (cap != null)
            {
                cap.onUpdate(player);
                if (player.ticksExisted % 100 == 0)
                {
                    //Apply Debuff and send update to client
                    if (cap.getThirst() < 10f)
                    {
                        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 1));
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 160, 1));
                        if (cap.getThirst() <= 0f)
                        {
                            //Hurt the player
                            player.attackEntityFrom(DamageSource.STARVE, 1); //5% life/5secs
                        }
                    }
                    else if (cap.getThirst() < 40f)
                    {
                        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 0));
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 160, 0));
                    }
                    TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap), (EntityPlayerMP) player);
                }
            }
        }
    }
}
