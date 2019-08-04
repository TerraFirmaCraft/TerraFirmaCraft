/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import net.dries007.tfc.api.capability.ItemStickCapability;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.EggHandler;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
import net.dries007.tfc.api.capability.skill.PlayerSkillsHandler;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.network.PacketFoodStatsReplace;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.objects.entity.animal.IAnimalTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

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
            {
                chance = ConfigTFC.GENERAL.leafStickDropChanceBonus;
            }
            if (Constants.RNG.nextFloat() < chance)
            {
                event.getDrops().add(new ItemStack(Items.STICK));
            }
        }
    }

    /**
     * Handler for {@link IPlaceableItem}
     * To add a new placeable item effect, either implement {@link IPlaceableItem} or see {@link IPlaceableItem.Impl} for vanilla item usages
     * Notes:
     * 1) `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     * 2) This event handler is fired first with the main hand as event.getStack()
     * If nothing happens (i.e. the event is not cancelled + set cancellation result to success
     * The event will fire AGAIN with the offhand and offhand stack.
     *
     * Also handles drinking water when right clicking an underwater block
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
                return;
            }
        }

        // Try to drink water
        if (!player.isCreative() && stack.isEmpty() && player.getFoodStats() instanceof IFoodStatsTFC)
        {
            IFoodStatsTFC foodStats = (IFoodStatsTFC) player.getFoodStats();
            RayTraceResult result = Helpers.rayTrace(event.getWorld(), player, true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = result.getBlockPos();
                IBlockState state = event.getWorld().getBlockState(blockpos);
                boolean isFreshWater = BlocksTFC.isFreshWater(state), isSaltWater = BlocksTFC.isSaltWater(state);
                if ((isFreshWater && foodStats.attemptDrink(10)) || (isSaltWater && foodStats.attemptDrink(-1)))
                {
                    if (!world.isRemote)
                    {
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    }
                    event.setCancellationResult(EnumActionResult.SUCCESS);
                    event.setCanceled(true);
                }
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
                if (placeable.consumeAmount() > 0)
                {
                    player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, placeable.consumeAmount()));
                }
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
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
                    world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.setBlockState(pos, BlockRockVariant.get(blockRock.getRock(), Rock.Type.FARMLAND).getDefaultState());
                }
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        float actualDamage = event.getAmount();
        // Modifier for damage type + damage resistance
        actualDamage *= DamageType.getModifier(event.getSource(), event.getEntityLiving());
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getFoodStats() instanceof IFoodStatsTFC)
            {
                float healthModifier = ((IFoodStatsTFC) player.getFoodStats()).getHealthModifier();
                if (healthModifier < ConfigTFC.GENERAL.playerMinHealthModifier)
                {
                    healthModifier = (float) ConfigTFC.GENERAL.playerMinHealthModifier;
                }
                if (healthModifier > ConfigTFC.GENERAL.playerMaxHealthModifier)
                {
                    healthModifier = (float) ConfigTFC.GENERAL.playerMaxHealthModifier;
                }
                actualDamage /= healthModifier;
            }
        }
        event.setAmount(actualDamage);
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();

        // Item Size
        // Skip items with existing capabilities
        if (!stack.isEmpty() && CapabilityItemSize.getIItemSize(stack) == null)
        {
            boolean canStack = stack.getMaxStackSize() > 1; // This is necessary so it isn't accidentally overridden by a default implementation

            // todo: Add more items here
            if (item == Items.COAL)
                CapabilityItemSize.add(event, Items.COAL, Size.SMALL, Weight.MEDIUM, canStack);
            else if (item == Items.STICK)
                event.addCapability(ItemStickCapability.KEY, new ItemStickCapability(event.getObject().getTagCompound()));
            else if (item == Items.CLAY_BALL)
                CapabilityItemSize.add(event, item, Size.SMALL, Weight.MEDIUM, canStack);

                // Final checks for general item types
            else if (item instanceof ItemTool)
                CapabilityItemSize.add(event, item, Size.LARGE, Weight.MEDIUM, canStack);
            else if (item instanceof ItemArmor)
                CapabilityItemSize.add(event, item, Size.LARGE, Weight.HEAVY, canStack);
            else if (item instanceof ItemBlock)
                CapabilityItemSize.add(event, item, Size.SMALL, Weight.MEDIUM, canStack);
            else
                CapabilityItemSize.add(event, item, Size.VERY_SMALL, Weight.LIGHT, canStack);
        }

        // todo: create a lookup or something for vanilla items
        // future plans: add via craft tweaker or json (1.14)
        if (stack.getItem() instanceof ItemFood && !stack.hasCapability(CapabilityFood.CAPABILITY, null))
        {
            event.addCapability(CapabilityFood.KEY, new FoodHandler(stack.getTagCompound(), new float[] {1, 0, 0, 0, 0}, 0, 0, 1));
        }
        if (stack.getItem() == Items.EGG && !stack.hasCapability(CapabilityEgg.CAPABILITY, null))
        {
            event.addCapability(CapabilityEgg.KEY, new EggHandler());
        }
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            // Player skills
            EntityPlayer player = (EntityPlayer) event.getObject();
            if (!player.hasCapability(CapabilityPlayerSkills.CAPABILITY, null))
            {
                event.addCapability(CapabilityPlayerSkills.KEY, new PlayerSkillsHandler());
            }
        }
    }

    /**
     * Fired on server only when a player logs in
     *
     * @param event {@link PlayerEvent.PlayerLoggedInEvent}
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // World Data (Calendar) Sync Handler
            CalendarTFC.INSTANCE.updatePlayer(player);

            // Food Stats
            FoodStats originalStats = player.getFoodStats();
            if (!(originalStats instanceof FoodStatsTFC))
            {
                player.foodStats = new FoodStatsTFC(player, originalStats);

                // Also need to read the food stats from nbt, as they were not present when the player was loaded
                MinecraftServer server = player.world.getMinecraftServer();
                if (server != null)
                {
                    NBTTagCompound nbt = server.getPlayerList().getPlayerNBT(player);
                    // This can be null if the server is unable to read the file
                    //noinspection ConstantConditions
                    if (nbt != null)
                    {
                        player.foodStats.readNBT(nbt);
                    }
                }

                TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsReplace(), (EntityPlayerMP) event.player);
            }

            // Check total players and reset calendar time ticking
            int players = event.player.world.playerEntities.size();
            CalendarTFC.INSTANCE.setArePlayersLoggedOn(event.player.world, players > 0);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Check total players and reset calendar time ticking
            int players = event.player.world.playerEntities.size();
            CalendarTFC.INSTANCE.setArePlayersLoggedOn(event.player.world, players > 0);
        }
    }

    /**
     * Fired on server only when a player dies and respawns, or travels through dimensions
     *
     * @param event {@link net.minecraftforge.event.entity.player.PlayerEvent.Clone}
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // Food Stats
            FoodStats originalStats = event.player.getFoodStats();
            if (!(originalStats instanceof FoodStatsTFC))
            {
                event.player.foodStats = new FoodStatsTFC(event.player, originalStats);
                TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsReplace(), (EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            event.getContainer().addListener(new CapabilityContainerListener(player));
        }
    }

    @SubscribeEvent
    public static void onLivingSpawnEvent(LivingSpawnEvent.CheckSpawn event)
    {
        // Check creature spawning
        if (event.getEntity() instanceof IAnimalTFC)
        {
            IAnimalTFC animal = (IAnimalTFC) event.getEntity();
            World world = event.getWorld();
            BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());

            float rainfall = ChunkDataTFC.getRainfall(world, pos);
            float temperature = ClimateTFC.getAverageBiomeTemp(world, pos);
            Biome biome = world.getBiome(pos);

            if (!animal.isValidSpawnConditions(biome, temperature, rainfall))
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onGameRuleChange(GameRuleChangeEvent event)
    {
        GameRules rules = event.getRules();
        if ("doDaylightCycle".equals(event.getRuleName()))
        {
            // This is only called on server, so it needs to sync to client
            CalendarTFC.INSTANCE.setDoDaylightCycle(event.getServer().getEntityWorld(), rules.getBoolean("doDaylightCycle"));
        }
        else if ("naturalRegeneration".equals(event.getRuleName()) && ConfigTFC.GENERAL.forceNoVanillaNaturalRegeneration)
        {
            // Natural regeneration should be disabled, allows TFC to have custom regeneration
            event.getRules().setOrCreateGameRule("naturalRegeneration", "false");
            TerraFirmaCraft.getLog().warn("Something tried to set natural regeneration to true, reverting!");
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (ConfigTFC.GENERAL.forceNoVanillaNaturalRegeneration)
        {
            // Natural regeneration should be disabled, allows TFC to have custom regeneration
            event.getWorld().getGameRules().setOrCreateGameRule("naturalRegeneration", "false");
            TerraFirmaCraft.getLog().warn("Updating gamerule naturalRegeneration to false!");
        }
    }

    /**
     * This will disable the bonus chest, cheaty cheaty players >:(
     *
     * @param event {@link net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition}
     */
    @SubscribeEvent
    public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event)
    {
        event.getSettings().bonusChestEnabled = false;
        TerraFirmaCraft.getLog().info("Disabling bonus chest, you cheaty cheater!");
    }
}
