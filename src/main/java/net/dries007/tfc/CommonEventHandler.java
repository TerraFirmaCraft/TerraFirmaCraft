/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.EggHandler;
import net.dries007.tfc.api.capability.food.*;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.ForgeableHeatableHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.capability.player.PlayerDataHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.capability.worldtracker.CapabilityWorldTracker;
import net.dries007.tfc.api.capability.worldtracker.WorldTracker;
import net.dries007.tfc.api.types.*;
import net.dries007.tfc.compat.patchouli.TFCPatchouliPlugin;
import net.dries007.tfc.network.PacketCalendarUpdate;
import net.dries007.tfc.network.PacketPlayerDataUpdate;
import net.dries007.tfc.objects.blocks.BlockFluidTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockQuern;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.stone.BlockStoneAnvil;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSupport;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemQuiver;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.DamageSourcesTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MonsterEquipment;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.CalendarWorldData;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.util.skills.SmithingSkill;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CommonEventHandler
{
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Fill thirst after drinking vanilla water bottles or milk
     */
    @SubscribeEvent
    public static void onEntityUseItem(LivingEntityUseItemEvent.Finish event)
    {
        ItemStack usedItem = event.getItem();
        if (usedItem.getItem() == Items.MILK_BUCKET || PotionUtils.getPotionFromItem(usedItem) == PotionTypes.WATER)
        {
            if (event.getEntityLiving() instanceof EntityPlayerMP)
            {
                EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
                if (player.getFoodStats() instanceof FoodStatsTFC)
                {
                    ((FoodStatsTFC) player.getFoodStats()).addThirst(40); //Same as jug
                }
            }
        }
    }

    /**
     * Update harvesting tool before it takes damage
     */
    @SubscribeEvent
    public static void breakEvent(BlockEvent.BreakEvent event)
    {
        final EntityPlayer player = event.getPlayer();
        final ItemStack heldItem = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (player != null)
        {
            IPlayerData cap = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (cap != null)
            {
                cap.setHarvestingTool(player.getHeldItemMainhand());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final EntityPlayer player = event.getHarvester();
        final ItemStack heldItem = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        // Make leaves drop sticks
        if (!event.isSilkTouching() && block instanceof BlockLeaves)
        {
            // Done via event so it applies to all leaves.
            double chance = ConfigTFC.General.TREE.leafStickDropChance;
            if (!heldItem.isEmpty() && Helpers.containsAnyOfCaseInsensitive(heldItem.getItem().getToolClasses(heldItem), ConfigTFC.General.TREE.leafStickDropChanceBonusClasses))
            {
                chance = ConfigTFC.General.TREE.leafStickDropChanceBonus;
            }
            if (Constants.RNG.nextFloat() < chance)
            {
                event.getDrops().add(new ItemStack(Items.STICK));
            }
        }
        // Harvest ice from saws
        if (OreDictionaryHelper.doesStackMatchOre(heldItem, "saw") && block == Blocks.ICE)
        {
            event.getDrops().add(new ItemStack(Blocks.ICE));
        }
        // Drop shards from glass
        ItemStack stackAt = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, state.getBlock().damageDropped(state));
        if (OreDictionaryHelper.doesStackMatchOre(stackAt,"blockGlass"))
        {
            event.getDrops().add(new ItemStack(ItemsTFC.GLASS_SHARD));
        }

        // Apply durability modifier on tools
        if (player != null)
        {
            ItemStack tool = ItemStack.EMPTY;
            IPlayerData cap = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (cap != null)
            {
                tool = cap.getHarvestingTool();
            }
            if (!tool.isEmpty())
            {
                float skillModifier = SmithingSkill.getSkillBonus(tool, SmithingSkill.Type.TOOLS) / 2.0F;
                if (skillModifier > 0 && Constants.RNG.nextFloat() < skillModifier)
                {
                    // Up to 50% negating damage, for double durability
                    player.setHeldItem(EnumHand.MAIN_HAND, tool);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakProgressEvent(BreakSpeed event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player != null)
        {
            ItemStack stack = player.getHeldItemMainhand();
            float skillModifier = SmithingSkill.getSkillBonus(stack, SmithingSkill.Type.TOOLS);
            if (skillModifier > 0)
            {
                // Up to 2x modifier for break speed for skill bonuses on tools
                // New speed, so it will take into account other mods' modifications
                event.setNewSpeed(event.getNewSpeed() + (event.getNewSpeed() * skillModifier));
            }
        }
        if (event.getState().getBlock() instanceof BlockRockVariant)
        {
            event.setNewSpeed((float) (event.getNewSpeed() / ConfigTFC.General.MISC.rockMiningTimeModifier));
        }
        if (event.getState().getBlock() instanceof BlockLogTFC)
        {
            event.setNewSpeed((float) (event.getNewSpeed() / ConfigTFC.General.MISC.logMiningTimeModifier));
        }
    }

    /**
     * Handles drinking water when right clicking an underwater block
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final IBlockState state = world.getBlockState(pos);
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        // Fire onBlockActivated for in world crafting devices
        if (state.getBlock() instanceof BlockAnvilTFC
            || state.getBlock() instanceof BlockStoneAnvil
            || state.getBlock() instanceof BlockQuern
            || state.getBlock() instanceof BlockSupport)
        {
            event.setUseBlock(Event.Result.ALLOW);
        }

        // Try to drink water
        // Only possible with main hand - fixes attempting to drink even when it doesn't make sense
        if (!player.isCreative() && stack.isEmpty() && player.getFoodStats() instanceof IFoodStatsTFC && event.getHand() == EnumHand.MAIN_HAND)
        {
            IFoodStatsTFC foodStats = (IFoodStatsTFC) player.getFoodStats();
            RayTraceResult result = Helpers.rayTrace(event.getWorld(), player, true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                IBlockState waterState = world.getBlockState(result.getBlockPos());
                boolean isFreshWater = BlocksTFC.isFreshWater(waterState), isSaltWater = BlocksTFC.isSaltWater(waterState);
                if ((isFreshWater && foodStats.attemptDrink(10, true)) || (isSaltWater && foodStats.attemptDrink(-1, true)))
                {
                    //Simulated so client will check if he would drink before updating stats
                    if (!world.isRemote)
                    {
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        if (isFreshWater)
                        {
                            foodStats.attemptDrink(10, false);
                        }
                        else
                        {
                            foodStats.attemptDrink(-1, false);
                        }
                    }
                    else
                    {
                        foodStats.resetCooldown();
                    }
                    event.setCancellationResult(EnumActionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUseHoe(UseHoeEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);

        if (ConfigTFC.General.OVERRIDES.enableHoeing)
        {
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
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        float actualDamage = event.getAmount();
        // Add damage bonus for weapons
        Entity entity = event.getSource().getTrueSource();
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase damager = (EntityLivingBase) entity;
            ItemStack stack = damager.getHeldItemMainhand();
            float skillModifier = SmithingSkill.getSkillBonus(stack, SmithingSkill.Type.WEAPONS);
            if (skillModifier > 0)
            {
                // Up to 1.5x damage
                actualDamage *= 1 + (skillModifier / 2.0F);
            }
        }
        // Modifier for damage type + damage resistance
        actualDamage *= DamageType.getModifier(event.getSource(), event.getEntityLiving());
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getFoodStats() instanceof IFoodStatsTFC)
            {
                float healthModifier = ((IFoodStatsTFC) player.getFoodStats()).getHealthModifier();
                if (healthModifier < ConfigTFC.General.PLAYER.minHealthModifier)
                {
                    healthModifier = (float) ConfigTFC.General.PLAYER.minHealthModifier;
                }
                if (healthModifier > ConfigTFC.General.PLAYER.maxHealthModifier)
                {
                    healthModifier = (float) ConfigTFC.General.PLAYER.maxHealthModifier;
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
        if (!stack.isEmpty())
        {
            // Size
            if (CapabilityItemSize.getIItemSize(stack) == null)
            {
                ICapabilityProvider sizeHandler = CapabilityItemSize.getCustomSize(stack);
                event.addCapability(CapabilityItemSize.KEY, sizeHandler);
                if (sizeHandler instanceof IItemSize)
                {
                    // Only modify the stack size if the item was stackable in the first place
                    // Note: this is called in many cases BEFORE all custom capabilities are added.
                    int prevStackSize = stack.getMaxStackSize();
                    if (prevStackSize != 1)
                    {
                        item.setMaxStackSize(((IItemSize) sizeHandler).getStackSize(stack));
                    }
                }
            }

            // Food
            // Because our foods supply a custom capability in Item#initCapabilities, we need to avoid attaching a duplicate, otherwise it breaks food stacking recipes.
            // This problem goes away in 1.15 as all of these definitions (including ours) become tags)
            // We allow custom defined capabilities to attach to non-food items, that should have rot (such as eggs).
            ICapabilityProvider foodHandler = CapabilityFood.getCustomFood(stack);
            if (foodHandler != null || stack.getItem() instanceof ItemFood)
            {
                if (stack.getItem() instanceof IItemFoodTFC)
                {
                    foodHandler = ((IItemFoodTFC) stack.getItem()).getCustomFoodHandler();
                }
                if (foodHandler == null)
                {
                    foodHandler = new FoodHandler(stack.getTagCompound(), new FoodData());
                }
                event.addCapability(CapabilityFood.KEY, foodHandler);
            }

            // Forge / Metal / Heat. Try forge first, because it's more specific
            ICapabilityProvider forgeHandler = CapabilityForgeable.getCustomForgeable(stack);
            boolean isForgeable = false;
            boolean isHeatable = false;
            if (forgeHandler != null)
            {
                isForgeable = true;
                event.addCapability(CapabilityForgeable.KEY, forgeHandler);
                isHeatable = forgeHandler instanceof IItemHeat;
            }
            // Metal
            ICapabilityProvider metalCapability = CapabilityMetalItem.getCustomMetalItem(stack);
            if (metalCapability != null)
            {
                event.addCapability(CapabilityMetalItem.KEY, metalCapability);
                if (!isForgeable)
                {
                    // Add a forgeable capability for this item, if none is found
                    IMetalItem cap = (IMetalItem) metalCapability;
                    Metal metal = cap.getMetal(stack);
                    if (metal != null)
                    {
                        event.addCapability(CapabilityForgeable.KEY, new ForgeableHeatableHandler(null, metal.getSpecificHeat(), metal.getMeltTemp()));
                        isHeatable = true;
                    }
                }
            }
            // If one of the above is also heatable, skip this
            if (!isHeatable)
            {
                ICapabilityProvider heatHandler = CapabilityItemHeat.getCustomHeat(stack);
                if (heatHandler != null)
                {
                    event.addCapability(CapabilityItemHeat.KEY, heatHandler);
                }
            }

            // Armor
            if (item instanceof ItemArmor)
            {
                ICapabilityProvider damageResistance = CapabilityDamageResistance.getCustomDamageResistance(stack);
                if (damageResistance != null)
                {
                    event.addCapability(CapabilityDamageResistance.KEY, damageResistance);
                }
            }

            // Eggs
            if (stack.getItem() == Items.EGG)
            {
                event.addCapability(CapabilityEgg.KEY, new EggHandler());
            }
        }
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            // Player skills
            EntityPlayer player = (EntityPlayer) event.getObject();
            if (!player.hasCapability(CapabilityPlayerData.CAPABILITY, null))
            {
                event.addCapability(CapabilityPlayerData.KEY, new PlayerDataHandler(player));
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
            CapabilityContainerListener.addTo(player.inventoryContainer, player);

            // Food Stats
            FoodStatsTFC.replaceFoodStats(player);
            if (player.getFoodStats() instanceof IFoodStatsTFC)
            {
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
            }

            // layer Data
            IPlayerData playerData = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (playerData != null)
            {
                // Give book if possible
                if (Loader.isModLoaded("patchouli") && !playerData.hasBook() && ConfigTFC.General.MISC.giveBook)
                {
                    TFCPatchouliPlugin.giveBookToPlayer(player);
                    playerData.setHasBook(true);
                }

                // Sync
                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(playerData.serializeNBT()), player);
            }
        }
    }

    /**
     * Fired on server only when a player logs out
     *
     * @param event {@link PlayerEvent.PlayerLoggedOutEvent}
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability sync handler, we can remove it now
            CapabilityContainerListener.removeFrom((EntityPlayerMP) event.player);
        }
    }

    /**
     * Fired on server only when a player dies and respawns, or travels through dimensions
     *
     * @param event {@link PlayerEvent.PlayerRespawnEvent event}
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            CapabilityContainerListener.addTo(player.inventoryContainer, player);

            // Food Stats
            FoodStatsTFC.replaceFoodStats(player);

            // Skills / Player data
            IPlayerData cap = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (cap != null)
            {
                // Give book if possible
                if (Loader.isModLoaded("patchouli") && !(event.isEndConquered() || player.world.getGameRules().getBoolean("keepInventory")) && ConfigTFC.General.MISC.giveBook)
                {
                    TFCPatchouliPlugin.giveBookToPlayer(player);
                    cap.setHasBook(true);
                }

                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap.serializeNBT()), player);
            }
        }
    }

    /**
     * Fired on server only when a player dies and respawns.
     * Used to copy skill level before respawning since we need the original (AKA the body) player entity
     *
     * @param event {@link net.minecraftforge.event.entity.player.PlayerEvent.Clone}
     */
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

            // Skills
            IPlayerData newSkills = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            IPlayerData originalSkills = event.getOriginal().getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (newSkills != null && originalSkills != null)
            {
                newSkills.deserializeNBT(originalSkills.serializeNBT());
                // To properly sync, we need to use PlayerRespawnEvent
            }
        }
    }

    /*
     * Fired on server, sync capabilities to client whenever player changes dimension.
     */
    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            CapabilityContainerListener.addTo(player.inventoryContainer, player);

            // Food Stats
            FoodStatsTFC.replaceFoodStats(player);

            // Skills
            IPlayerData skills = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (skills != null)
            {
                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(skills.serializeNBT()), player);
            }
        }
    }

    /**
     * Only fired on server
     */
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            CapabilityContainerListener.addTo(event.getContainer(), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void onLivingSpawnEvent(LivingSpawnEvent.CheckSpawn event)
    {
        World world = event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (world.getWorldType() == TerraFirmaCraft.getWorldType() && event.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD)
        {
            if (ConfigTFC.General.SPAWN_PROTECTION.preventMobs && event.getEntity().isCreatureType(EnumCreatureType.MONSTER, false))
            {
                // Prevent Mobs
                ChunkDataTFC data = ChunkDataTFC.get(event.getWorld(), pos);
                int minY = ConfigTFC.General.SPAWN_PROTECTION.minYMobs;
                int maxY = ConfigTFC.General.SPAWN_PROTECTION.maxYMobs;
                if (data.isSpawnProtected() && minY <= maxY && event.getY() >= minY && event.getY() <= maxY)
                {
                    event.setResult(Event.Result.DENY);
                }
            }

            if (ConfigTFC.General.SPAWN_PROTECTION.preventPredators && event.getEntity() instanceof IPredator)
            {
                // Prevent Predators
                ChunkDataTFC data = ChunkDataTFC.get(event.getWorld(), pos);
                int minY = ConfigTFC.General.SPAWN_PROTECTION.minYPredators;
                int maxY = ConfigTFC.General.SPAWN_PROTECTION.maxYPredators;
                if (data.isSpawnProtected() && minY <= maxY && event.getY() >= minY && event.getY() <= maxY)
                {
                    event.setResult(Event.Result.DENY);
                }
            }

            if (event.getEntity() instanceof EntitySquid && world.getBlockState(pos).getBlock() instanceof BlockFluidTFC)
            {
                // Prevents squids spawning outside of salt water (eg: oceans)
                Fluid fluid = ((BlockFluidTFC) world.getBlockState(pos).getBlock()).getFluid();
                if (FluidsTFC.SALT_WATER.get() != fluid)
                {
                    event.setResult(Event.Result.DENY);
                }
            }

            // Check creature spawning - Prevents vanilla's respawning mechanic to spawn creatures outside their allowed conditions
            if (event.getEntity() instanceof ICreatureTFC)
            {
                ICreatureTFC creature = (ICreatureTFC) event.getEntity();
                float rainfall = ChunkDataTFC.getRainfall(world, pos);
                float temperature = ClimateTFC.getAvgTemp(world, pos);
                float floraDensity = ChunkDataTFC.getFloraDensity(world, pos);
                float floraDiversity = ChunkDataTFC.getFloraDiversity(world, pos);
                Biome biome = world.getBiome(pos);

                // We don't roll spawning again since vanilla is handling it
                if (creature.getSpawnWeight(biome, temperature, rainfall, floraDensity, floraDiversity) <= 0)
                {
                    event.setResult(Event.Result.DENY);
                }
            }

            // Stop mob spawning on surface
            if (ConfigTFC.General.DIFFICULTY.preventMobsOnSurface)
            {
                if (Helpers.shouldPreventOnSurface(event.getEntity()))
                {
                    int maximumY = (WorldTypeTFC.SEALEVEL - WorldTypeTFC.ROCKLAYER2) / 2 + WorldTypeTFC.ROCKLAYER2; // Half through rock layer 1
                    if (pos.getY() >= maximumY || world.canSeeSky(pos))
                    {
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }

        // Stop mob spawning in thatch - the list of non-spawnable light-blocking, non-collidable blocks is hardcoded in WorldEntitySpawner#canEntitySpawnBody
        // This is intentionally outside the previous world type check as this is a fix for the thatch block, not a generic spawning check.
        if (event.getWorld().getBlockState(pos).getBlock() == BlocksTFC.THATCH || event.getWorld().getBlockState(pos.up()).getBlock() == BlocksTFC.THATCH)
        {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (event.getWorld().getWorldType() == TerraFirmaCraft.getWorldType() && event.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD)
        {
            // Fix chickens spawning in caves (which is caused by zombie jockeys)
            if (entity instanceof EntityChicken && ((EntityChicken) entity).isChickenJockey())
            {
                event.setCanceled(true); // NO!
            }

            // Prevent vanilla animals (that have a TFC counterpart) from mob spawners / egg throws / other mod mechanics
            if (ConfigTFC.General.OVERRIDES.forceReplaceVanillaAnimals && Helpers.isVanillaAnimal(entity))
            {
                Entity TFCReplacement = Helpers.getTFCReplacement(entity);
                if (TFCReplacement != null)
                {
                    TFCReplacement.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                    event.getWorld().spawnEntity(TFCReplacement); // Fires another spawning event for the TFC replacement
                }
                event.setCanceled(true); // Cancel the vanilla spawn
            }
            if (ConfigTFC.General.DIFFICULTY.giveVanillaMobsEquipment)
            {
                // Set equipment to some mobs
                MonsterEquipment equipment = MonsterEquipment.get(entity);
                if (equipment != null)
                {
                    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
                    {
                        equipment.getEquipment(slot, Constants.RNG).ifPresent(stack -> entity.setItemStackToSlot(slot, stack));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpactEvent(ProjectileImpactEvent.Throwable event)
    {
        if (event.getThrowable() instanceof EntityEgg)
        {
            // Only way of preventing EntityEgg from spawning a chicken is to cancel the impact altogether
            // Side effect: The impact will not hurt entities
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGameRuleChange(GameRuleChangeEvent event)
    {
        GameRules rules = event.getRules();
        if ("naturalRegeneration".equals(event.getRuleName()) && ConfigTFC.General.OVERRIDES.forceNoVanillaNaturalRegeneration)
        {
            // Natural regeneration should be disabled, allows TFC to have custom regeneration
            event.getRules().setOrCreateGameRule("naturalRegeneration", "false");
            TerraFirmaCraft.getLog().warn("Something tried to set natural regeneration to true, reverting!");
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        final World world = event.getWorld();

        if (world.provider.getDimension() == 0 && !world.isRemote)
        {
            // Calendar Sync / Initialization
            CalendarWorldData data = CalendarWorldData.get(world);
            CalendarTFC.INSTANCE.resetTo(data.getCalendar());
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(CalendarTFC.INSTANCE));
        }

        if (ConfigTFC.General.OVERRIDES.forceNoVanillaNaturalRegeneration)
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

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        // Since cobble is a gravity block, placing it can lead to world crashes, so we avoid doing that and place rhyolite instead
        if (ConfigTFC.General.OVERRIDES.enableLavaWaterPlacesTFCBlocks)
        {
            if (event.getNewState().getBlock() == Blocks.STONE)
            {
                event.setNewState(BlockRockVariant.get(Rock.BASALT, Rock.Type.RAW).getDefaultState().withProperty(BlockRockRaw.CAN_FALL, false));
            }
            if (event.getNewState().getBlock() == Blocks.COBBLESTONE)
            {
                event.setNewState(BlockRockVariant.get(Rock.RHYOLITE, Rock.Type.RAW).getDefaultState().withProperty(BlockRockRaw.CAN_FALL, false));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.player.ticksExisted % 100 == 0)
        {
            // Add spawn protection to surrounding chunks
            BlockPos basePos = new BlockPos(event.player);
            for (int i = -2; i <= 2; i++)
            {
                for (int j = -2; j <= 2; j++)
                {
                    BlockPos chunkPos = basePos.add(16 * i, 0, 16 * j);
                    ChunkDataTFC data = ChunkDataTFC.get(event.player.getEntityWorld(), chunkPos);
                    data.addSpawnProtection(1);
                }
            }
        }

        if (event.phase == TickEvent.Phase.START && !event.player.isCreative() && event.player.ticksExisted % 20 == 0)
        {
            // Update overburdened state
            int hugeHeavyCount = countPlayerOverburdened(event.player.inventory);
            if (hugeHeavyCount >= 1)
            {
                // Add extra exhaustion from carrying a heavy item
                // This is equivalent to an additional 25% of passive exhaustion
                event.player.addExhaustion(FoodStatsTFC.PASSIVE_EXHAUSTION * 20 * 0.25f / 0.4f);
            }
            if (hugeHeavyCount >= 2)
            {
                // Player is barely able to move
                event.player.addPotionEffect(new PotionEffect(PotionEffectsTFC.OVERBURDENED, 25, 125, false, false));
            }
        }

        if (event.phase == TickEvent.Phase.START && event.player.openContainer != null && event.player instanceof EntityPlayerMP)
        {
            // Sync capability only changes in the player's container
            // We do this for containers (such as the player's inventory), which don't sync capability changes through detectAndSendChanges
            CapabilityContainerListener.syncCapabilityOnlyChanges(event.player.openContainer, (EntityPlayerMP) event.player);
        }
    }

    //go last, so if other mods handle this event, we don't.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void checkArrowFill(ArrowNockEvent event)
    {
        //if we didn't have ammo in main inventory and no other mod has handled the event
        if (!event.hasAmmo() && event.getAction() == null)
        {
            final EntityPlayer player = event.getEntityPlayer();
            if (player != null && !player.capabilities.isCreativeMode)
            {
                if (ItemQuiver.replenishArrow(player))
                {
                    event.setAction(new ActionResult<>(EnumActionResult.PASS, event.getBow()));
                }
            }
        }
    }

    //go last to avoid cancelled events
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void pickupQuiverItems(EntityItemPickupEvent event) //only pickups of EntityItem, not EntityArrow
    {
        if (!event.isCanceled())
        {
            if (ItemQuiver.pickupAmmo(event))
            {
                event.setResult(Event.Result.ALLOW);
                event.getItem().getItem().setCount(0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        ResourceLocation entityType = EntityList.getKey(event.getTarget());
        Entity target = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();

        if (entityType != null && target.hurtResistantTime == 0 && !target.getEntityWorld().isRemote && player.getHeldItemMainhand().isEmpty()
            && player.isSneaking() && Arrays.asList(ConfigTFC.General.MISC.pluckableEntities).contains(entityType.toString()))
        {
            target.dropItem(Items.FEATHER, 1);
            target.attackEntityFrom(DamageSourcesTFC.PLUCKING, (float) ConfigTFC.General.MISC.damagePerFeather);
            if (target instanceof IAnimalTFC)
            {
                ((IAnimalTFC) target).setFamiliarity(((EntityAnimalTFC) target).getFamiliarity() - 0.04f);
            }
        }
    }

    @SubscribeEvent
    public static void attachWorldCapabilities(AttachCapabilitiesEvent<World> event)
    {
        event.addCapability(CapabilityWorldTracker.KEY, new WorldTracker());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            WorldTracker tracker = event.world.getCapability(CapabilityWorldTracker.CAPABILITY, null);
            if (tracker != null)
            {
                tracker.tick(event.world);
            }
        }
    }

    @SubscribeEvent
    public static void onServerChatEvent(ServerChatEvent event)
    {
        IPlayerData cap = event.getPlayer().getCapability(CapabilityPlayerData.CAPABILITY, null);
        if (cap != null)
        {
            long intoxicatedTicks = cap.getIntoxicatedTime() - 6 * ICalendar.TICKS_IN_HOUR; // Only apply intoxication after 6 hr
            if (intoxicatedTicks > 0)
            {
                float drunkChance = MathHelper.clamp((float) intoxicatedTicks / PlayerDataHandler.MAX_INTOXICATED_TICKS, 0, 0.7f);
                String originalMessage = event.getMessage();
                String[] words = originalMessage.split(" ");
                for (int i = 0; i < words.length; i++)
                {
                    String word = words[i];
                    if (word.length() == 0)
                    {
                        continue;
                    }

                    // Swap two letters
                    if (Constants.RNG.nextFloat() < drunkChance && word.length() >= 2)
                    {
                        int pos = Constants.RNG.nextInt(word.length() - 1);
                        word = word.substring(0, pos) + word.charAt(pos + 1) + word.charAt(pos) + word.substring(pos + 2);
                    }

                    // Repeat / slur letters
                    if (Constants.RNG.nextFloat() < drunkChance)
                    {
                        int pos = Constants.RNG.nextInt(word.length());
                        char repeat = word.charAt(pos);
                        int amount = 1 + Constants.RNG.nextInt(3);
                        word = word.substring(0, pos) + new String(new char[amount]).replace('\0', repeat) + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                    }

                    // Add additional letters
                    if (Constants.RNG.nextFloat() < drunkChance)
                    {
                        int pos = Constants.RNG.nextInt(word.length());
                        char replacement = ALPHABET.charAt(Constants.RNG.nextInt(ALPHABET.length()));
                        if (Character.isUpperCase(word.charAt(Constants.RNG.nextInt(word.length()))))
                        {
                            replacement = Character.toUpperCase(replacement);
                        }
                        word = word.substring(0, pos) + replacement + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                    }

                    words[i] = word;
                }
                event.setComponent(new TextComponentTranslation("<" + event.getUsername() + "> " + String.join(" ", words)));
            }
        }
    }

    private static int countPlayerOverburdened(InventoryPlayer inventory)
    {
        // This is just optimized (probably uselessly, but whatever) for use in onPlayerTick
        int hugeHeavyCount = 0;
        for (ItemStack stack : inventory.mainInventory)
        {
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.VERY_HEAVY))
            {
                hugeHeavyCount++;
                if (hugeHeavyCount >= 2)
                {
                    return hugeHeavyCount;
                }
            }
        }
        for (ItemStack stack : inventory.armorInventory)
        {
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.VERY_HEAVY))
            {
                hugeHeavyCount++;
                if (hugeHeavyCount >= 2)
                {
                    return hugeHeavyCount;
                }
            }
        }
        for (ItemStack stack : inventory.offHandInventory)
        {
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.VERY_HEAVY))
            {
                hugeHeavyCount++;
                if (hugeHeavyCount >= 2)
                {
                    return hugeHeavyCount;
                }
            }
        }
        return hugeHeavyCount;
    }
}
