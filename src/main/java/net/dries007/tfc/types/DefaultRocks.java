/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultRocks
{
    public static final ResourceLocation SEDIMENTARY = new ResourceLocation(MOD_ID, "sedimentary");
    public static final ResourceLocation METAMORPHIC = new ResourceLocation(MOD_ID, "metamorphic");
    public static final ResourceLocation IGNEOUS_INTRUSIVE = new ResourceLocation(MOD_ID, "igneous_intrusive");
    public static final ResourceLocation IGNEOUS_EXTRUSIVE = new ResourceLocation(MOD_ID, "igneous_extrusive");

    public static final ResourceLocation GRANITE = new ResourceLocation(MOD_ID, "granite");
    public static final ResourceLocation DIORITE = new ResourceLocation(MOD_ID, "diorite");
    public static final ResourceLocation GABBRO = new ResourceLocation(MOD_ID, "gabbro");
    public static final ResourceLocation SHALE = new ResourceLocation(MOD_ID, "shale");
    public static final ResourceLocation CLAYSTONE = new ResourceLocation(MOD_ID, "claystone");
    public static final ResourceLocation ROCKSALT = new ResourceLocation(MOD_ID, "rocksalt");
    public static final ResourceLocation LIMESTONE = new ResourceLocation(MOD_ID, "limestone");
    public static final ResourceLocation CONGLOMERATE = new ResourceLocation(MOD_ID, "conglomerate");
    public static final ResourceLocation DOLOMITE = new ResourceLocation(MOD_ID, "dolomite");
    public static final ResourceLocation CHERT = new ResourceLocation(MOD_ID, "chert");
    public static final ResourceLocation CHALK = new ResourceLocation(MOD_ID, "chalk");
    public static final ResourceLocation RHYOLITE = new ResourceLocation(MOD_ID, "rhyolite");
    public static final ResourceLocation BASALT = new ResourceLocation(MOD_ID, "basalt");
    public static final ResourceLocation ANDESITE = new ResourceLocation(MOD_ID, "andesite");
    public static final ResourceLocation DACITE = new ResourceLocation(MOD_ID, "dacite");
    public static final ResourceLocation QUARTZITE = new ResourceLocation(MOD_ID, "quartzite");
    public static final ResourceLocation SLATE = new ResourceLocation(MOD_ID, "slate");
    public static final ResourceLocation PHYLLITE = new ResourceLocation(MOD_ID, "phyllite");
    public static final ResourceLocation SCHIST = new ResourceLocation(MOD_ID, "schist");
    public static final ResourceLocation GNEISS = new ResourceLocation(MOD_ID, "gneiss");
    public static final ResourceLocation MARBLE = new ResourceLocation(MOD_ID, "marble");

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onPreRegisterRockCategory(TFCRegistryEvent.RegisterPreBlock<RockCategory> event)
    {
        event.getRegistry().registerAll(
            new RockCategory(IGNEOUS_INTRUSIVE, ToolMaterialsTFC.IGNEOUS_INTRUSIVE, true, true, true, -0.4f, 0f, 1.6F, 10F, true),
            new RockCategory(IGNEOUS_EXTRUSIVE, ToolMaterialsTFC.IGNEOUS_EXTRUSIVE, true, true, true, -0.5f, 0f, 1.6F, 10F, true),
            new RockCategory(SEDIMENTARY, ToolMaterialsTFC.SEDIMENTARY, true, false, false, 0.3f, 5f, 1.4F, 10F, false),
            new RockCategory(METAMORPHIC, ToolMaterialsTFC.METAMORPHIC, true, true, false, 0.2f, 0f, 1.5F, 10F, false)
        );
    }

    @SubscribeEvent
    public static void onPreRegisterRock(TFCRegistryEvent.RegisterPreBlock<Rock> event)
    {
        event.getRegistry().registerAll(
            new Rock(GRANITE, IGNEOUS_INTRUSIVE, false),
            new Rock(DIORITE, IGNEOUS_INTRUSIVE, false),
            new Rock(GABBRO, IGNEOUS_INTRUSIVE, false),
            new Rock(SHALE, SEDIMENTARY, false),
            new Rock(CLAYSTONE, SEDIMENTARY, false),
            new Rock(ROCKSALT, SEDIMENTARY, false),
            new Rock(LIMESTONE, SEDIMENTARY, true),
            new Rock(CONGLOMERATE, SEDIMENTARY, false),
            new Rock(DOLOMITE, SEDIMENTARY, true),
            new Rock(CHERT, SEDIMENTARY, false),
            new Rock(CHALK, SEDIMENTARY, true),
            new Rock(RHYOLITE, IGNEOUS_EXTRUSIVE, false),
            new Rock(BASALT, IGNEOUS_EXTRUSIVE, false),
            new Rock(ANDESITE, IGNEOUS_EXTRUSIVE, false),
            new Rock(DACITE, IGNEOUS_EXTRUSIVE, false),
            new Rock(QUARTZITE, METAMORPHIC, false),
            new Rock(SLATE, METAMORPHIC, false),
            new Rock(PHYLLITE, METAMORPHIC, false),
            new Rock(SCHIST, METAMORPHIC, false),
            new Rock(GNEISS, METAMORPHIC, false),
            new Rock(MARBLE, METAMORPHIC, true)
        );
    }
}
