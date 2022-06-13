/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import org.slf4j.Logger;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Central location for all self tests
 * These are tests that are performed at runtime for various static analysis purposes.
 * None of these are executed unless {@link Helpers#ASSERTIONS_ENABLED} is {@code true}.
 */
public final class SelfTests
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean THROW_ON_SELF_TEST_FAIL = true;

    public static void runClientSelfTests()
    {
        if (Helpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockEntities(),
                validateModels(),
                validateTranslations()
            );
            MinecraftForge.EVENT_BUS.post(new ClientSelfTestEvent()); // For other mods, as this is invoked via a tricky mixin
            LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    public static void runServerSelfTests()
    {
        if (Helpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockLootTables(),
                validateOwnBlockMineableTags()
            );
            LOGGER.info("Server self tests passed in {}", tick.stop());
        }
    }

    // Public Self Test API

    /**
     * Iterate the values of a registry, filtered by mod ID
     */
    public static <T extends IForgeRegistryEntry<T>> Stream<T> stream(IForgeRegistry<T> registry, String modID)
    {
        return registry.getValues().stream()
            .filter(e -> {
                assert e.getRegistryName() != null;
                return e.getRegistryName().getNamespace().equals(modID);
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
     * Validates that all blocks have a loot table defined.
     */
    public static boolean validateBlockLootTables(Stream<Block> blocks, Logger logger)
    {
        final Set<ResourceLocation> lootTables = ServerLifecycleHooks.getCurrentServer().getLootTables().getIds();
        final List<Block> missingLootTables = blocks
            .filter(b -> !lootTables.contains(b.getLootTable()))
            .toList();

        return logRegistryErrors("{} blocks found with a non-existent loot table:", missingLootTables, logger);
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

    public static <T extends IForgeRegistryEntry<T>> boolean logRegistryErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {} of {}", e.getRegistryName(), e.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    public static void throwIfAny(boolean... errors)
    {
        for (boolean error : errors)
        {
            if (error && THROW_ON_SELF_TEST_FAIL)
            {
                throw new AssertionError("Self Tests Failed! Fix the above errors!");
            }
        }
    }

    // Private TFC Self Tests

    private static boolean validateOwnBlockEntities()
    {
        return validateBlockEntities(stream(ForgeRegistries.BLOCKS, MOD_ID), LOGGER);
    }

    private static boolean validateOwnBlockLootTables()
    {
        final Set<Block> expectedNoLootTableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE, TFCBlocks.BLOOM, TFCBlocks.MOLTEN, TFCBlocks.SCRAPING, TFCBlocks.THATCH_BED, TFCBlocks.INGOT_PILE, TFCBlocks.SHEET_PILE, TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT))
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<Class<?>> expectedNoLootTableClasses = ImmutableSet.of(BodyPlantBlock.class, GrowingFruitTreeBranchBlock.class);
        return validateBlockLootTables(stream(ForgeRegistries.BLOCKS, MOD_ID)
            .filter(b -> !expectedNoLootTableBlocks.contains(b)).filter(b -> !expectedNoLootTableClasses.contains(b.getClass())), LOGGER);
    }

    private static boolean validateOwnBlockMineableTags()
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.SCRAPING, TFCBlocks.CANDLE, TFCBlocks.DYED_CANDLE.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten).map(Supplier::get).collect(Collectors.toSet());
        final Set<TagKey<Block>> mineableTags = Set.of(
            BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL,
            TFCTags.Blocks.MINEABLE_WITH_PROPICK, TFCTags.Blocks.MINEABLE_WITH_HAMMER, TFCTags.Blocks.MINEABLE_WITH_KNIFE, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, TFCTags.Blocks.MINEABLE_WITH_CHISEL
        );
        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final List<Block> missingTag = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .filter(b -> !(b instanceof LiquidBlock)
                && b.defaultDestroyTime() > 0
                && !expectedNotMineableBlocks.contains(b)
                && mineableTags.stream().noneMatch(t -> Helpers.isBlock(b, t)))
            .toList();

        return logRegistryErrors("{} non-fluid blocks have no mineable_with_<tool> tag.", missingTag, LOGGER);
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

        final List<BlockState> missingModelErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> !s.isAir() && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        return logErrors("{} block states with missing models:", missingModelErrors, LOGGER)
            | logErrors("{} block states with missing particles:", missingParticleErrors, LOGGER);
    }

    /**
     * Detects any missing translation keys, for all items, in all creative tabs.
     */
    private static boolean validateTranslations()
    {
        final Set<String> missingTranslations = Bootstrap.getMissingTranslations();
        final NonNullList<ItemStack> items = NonNullList.create();

        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            items.clear();
            item.fillItemCategory(CreativeModeTab.TAB_SEARCH, items);
            items.forEach(stack -> validateTranslation(missingTranslations, stack.getHoverName()));
        });
        
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        ForgeRegistries.SOUND_EVENTS.getKeys().forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound)).map(WeighedSoundEvents::getSubtitle).ifPresent(subtitle -> validateTranslation(missingTranslations, subtitle)));

        boolean metaError = false;
        for (CreativeModeTab tab : CreativeModeTab.TABS)
        {
            metaError |= validateTranslation(missingTranslations, tab.getDisplayName());
        }

        return metaError | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }

    private static boolean validateTranslation(Set<String> missingTranslations, Component component)
    {
        if (component instanceof TranslatableComponent translatable)
        {
            if (!Language.getInstance().has(translatable.getKey()))
            {
                missingTranslations.add(translatable.getKey());
            }
        }
        else
        {
            LOGGER.error("Tried to check the translation key of a non-translatable-component, this is almost certainly a bug, {}", component);
            return true;
        }
        return false;
    }

    public static class ClientSelfTestEvent extends Event {}
}
