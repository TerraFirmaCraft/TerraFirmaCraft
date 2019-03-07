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

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

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
            new RockCategory(IGNEOUS_INTRUSIVE, ToolMaterialsTFC.IG_IN, true, true, true, -0.4f, 0f, true),
            new RockCategory(IGNEOUS_EXTRUSIVE, ToolMaterialsTFC.IG_EX, true, true, true, -0.5f, 0f, true),
            new RockCategory(SEDIMENTARY, ToolMaterialsTFC.SED, true, false, false, 0.3f, 5f, false),
            new RockCategory(METAMORPHIC, ToolMaterialsTFC.M_M, true, true, false, 0.2f, 0f, false)
        );
    }

    @SubscribeEvent
    public static void onPreRegisterRock(TFCRegistryEvent.RegisterPreBlock<Rock> event)
    {
        event.getRegistry().registerAll(
            new Rock(GRANITE, IGNEOUS_INTRUSIVE),
            new Rock(DIORITE, IGNEOUS_INTRUSIVE),
            new Rock(GABBRO, IGNEOUS_INTRUSIVE),
            new Rock(SHALE, SEDIMENTARY),
            new Rock(CLAYSTONE, SEDIMENTARY),
            new Rock(ROCKSALT, SEDIMENTARY),
            new Rock(LIMESTONE, SEDIMENTARY),
            new Rock(CONGLOMERATE, SEDIMENTARY),
            new Rock(DOLOMITE, SEDIMENTARY),
            new Rock(CHERT, SEDIMENTARY),
            new Rock(CHALK, SEDIMENTARY),
            new Rock(RHYOLITE, IGNEOUS_EXTRUSIVE),
            new Rock(BASALT, IGNEOUS_EXTRUSIVE),
            new Rock(ANDESITE, IGNEOUS_EXTRUSIVE),
            new Rock(DACITE, IGNEOUS_EXTRUSIVE),
            new Rock(QUARTZITE, METAMORPHIC),
            new Rock(SLATE, METAMORPHIC),
            new Rock(PHYLLITE, METAMORPHIC),
            new Rock(SCHIST, METAMORPHIC),
            new Rock(GNEISS, METAMORPHIC),
            new Rock(MARBLE, METAMORPHIC)
        );
    }
}
