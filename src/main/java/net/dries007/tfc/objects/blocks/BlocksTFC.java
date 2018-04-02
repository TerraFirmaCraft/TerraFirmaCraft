package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTab.CT_MISC;
import static net.dries007.tfc.objects.CreativeTab.CT_ROCK_SOIL;
import static net.dries007.tfc.objects.blocks.BlockTFCVariant.Type.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BlocksTFC
{
    private BlocksTFC() {}

    public static final BlockDebug DEBUG = null;

    public static final BlockTFCVariant RAW_GRANITE = null;
    public static final BlockTFCVariant RAW_DIORITE = null;
    public static final BlockTFCVariant RAW_GABBRO = null;
    public static final BlockTFCVariant RAW_SHALE = null;
    public static final BlockTFCVariant RAW_CLAYSTONE = null;
    public static final BlockTFCVariant RAW_ROCKSALT = null;
    public static final BlockTFCVariant RAW_LIMESTONE = null;
    public static final BlockTFCVariant RAW_CONGLOMERATE = null;
    public static final BlockTFCVariant RAW_DOLOMITE = null;
    public static final BlockTFCVariant RAW_CHERT = null;
    public static final BlockTFCVariant RAW_CHALK = null;
    public static final BlockTFCVariant RAW_RHYOLITE = null;
    public static final BlockTFCVariant RAW_BASALT = null;
    public static final BlockTFCVariant RAW_ANDESITE = null;
    public static final BlockTFCVariant RAW_DACITE = null;
    public static final BlockTFCVariant RAW_QUARTZITE = null;
    public static final BlockTFCVariant RAW_SLATE = null;
    public static final BlockTFCVariant RAW_PHYLLITE = null;
    public static final BlockTFCVariant RAW_SCHIST = null;
    public static final BlockTFCVariant RAW_GNEISS = null;
    public static final BlockTFCVariant RAW_MARBLE = null;
    public static final BlockTFCVariant SMOOTH_GRANITE = null;
    public static final BlockTFCVariant SMOOTH_DIORITE = null;
    public static final BlockTFCVariant SMOOTH_GABBRO = null;
    public static final BlockTFCVariant SMOOTH_SHALE = null;
    public static final BlockTFCVariant SMOOTH_CLAYSTONE = null;
    public static final BlockTFCVariant SMOOTH_ROCKSALT = null;
    public static final BlockTFCVariant SMOOTH_LIMESTONE = null;
    public static final BlockTFCVariant SMOOTH_CONGLOMERATE = null;
    public static final BlockTFCVariant SMOOTH_DOLOMITE = null;
    public static final BlockTFCVariant SMOOTH_CHERT = null;
    public static final BlockTFCVariant SMOOTH_CHALK = null;
    public static final BlockTFCVariant SMOOTH_RHYOLITE = null;
    public static final BlockTFCVariant SMOOTH_BASALT = null;
    public static final BlockTFCVariant SMOOTH_ANDESITE = null;
    public static final BlockTFCVariant SMOOTH_DACITE = null;
    public static final BlockTFCVariant SMOOTH_QUARTZITE = null;
    public static final BlockTFCVariant SMOOTH_SLATE = null;
    public static final BlockTFCVariant SMOOTH_PHYLLITE = null;
    public static final BlockTFCVariant SMOOTH_SCHIST = null;
    public static final BlockTFCVariant SMOOTH_GNEISS = null;
    public static final BlockTFCVariant SMOOTH_MARBLE = null;
    public static final BlockTFCVariant COBBLE_GRANITE = null;
    public static final BlockTFCVariant COBBLE_DIORITE = null;
    public static final BlockTFCVariant COBBLE_GABBRO = null;
    public static final BlockTFCVariant COBBLE_SHALE = null;
    public static final BlockTFCVariant COBBLE_CLAYSTONE = null;
    public static final BlockTFCVariant COBBLE_ROCKSALT = null;
    public static final BlockTFCVariant COBBLE_LIMESTONE = null;
    public static final BlockTFCVariant COBBLE_CONGLOMERATE = null;
    public static final BlockTFCVariant COBBLE_DOLOMITE = null;
    public static final BlockTFCVariant COBBLE_CHERT = null;
    public static final BlockTFCVariant COBBLE_CHALK = null;
    public static final BlockTFCVariant COBBLE_RHYOLITE = null;
    public static final BlockTFCVariant COBBLE_BASALT = null;
    public static final BlockTFCVariant COBBLE_ANDESITE = null;
    public static final BlockTFCVariant COBBLE_DACITE = null;
    public static final BlockTFCVariant COBBLE_QUARTZITE = null;
    public static final BlockTFCVariant COBBLE_SLATE = null;
    public static final BlockTFCVariant COBBLE_PHYLLITE = null;
    public static final BlockTFCVariant COBBLE_SCHIST = null;
    public static final BlockTFCVariant COBBLE_GNEISS = null;
    public static final BlockTFCVariant COBBLE_MARBLE = null;
    public static final BlockTFCVariant BRICK_GRANITE = null;
    public static final BlockTFCVariant BRICK_DIORITE = null;
    public static final BlockTFCVariant BRICK_GABBRO = null;
    public static final BlockTFCVariant BRICK_SHALE = null;
    public static final BlockTFCVariant BRICK_CLAYSTONE = null;
    public static final BlockTFCVariant BRICK_ROCKSALT = null;
    public static final BlockTFCVariant BRICK_LIMESTONE = null;
    public static final BlockTFCVariant BRICK_CONGLOMERATE = null;
    public static final BlockTFCVariant BRICK_DOLOMITE = null;
    public static final BlockTFCVariant BRICK_CHERT = null;
    public static final BlockTFCVariant BRICK_CHALK = null;
    public static final BlockTFCVariant BRICK_RHYOLITE = null;
    public static final BlockTFCVariant BRICK_BASALT = null;
    public static final BlockTFCVariant BRICK_ANDESITE = null;
    public static final BlockTFCVariant BRICK_DACITE = null;
    public static final BlockTFCVariant BRICK_QUARTZITE = null;
    public static final BlockTFCVariant BRICK_SLATE = null;
    public static final BlockTFCVariant BRICK_PHYLLITE = null;
    public static final BlockTFCVariant BRICK_SCHIST = null;
    public static final BlockTFCVariant BRICK_GNEISS = null;
    public static final BlockTFCVariant BRICK_MARBLE = null;
    public static final BlockTFCVariant SAND_GRANITE = null;
    public static final BlockTFCVariant SAND_DIORITE = null;
    public static final BlockTFCVariant SAND_GABBRO = null;
    public static final BlockTFCVariant SAND_SHALE = null;
    public static final BlockTFCVariant SAND_CLAYSTONE = null;
    public static final BlockTFCVariant SAND_ROCKSALT = null;
    public static final BlockTFCVariant SAND_LIMESTONE = null;
    public static final BlockTFCVariant SAND_CONGLOMERATE = null;
    public static final BlockTFCVariant SAND_DOLOMITE = null;
    public static final BlockTFCVariant SAND_CHERT = null;
    public static final BlockTFCVariant SAND_CHALK = null;
    public static final BlockTFCVariant SAND_RHYOLITE = null;
    public static final BlockTFCVariant SAND_BASALT = null;
    public static final BlockTFCVariant SAND_ANDESITE = null;
    public static final BlockTFCVariant SAND_DACITE = null;
    public static final BlockTFCVariant SAND_QUARTZITE = null;
    public static final BlockTFCVariant SAND_SLATE = null;
    public static final BlockTFCVariant SAND_PHYLLITE = null;
    public static final BlockTFCVariant SAND_SCHIST = null;
    public static final BlockTFCVariant SAND_GNEISS = null;
    public static final BlockTFCVariant SAND_MARBLE = null;
    public static final BlockTFCVariant GRAVEL_GRANITE = null;
    public static final BlockTFCVariant GRAVEL_DIORITE = null;
    public static final BlockTFCVariant GRAVEL_GABBRO = null;
    public static final BlockTFCVariant GRAVEL_SHALE = null;
    public static final BlockTFCVariant GRAVEL_CLAYSTONE = null;
    public static final BlockTFCVariant GRAVEL_ROCKSALT = null;
    public static final BlockTFCVariant GRAVEL_LIMESTONE = null;
    public static final BlockTFCVariant GRAVEL_CONGLOMERATE = null;
    public static final BlockTFCVariant GRAVEL_DOLOMITE = null;
    public static final BlockTFCVariant GRAVEL_CHERT = null;
    public static final BlockTFCVariant GRAVEL_CHALK = null;
    public static final BlockTFCVariant GRAVEL_RHYOLITE = null;
    public static final BlockTFCVariant GRAVEL_BASALT = null;
    public static final BlockTFCVariant GRAVEL_ANDESITE = null;
    public static final BlockTFCVariant GRAVEL_DACITE = null;
    public static final BlockTFCVariant GRAVEL_QUARTZITE = null;
    public static final BlockTFCVariant GRAVEL_SLATE = null;
    public static final BlockTFCVariant GRAVEL_PHYLLITE = null;
    public static final BlockTFCVariant GRAVEL_SCHIST = null;
    public static final BlockTFCVariant GRAVEL_GNEISS = null;
    public static final BlockTFCVariant GRAVEL_MARBLE = null;
    public static final BlockTFCVariant DIRT_GRANITE = null;
    public static final BlockTFCVariant DIRT_DIORITE = null;
    public static final BlockTFCVariant DIRT_GABBRO = null;
    public static final BlockTFCVariant DIRT_SHALE = null;
    public static final BlockTFCVariant DIRT_CLAYSTONE = null;
    public static final BlockTFCVariant DIRT_ROCKSALT = null;
    public static final BlockTFCVariant DIRT_LIMESTONE = null;
    public static final BlockTFCVariant DIRT_CONGLOMERATE = null;
    public static final BlockTFCVariant DIRT_DOLOMITE = null;
    public static final BlockTFCVariant DIRT_CHERT = null;
    public static final BlockTFCVariant DIRT_CHALK = null;
    public static final BlockTFCVariant DIRT_RHYOLITE = null;
    public static final BlockTFCVariant DIRT_BASALT = null;
    public static final BlockTFCVariant DIRT_ANDESITE = null;
    public static final BlockTFCVariant DIRT_DACITE = null;
    public static final BlockTFCVariant DIRT_QUARTZITE = null;
    public static final BlockTFCVariant DIRT_SLATE = null;
    public static final BlockTFCVariant DIRT_PHYLLITE = null;
    public static final BlockTFCVariant DIRT_SCHIST = null;
    public static final BlockTFCVariant DIRT_GNEISS = null;
    public static final BlockTFCVariant DIRT_MARBLE = null;
    public static final BlockTFCVariant GRASS_GRANITE = null;
    public static final BlockTFCVariant GRASS_DIORITE = null;
    public static final BlockTFCVariant GRASS_GABBRO = null;
    public static final BlockTFCVariant GRASS_SHALE = null;
    public static final BlockTFCVariant GRASS_CLAYSTONE = null;
    public static final BlockTFCVariant GRASS_ROCKSALT = null;
    public static final BlockTFCVariant GRASS_LIMESTONE = null;
    public static final BlockTFCVariant GRASS_CONGLOMERATE = null;
    public static final BlockTFCVariant GRASS_DOLOMITE = null;
    public static final BlockTFCVariant GRASS_CHERT = null;
    public static final BlockTFCVariant GRASS_CHALK = null;
    public static final BlockTFCVariant GRASS_RHYOLITE = null;
    public static final BlockTFCVariant GRASS_BASALT = null;
    public static final BlockTFCVariant GRASS_ANDESITE = null;
    public static final BlockTFCVariant GRASS_DACITE = null;
    public static final BlockTFCVariant GRASS_QUARTZITE = null;
    public static final BlockTFCVariant GRASS_SLATE = null;
    public static final BlockTFCVariant GRASS_PHYLLITE = null;
    public static final BlockTFCVariant GRASS_SCHIST = null;
    public static final BlockTFCVariant GRASS_GNEISS = null;
    public static final BlockTFCVariant GRASS_MARBLE = null;
    public static final BlockTFCVariant DRY_GRASS_GRANITE = null;
    public static final BlockTFCVariant DRY_GRASS_DIORITE = null;
    public static final BlockTFCVariant DRY_GRASS_GABBRO = null;
    public static final BlockTFCVariant DRY_GRASS_SHALE = null;
    public static final BlockTFCVariant DRY_GRASS_CLAYSTONE = null;
    public static final BlockTFCVariant DRY_GRASS_ROCKSALT = null;
    public static final BlockTFCVariant DRY_GRASS_LIMESTONE = null;
    public static final BlockTFCVariant DRY_GRASS_CONGLOMERATE = null;
    public static final BlockTFCVariant DRY_GRASS_DOLOMITE = null;
    public static final BlockTFCVariant DRY_GRASS_CHERT = null;
    public static final BlockTFCVariant DRY_GRASS_CHALK = null;
    public static final BlockTFCVariant DRY_GRASS_RHYOLITE = null;
    public static final BlockTFCVariant DRY_GRASS_BASALT = null;
    public static final BlockTFCVariant DRY_GRASS_ANDESITE = null;
    public static final BlockTFCVariant DRY_GRASS_DACITE = null;
    public static final BlockTFCVariant DRY_GRASS_QUARTZITE = null;
    public static final BlockTFCVariant DRY_GRASS_SLATE = null;
    public static final BlockTFCVariant DRY_GRASS_PHYLLITE = null;
    public static final BlockTFCVariant DRY_GRASS_SCHIST = null;
    public static final BlockTFCVariant DRY_GRASS_GNEISS = null;
    public static final BlockTFCVariant DRY_GRASS_MARBLE = null;

    private static ImmutableList<BlockTFCVariant> allBlockTFCVariants;

    public static ImmutableList<BlockTFCVariant> getAllBlockTFCVariants()
    {
        return allBlockTFCVariants;
    }

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        register(r, "debug", new BlockDebug(), CT_MISC);

        ImmutableList.Builder<BlockTFCVariant> b = ImmutableList.builder();

        b.add(register(r, "raw_granite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_diorite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_gabbro", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_shale", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_claystone", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_rocksalt", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_limestone", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_conglomerate", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_dolomite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_chert", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_chalk", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_rhyolite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_basalt", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_andesite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_dacite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_quartzite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_slate", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_phyllite", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_schist", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_gneiss", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "raw_marble", new BlockTFCVariant(Material.ROCK, RAW)));
        b.add(register(r, "smooth_granite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_diorite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_gabbro", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_shale", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_claystone", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_rocksalt", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_limestone", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_conglomerate", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_dolomite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_chert", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_chalk", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_rhyolite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_basalt", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_andesite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_dacite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_quartzite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_slate", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_phyllite", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_schist", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_gneiss", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "smooth_marble", new BlockTFCVariant(Material.ROCK, SMOOTH)));
        b.add(register(r, "cobble_granite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_diorite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_gabbro", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_shale", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_claystone", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_rocksalt", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_limestone", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_conglomerate", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_dolomite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_chert", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_chalk", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_rhyolite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_basalt", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_andesite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_dacite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_quartzite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_slate", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_phyllite", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_schist", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_gneiss", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "cobble_marble", new BlockTFCVariant(Material.ROCK, COBBLE)));
        b.add(register(r, "brick_granite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_diorite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_gabbro", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_shale", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_claystone", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_rocksalt", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_limestone", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_conglomerate", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_dolomite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_chert", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_chalk", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_rhyolite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_basalt", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_andesite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_dacite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_quartzite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_slate", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_phyllite", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_schist", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_gneiss", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "brick_marble", new BlockTFCVariant(Material.ROCK, BRICK)));
        b.add(register(r, "sand_granite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_diorite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_gabbro", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_shale", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_claystone", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_rocksalt", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_limestone", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_conglomerate", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_dolomite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_chert", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_chalk", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_rhyolite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_basalt", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_andesite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_dacite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_quartzite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_slate", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_phyllite", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_schist", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_gneiss", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "sand_marble", new BlockTFCVariant(Material.SAND, SAND)));
        b.add(register(r, "gravel_granite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_diorite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_gabbro", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_shale", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_claystone", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_rocksalt", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_limestone", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_conglomerate", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_dolomite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_chert", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_chalk", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_rhyolite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_basalt", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_andesite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_dacite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_quartzite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_slate", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_phyllite", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_schist", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_gneiss", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "gravel_marble", new BlockTFCVariant(Material.SAND, GRAVEL)));
        b.add(register(r, "dirt_granite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_diorite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_gabbro", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_shale", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_claystone", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_rocksalt", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_limestone", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_conglomerate", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_dolomite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_chert", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_chalk", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_rhyolite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_basalt", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_andesite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_dacite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_quartzite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_slate", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_phyllite", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_schist", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_gneiss", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "dirt_marble", new BlockTFCVariant(Material.GROUND, DIRT)));
        b.add(register(r, "grass_granite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_diorite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_gabbro", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_shale", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_claystone", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_rocksalt", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_limestone", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_conglomerate", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_dolomite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_chert", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_chalk", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_rhyolite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_basalt", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_andesite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_dacite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_quartzite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_slate", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_phyllite", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_schist", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_gneiss", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "grass_marble", new BlockTFCVariant(Material.GRASS, GRASS)));
        b.add(register(r, "dry_grass_granite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_diorite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_gabbro", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_shale", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_claystone", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_rocksalt", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_limestone", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_conglomerate", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_dolomite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_chert", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_chalk", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_rhyolite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_basalt", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_andesite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_dacite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_quartzite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_slate", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_phyllite", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_schist", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_gneiss", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));
        b.add(register(r, "dry_grass_marble", new BlockTFCVariant(Material.GRASS, DRY_GRASS)));

        allBlockTFCVariants = b.build();
    }

    private static BlockTFCVariant register(IForgeRegistry<Block> r, String name, BlockTFCVariant item)
    {
        item.setResistance(0).setHardness(0); //todo: remove
        return register(r, name, item, CT_ROCK_SOIL);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));
        item.setCreativeTab(ct);

        r.register(item);
        return item;
    }

    public static boolean isWater(IBlockState current)
    {
        // todo: check for other water types
        // todo: replace
        //noinspection deprecation
        return current == Blocks.STAINED_GLASS.getStateFromMeta(EnumDyeColor.BLUE.getMetadata()) ||
                current == Blocks.STAINED_GLASS.getStateFromMeta(EnumDyeColor.LIGHT_BLUE.getMetadata());
    }

    // todo: change to property of type? (soil & stone maybe?)

    public static boolean isRawStone(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == RAW;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == DIRT;
    }

    public static boolean isSoil(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS;
    }
}
