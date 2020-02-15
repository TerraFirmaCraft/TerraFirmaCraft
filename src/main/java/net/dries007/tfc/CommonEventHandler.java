/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.EggHandler;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.ForgeableHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.capability.player.PlayerDataHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.ICreatureTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.network.PacketCalendarUpdate;
import net.dries007.tfc.network.PacketFoodStatsReplace;
import net.dries007.tfc.network.PacketPlayerDataUpdate;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockQuern;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.stone.BlockStoneAnvil;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.CalendarWorldData;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.util.skills.SmithingSkill;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CommonEventHandler
{
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
                event.setNewSpeed(event.getOriginalSpeed() + (event.getOriginalSpeed() * skillModifier));
            }
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
        if (state.getBlock() instanceof BlockAnvilTFC || state.getBlock() instanceof BlockStoneAnvil || state.getBlock() instanceof BlockQuern)
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
                            foodStats.addThirst(10); //Simulation already proven that i can drink this amount
                        }
                        else
                        {
                            foodStats.addThirst(-1); //Simulation already proven that i can drink this amount
                        }
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
            if (stack.getItem() instanceof ItemFood)
            {
                ICapabilityProvider foodHandler = CapabilityFood.getCustomFood(stack);
                if (foodHandler != null)
                {
                    event.addCapability(CapabilityFood.KEY, foodHandler);
                }
                else
                {
                    foodHandler = new FoodHandler(stack.getTagCompound(), new float[] {1, 0, 0, 0, 0}, 0, 0, 1);
                    event.addCapability(CapabilityFood.KEY, foodHandler);
                }
            }

            // Forge / Heat. Try forge first, because it's more specific
            ICapabilityProvider forgeHandler = CapabilityForgeable.getCustomForgeable(stack);
            boolean isForgeable = false;
            if (forgeHandler != null)
            {
                isForgeable = true;
                event.addCapability(CapabilityForgeable.KEY, forgeHandler);
            }
            else
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
                        event.addCapability(CapabilityForgeable.KEY, new ForgeableHandler(null, metal.getSpecificHeat(), metal.getMeltTemp()));
                    }
                }
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
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

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

            // Skills
            IPlayerData skills = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (skills != null)
            {
                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(skills.serializeNBT()), player);
            }
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
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // Food Stats
            FoodStats originalStats = event.player.getFoodStats();
            if (!(originalStats instanceof FoodStatsTFC))
            {
                event.player.foodStats = new FoodStatsTFC(event.player, originalStats);
                TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsReplace(), (EntityPlayerMP) event.player);
            }

            // Skills
            IPlayerData skills = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (skills != null)
            {
                TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(skills.serializeNBT()), player);
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
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // Food Stats
            FoodStats originalStats = event.player.getFoodStats();
            if (!(originalStats instanceof FoodStatsTFC))
            {
                event.player.foodStats = new FoodStatsTFC(event.player, originalStats);
                TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsReplace(), (EntityPlayerMP) event.player);
            }

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
            event.getContainer().addListener(new CapabilityContainerListener((EntityPlayerMP) event.getEntityPlayer()));
        }
    }

    @SubscribeEvent
    public static void onLivingSpawnEvent(LivingSpawnEvent.CheckSpawn event)
    {
        // Check creature spawning - Prevents vanilla's respawning mechanic to spawn creatures outside their allowed conditions
        if (event.getEntity() instanceof ICreatureTFC)
        {
            ICreatureTFC creature = (ICreatureTFC) event.getEntity();
            World world = event.getWorld();
            BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());

            float rainfall = ChunkDataTFC.getRainfall(world, pos);
            float temperature = ClimateTFC.getAvgTemp(world, pos);
            Biome biome = world.getBiome(pos);

            // We don't roll spawning again since vanilla is handling it
            if (creature.getSpawnWeight(biome, temperature, rainfall) <= 0)
            {
                event.setResult(Event.Result.DENY);
            }
        }

        // Stop mob spawning in thatch - the list of non-spawnable light-blocking, non-collidable blocks is hardcoded in WorldEntitySpawner#canEntitySpawnBody
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (event.getWorld().getBlockState(pos).getBlock() == BlocksTFC.THATCH || event.getWorld().getBlockState(pos.up()).getBlock() == BlocksTFC.THATCH)
        {
            event.setResult(Event.Result.DENY);
        }

        // Stop mob spawning in spawn protected chunks
        if (event.getEntity().isCreatureType(EnumCreatureType.MONSTER, false))
        {
            ChunkDataTFC data = ChunkDataTFC.get(event.getWorld(), pos);
            if (ConfigTFC.GENERAL.spawnProtectionEnable && (ConfigTFC.GENERAL.spawnProtectionMinY <= event.getY()) && data.isSpawnProtected())
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        // Prevent vanilla animals (that have a TFC counterpart) from mob spawners / egg throws / other mod mechanics
        if (ConfigTFC.GENERAL.forceReplaceVanillaAnimals && Helpers.isVanillaAnimal(event.getEntity()))
        {
            Entity TFCReplacement = Helpers.getTFCReplacement(event.getEntity());
            if (TFCReplacement != null)
            {
                TFCReplacement.setPositionAndRotation(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch);
                event.getWorld().spawnEntity(TFCReplacement); // Fires another spawning event for the TFC replacement
            }
            event.setCanceled(true); // Cancel the vanilla spawn
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
        if ("naturalRegeneration".equals(event.getRuleName()) && ConfigTFC.GENERAL.forceNoVanillaNaturalRegeneration)
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

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        // Since cobble is a gravity block, placing it can lead to world crashes, so we avoid doing that and place rhyolite instead
        if (event.getNewState().getBlock() == Blocks.STONE)
        {
            event.setNewState(BlockRockVariant.get(Rock.BASALT, Rock.Type.RAW).getDefaultState().withProperty(BlockRockRaw.CAN_FALL, false));
        }
        if (event.getNewState().getBlock() == Blocks.COBBLESTONE)
        {
            event.setNewState(BlockRockVariant.get(Rock.RHYOLITE, Rock.Type.RAW).getDefaultState().withProperty(BlockRockRaw.CAN_FALL, false));
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
    }

    private static int countPlayerOverburdened(InventoryPlayer inventory)
    {
        // This is just optimized (probably uselessly, but whatever) for use in onPlayerTick
        int hugeHeavyCount = 0;
        for (ItemStack stack : inventory.mainInventory)
        {
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.HEAVY))
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
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.HEAVY))
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
            if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.HEAVY))
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
