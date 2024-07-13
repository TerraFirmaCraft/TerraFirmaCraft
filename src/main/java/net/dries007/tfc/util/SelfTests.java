/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.common.TFCCreativeTabs;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCLightBlock;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.ScrapingBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.BranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.GrowingBranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.blocks.rock.RockDisplayCategory;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.calendar.Day;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.util.data.Metal;
import net.dries007.tfc.world.chunkdata.ForestType;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Central location for all self tests
 * These are tests that are performed at runtime for various static analysis purposes.
 * None of these are executed unless {@link Helpers#ASSERTIONS_ENABLED} is {@code true}.
 */
public final class SelfTests
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean THROW_ON_SELF_TEST_FAIL = true;

    private static boolean EXTERNAL_ERROR = false;

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static void runWorldVersionTest()
    {
        assert SharedConstants.WORLD_VERSION == 3465 : "If this fails, you need to update the world version here, AND in resources/generate_trees.py, then run `python resources trees`. This updates them and avoids triggering DFU when placed!";
    }

    public static void runClientSelfTests()
    {
        NeoForge.EVENT_BUS.post(new ClientSelfTestEvent()); // For other mods, as this is invoked via a tricky mixin
        if (Helpers.TEST_ENVIRONMENT)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockEntities(),
                validateModels(),
                validateTranslationsAndCreativeTabs()
            );
            LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    public static void runServerSelfTests()
    {
        if (Helpers.TEST_ENVIRONMENT)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockLootTables(),
                validateOwnBlockMineableTags(),
                validateOwnBlockTags(),
                EXTERNAL_ERROR
            );
            LOGGER.info("Server self tests passed in {}", tick.stop());
        }
    }

    public static void runDataPackTests(RecipeManager manager)
    {
        final Stopwatch tick = Stopwatch.createStarted();
        throwIfAny(
            validateReplaceableBlocksAreTagged(),
            validateFoodsAreFoods(),
            validateJugDrinkable(),
            validateCollapseRecipeTags(manager),
            validateLandslideRecipeTags(manager),
            validateMetalTagsAreCorrect(m -> m.parts().ingots(), TFCTags.Items.PILEABLE_INGOTS),
            validateMetalTagsAreCorrect(m -> m.parts().doubleIngots(), TFCTags.Items.PILEABLE_DOUBLE_INGOTS),
            validateMetalTagsAreCorrect(m -> m.parts().sheets(), TFCTags.Items.PILEABLE_SHEETS),
            validatePotFluidUsability(manager),
            validateBarrelFluidUsability(manager),
            validateUniqueBloomeryRecipes(manager),
            validateUniqueLoomRecipes(manager),
            validateMoldsCanContainCastingIngredients(manager),
            validateHeatingRecipeIngredientsAreHeatable(manager)
        );
        LOGGER.info("Data pack self tests passed in {}", tick.stop());
    }

    // Public Self Test API

    /**
     * Iterate the values of a registry, filtered by mod ID
     */
    public static <T> Stream<T> stream(Registry<T> registry, String modID)
    {
        return registry.stream()
            .filter(e -> {
                final var key = registry.getKey(e);
                assert key != null;
                return key.getNamespace().equals(modID);
            });
    }

    /**
     * Used in {@code stream().flatMap(states(state predicate))} to obtain all blocks matching a state predicate.
     */
    public static Function<Block, Stream<BlockState>> states(Predicate<BlockState> filter)
    {
        return block -> block.getStateDefinition().getPossibleStates().stream().filter(filter);
    }

    /**
     * Validates that blocks declare the right combination of {@link EntityBlock}, {@link EntityBlockExtension} and {@link IForgeBlockExtension}, if they have a block entity.
     */
    public static boolean validateBlockEntities(Stream<Block> blocks, Logger logger)
    {
        final List<Block> fbeButNoEbe = new ArrayList<>(), ebeButNoFbe = new ArrayList<>(), ebButNoEbe = new ArrayList<>();
        blocks.forEach(b -> {
            if (b instanceof IForgeBlockExtension ex && ex.getExtendedProperties().hasBlockEntity() && !(b instanceof EntityBlockExtension))
            {
                fbeButNoEbe.add(b);
            }
            if (b instanceof EntityBlockExtension && (!(b instanceof IForgeBlockExtension ex) || !ex.getExtendedProperties().hasBlockEntity()))
            {
                ebeButNoFbe.add(b);
            }
            if (b instanceof EntityBlock && !(b instanceof EntityBlockExtension))
            {
                ebButNoEbe.add(b);
            }
        });

        return logRegistryErrors("{} blocks found that declare a block entity in IForgeBlockExtension but do not implement EntityBlockExtension", fbeButNoEbe, logger)
            | logRegistryErrors("{} blocks found that implement EntityBlockExtension but do not declare a block entity in IForgeBlockExtension", ebeButNoFbe, logger)
            | logRegistryErrors("{} blocks found that implement EntityBlock but do not implement EntityBlockExtension", ebButNoEbe, logger);
    }

    /**
     * Validates that a translation exists for all enum constants in the style of {@link Helpers#getEnumTranslationKey(Enum)}
     */
    public static <T extends Enum<?>> boolean validateTranslations(Logger logger, Set<String> missingTranslations, Class<? extends T> enumClass)
    {
        boolean errors = false;
        for (T enumConstant : enumClass.getEnumConstants())
        {
            errors |= validateTranslation(logger, missingTranslations, Helpers.translateEnum(enumConstant));
        }
        return errors;
    }

    /**
     * Validates that a translation exists for a component.
     */
    public static boolean validateTranslation(Logger logger, Set<String> missingTranslations, Component component)
    {
        if (component.getContents() instanceof TranslatableContents translatable)
        {
            if (!Language.getInstance().has(translatable.getKey()))
            {
                missingTranslations.add(translatable.getKey());
            }
        }
        else
        {
            logger.error("Tried to check the translation key of a non-translatable-component, this is almost certainly a bug, {}", component);
            return true;
        }
        return false;
    }

    /**
     * Validates that all blocks have a loot table defined.
     */
    public static boolean validateBlockLootTables(Stream<Block> blocks, Logger logger)
    {
        final Collection<ResourceLocation> lootTables = ServerLifecycleHooks.getCurrentServer()
            .reloadableRegistries()
            .get()
            .registryOrThrow(Registries.LOOT_TABLE)
            .keySet();
        final List<Block> missingLootTables = blocks
            .filter(b -> !lootTables.contains(b.getLootTable().location()))
            .filter(b -> !b.defaultBlockState().isAir())
            .toList();

        return logRegistryErrors("{} blocks found with a non-existent loot table:", missingLootTables, logger);
    }

    public static boolean validateBlocksHaveTag(Stream<Block> blocks, TagKey<Block> tag, Logger logger)
    {
        return logRegistryErrors("{} blocks are missing the #" + tag.location() + " tag", blocks.filter(b -> !Helpers.isBlock(b, tag)).toList(), logger);
    }

    public static <T> boolean logErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {}", e));
            return true;
        }
        return false;
    }

    public static <T> boolean logWarnings(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.warn(error, errors.size());
            errors.forEach(e -> logger.warn("  {}", e));
            return true;
        }
        return false;
    }

    public static <T> boolean logRegistryErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {} of {}", e.toString(), e.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    public static void throwIfAny(boolean... errors)
    {
        for (boolean error : errors)
        {
            if (error && THROW_ON_SELF_TEST_FAIL && Helpers.ASSERTIONS_ENABLED)
            {
                throw new AssertionError("Self Tests Failed! Fix the above errors!");
            }
        }
    }

    // Private TFC Self Tests

    public static void reportExternalError()
    {
        EXTERNAL_ERROR = true;
    }

    private static boolean validateOwnBlockEntities()
    {
        return validateBlockEntities(stream(BuiltInRegistries.BLOCK, MOD_ID), LOGGER);
    }

    private static boolean validateOwnBlockLootTables()
    {
        final Set<Block> expectedNoLootTableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE, TFCBlocks.BLOOM, TFCBlocks.MOLTEN, TFCBlocks.SCRAPING, TFCBlocks.THATCH_BED, TFCBlocks.INGOT_PILE, TFCBlocks.DOUBLE_INGOT_PILE, TFCBlocks.SHEET_PILE, TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT), TFCBlocks.PUMPKIN, TFCBlocks.MELON, TFCBlocks.CAKE, TFCBlocks.CALCITE, TFCBlocks.ICICLE, TFCBlocks.RIVER_WATER, TFCBlocks.SPRING_WATER, TFCBlocks.LIGHT, TFCBlocks.SALTWATER_BUBBLE_COLUMN, TFCBlocks.FRESHWATER_BUBBLE_COLUMN, TFCBlocks.HOT_POURED_GLASS, TFCBlocks.GLASS_BASIN, TFCBlocks.JARS)
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<Class<?>> expectedNoLootTableClasses = ImmutableSet.of(BodyPlantBlock.class, GrowingFruitTreeBranchBlock.class, LiquidBlock.class, BranchingCactusBlock.class, GrowingBranchingCactusBlock.class, PouredGlassBlock.class);
        return validateBlockLootTables(stream(BuiltInRegistries.BLOCK, MOD_ID)
            .filter(b -> !expectedNoLootTableBlocks.contains(b)).filter(b -> !expectedNoLootTableClasses.contains(b.getClass())), LOGGER);
    }

    private static boolean validateOwnBlockMineableTags()
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.SCRAPING, TFCBlocks.CANDLE, TFCBlocks.DYED_CANDLE.values(), TFCBlocks.CANDLE_CAKE, TFCBlocks.CAKE, TFCBlocks.DYED_CANDLE_CAKES.values(), TFCBlocks.HOT_POURED_GLASS, TFCBlocks.GLASS_BASIN, TFCBlocks.POURED_GLASS, TFCBlocks.COLORED_POURED_GLASS.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten).map(Supplier::get).collect(Collectors.toSet());
        final Set<TagKey<Block>> mineableTags = Set.of(
            BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL,
            TFCTags.Blocks.MINEABLE_WITH_PROPICK, TFCTags.Blocks.MINEABLE_WITH_HAMMER, TFCTags.Blocks.MINEABLE_WITH_KNIFE, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, TFCTags.Blocks.MINEABLE_WITH_CHISEL
        );
        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final List<Block> missingTag = stream(BuiltInRegistries.BLOCK, MOD_ID)
            .filter(b -> !(b instanceof LiquidBlock)
                && b.defaultDestroyTime() > 0
                && !expectedNotMineableBlocks.contains(b)
                && mineableTags.stream().noneMatch(t -> Helpers.isBlock(b, t)))
            .toList();

        return logRegistryErrors("{} non-fluid blocks have no mineable_with_<tool> tag.", missingTag, LOGGER);
    }

    private static boolean validateOwnBlockTags()
    {
        return validateBlocksHaveTag(stream(BuiltInRegistries.BLOCK, MOD_ID).filter(b -> b instanceof WallBlock), BlockTags.WALLS, LOGGER)
            | validateBlocksHaveTag(stream(BuiltInRegistries.BLOCK, MOD_ID).filter(b -> b instanceof StairBlock), BlockTags.STAIRS, LOGGER)
            | validateBlocksHaveTag(stream(BuiltInRegistries.BLOCK, MOD_ID).filter(b -> b instanceof SlabBlock), BlockTags.SLABS, LOGGER);
    }

    /**
     * Detects any instances of missing models, or missing particles, among all models.
     */
    @SuppressWarnings("deprecation")
    private static boolean validateModels()
    {
        final BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        final BakedModel missingModel = shaper.getModelManager().getMissingModel();
        final TextureAtlasSprite missingParticle = missingModel.getParticleIcon();

        final List<BlockState> missingModelErrors = stream(BuiltInRegistries.BLOCK, MOD_ID)
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = stream(BuiltInRegistries.BLOCK, MOD_ID)
            .flatMap(states(s -> !s.isAir() && !(s.getBlock() instanceof IngotPileBlock) && !(s.getBlock() instanceof SheetPileBlock) && !(s.getBlock() instanceof ScrapingBlock) && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        return logErrors("{} block states with missing models:", missingModelErrors, LOGGER)
            | logErrors("{} block states with missing particles:", missingParticleErrors, LOGGER);
    }

    /**
     * Detects any missing translation keys, for all items, in all creative tabs.
     */
    @SuppressWarnings("ConstantConditions")
    private static boolean validateTranslationsAndCreativeTabs()
    {
        final Set<String> missingTranslations = Bootstrap.getMissingTranslations();
        final List<ItemStack> stacks = new ArrayList<>();
        final Set<Item> items = new HashSet<>();

        boolean error = false;

        TFCCreativeTabs.generators().forEach(gen -> gen.accept(/* We don't use parameters, this is fine*/ null, (stack, visibility) -> {
            stacks.add(stack);
            items.add(stack.getItem());
        }));

        final Set<Class<? extends Block>> blocksWithNoCreativeTabItem = Set.of(SnowPileBlock.class, IcePileBlock.class, BloomBlock.class, MoltenBlock.class, TFCLightBlock.class, RockAnvilBlock.class, PouredGlassBlock.class);
        final List<Item> missingItems = stream(BuiltInRegistries.ITEM, MOD_ID)
            .filter(item -> !items.contains(item)
                && (item != TFCItems.FILLED_PAN.get())
                && !(item instanceof BlockItem bi && blocksWithNoCreativeTabItem.contains(bi.getBlock().getClass()))
            )
            .toList();

        error |= logErrors("{} items were not found in any TFC creative tab", missingItems, LOGGER);

        for (ItemStack stack : stacks)
        {
            error |= validateTranslation(LOGGER, missingTranslations, stack.getHoverName());
        }

        final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        BuiltInRegistries.SOUND_EVENT.forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound.getLocation())).map(WeighedSoundEvents::getSubtitle).ifPresent(subtitle -> validateTranslation(LOGGER, missingTranslations, subtitle)));

        for (CreativeModeTab tab : CreativeModeTabs.allTabs())
        {
            error |= validateTranslation(LOGGER, missingTranslations, tab.getDisplayName());
        }

        error |= Stream.of(ForgeStep.class, ForgingBonus.class, Metal.Tier.class, Heat.class, Nutrient.class, Size.class, Weight.class, Day.class, Month.class, KoppenClimateClassification.class, ForestType.class, RockDisplayCategory.class, RockDisplayCategory.class)
            .anyMatch(clazz -> validateTranslations(LOGGER, missingTranslations, clazz));

        return error | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }

    private static boolean validateFoodsAreFoods()
    {
        final List<Item> errors = Helpers.allItems(TFCTags.Items.FOODS)
            .filter(item -> !FoodCapability.has(item.getDefaultInstance()))
            .toList();
        return logWarnings("{} items were in the tfc:foods tag but lacked a food definition", errors, LOGGER);
    }

    private static boolean validateJugDrinkable()
    {
        final List<Fluid> errors = Helpers.allFluids(TFCTags.Fluids.USABLE_IN_JUG)
            .filter(fluid -> Drinkable.get(fluid) == null)
            .toList();

        return logWarnings("{} fluids were in the tfc:usable_in_jug tag but lack a Drinkable json entry", errors, LOGGER);
    }

    private static boolean validateCollapseRecipeTags(RecipeManager manager)
    {
        final List<Block> errors = manager.getAllRecipesFor(TFCRecipeTypes.COLLAPSE.get()).stream()
            .flatMap(recipe -> recipe.value().getBlockIngredient().all())
            .filter(block -> !Helpers.isBlock(block, TFCTags.Blocks.CAN_COLLAPSE))
            .toList();

        return logErrors("{} blocks were defined in a collapse recipe but lack the tfc:can_collapse tag", errors, LOGGER);
    }

    private static boolean validateLandslideRecipeTags(RecipeManager manager)
    {
        final List<Block> errors = manager.getAllRecipesFor(TFCRecipeTypes.LANDSLIDE.get()).stream()
            .flatMap(recipe -> recipe.value().getBlockIngredient().all())
            .filter(block -> !Helpers.isBlock(block, TFCTags.Blocks.CAN_LANDSLIDE))
            .toList();

        return logErrors("{} blocks were defined in a landslide recipe but lack the tfc:can_landslide tag", errors, LOGGER);
    }

    private static boolean validateMetalTagsAreCorrect(Function<Metal, Optional<Ingredient>> metalItemType, TagKey<Item> containingTag)
    {
        boolean error = false;
        for (Metal metal : Metal.MANAGER.getValues())
        {
            final @Nullable Ingredient ingredient = metalItemType.apply(metal).orElse(null);
            if (ingredient != null)
            {
                final Set<Item> metalItems = Arrays.stream(ingredient.getItems())
                    .map(ItemStack::getItem)
                    .filter(item -> !Helpers.isItem(item, containingTag))
                    .collect(Collectors.toSet());


                error |= logErrors("{} items defined in the tag for the metal " + metal.id() + " were missing from the #" + containingTag.location() + " tag", metalItems, LOGGER);
            }
        }
        return error;
    }

    private static boolean validatePotFluidUsability(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.POT.get()).stream()
            .flatMap(recipe -> RecipeHelpers.stream(recipe.value().getFluidIngredient()))
            .filter(fluid -> !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_POT))
            .toList();
        return logErrors("{} fluids are listed in pot recieps that are not tagged as tfc:usable_in_pot", errors, LOGGER);
    }

    private static boolean validateBarrelFluidUsability(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getRecipes().stream()
            .filter(recipe -> recipe.value() instanceof BarrelRecipe)
            .map(recipe -> (BarrelRecipe) recipe.value())
            .flatMap(recipe -> Stream.concat(RecipeHelpers.stream(recipe.getInputFluid()), Stream.of(recipe.getOutputFluid().getFluid())))
            .filter(fluid -> !fluid.isSame(Fluids.EMPTY) && !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_BARREL))
            .toList();
        return logErrors("{} fluids are listed in barrel recipes that are not tagged as tfc:usable_in_barrel", errors, LOGGER);
    }

    private static boolean validateUniqueBloomeryRecipes(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.BLOOMERY.get())
            .stream()
            .flatMap(recipe -> RecipeHelpers.stream(recipe.value().getInputFluid()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(m -> m.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();
        return logErrors("{} fluids appeared in multiple bloomery recipes. Currently, every bloomery recipe must have a unique fluid input in order to work", errors, LOGGER);
    }

    private static boolean validateUniqueLoomRecipes(RecipeManager manager)
    {
        final List<Item> errors = manager.getAllRecipesFor(TFCRecipeTypes.LOOM.get()).stream()
            .flatMap(recipe -> Arrays.stream(recipe.value().getItemStackIngredient().ingredient().getItems()))
            .map(ItemStack::getItem)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream().filter(m -> m.getValue() > 1)
            .map(Map.Entry::getKey).toList();
        return logErrors("{} items appeared in multiple loom recipes. Currently, every loom recipe must have a unique item input in order to work", errors, LOGGER);
    }

    private static boolean validateMoldsCanContainCastingIngredients(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.CASTING.get()).stream()
            .flatMap(recipe -> RecipeHelpers.stream(recipe.value().getIngredient())
                .filter(item -> item instanceof MoldItem)
                .flatMap(item -> RecipeHelpers.stream(recipe.value().getFluidIngredient())
                    .filter(fluid -> !Helpers.isFluid(fluid, ((MoldItem) item).getFluidTag())))
            ).toList();

        return logErrors("{} fluids were found that were given as ingredients in a casting recipe that could not be put into the specified mold. This probably means that you need to add fluids to the tfc:usable_in_tool_head_mold or tfc:usable_in_ingot_mold tag.", errors, LOGGER);
    }

    private static boolean validateHeatingRecipeIngredientsAreHeatable(RecipeManager manager)
    {
        final List<ItemStack> errors = manager.getAllRecipesFor(TFCRecipeTypes.HEATING.get())
            .stream()
            .flatMap(recipe -> Arrays.stream(recipe.value().getIngredient().getItems()))
            .filter(stack -> HeatCapability.getDefinition(stack) == null)
            .toList();
        return logErrors("{} items found as ingredients to heating recipes without a heat definition!", errors, LOGGER);
    }

    private static boolean validateReplaceableBlocksAreTagged()
    {
        final TagKey<Block> tag = TagKey.create(Registries.BLOCK, Helpers.identifierMC("replaceable"));
        final List<Block> notTagged = BuiltInRegistries.BLOCK.stream().filter(b -> b.defaultBlockState().canBeReplaced() && !Helpers.isBlock(b, tag) && !BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals("minecraft")).toList();
        final List<Block> shouldNotBeTagged = Helpers.allBlocks(tag).filter(b -> !b.defaultBlockState().canBeReplaced()).toList();
        return logErrors("{} blocks are not tagged as minecraft:replaceable while being replaceable.", notTagged, LOGGER)
            | logErrors("{} blocks are tagged as minecraft:replaceable while being not replaceable.", shouldNotBeTagged, LOGGER);
    }

    /**
     * Fired from the entry point where client self tests are invoked in TFC
     * This is provided for convenience for any addon mods which wish to use this same entrypoint,
     * but don't want to duplicate the provided mixin.
     */
    public static class ClientSelfTestEvent extends Event {}
}
