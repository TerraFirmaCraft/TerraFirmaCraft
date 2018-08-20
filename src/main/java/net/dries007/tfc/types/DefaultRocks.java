/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.types.TFCRegistries;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultRocks
{
    private static final ResourceLocation SEDIMENTARY = new ResourceLocation(MOD_ID, "sedimentary");
    private static final ResourceLocation METAMORPHIC = new ResourceLocation(MOD_ID, "metamorphic");
    private static final ResourceLocation IGNEOUS_INTRUSIVE = new ResourceLocation(MOD_ID, "igneous_intrusive");
    private static final ResourceLocation IGNEOUS_EXTRUSIVE = new ResourceLocation(MOD_ID, "igneous_extrusive");

    private static final ResourceLocation GRANITE = new ResourceLocation(MOD_ID, "granite");
    private static final ResourceLocation DIORITE = new ResourceLocation(MOD_ID, "diorite");
    private static final ResourceLocation GABBRO = new ResourceLocation(MOD_ID, "gabbro");
    private static final ResourceLocation SHALE = new ResourceLocation(MOD_ID, "shale");
    private static final ResourceLocation CLAYSTONE = new ResourceLocation(MOD_ID, "claystone");
    private static final ResourceLocation ROCKSALT = new ResourceLocation(MOD_ID, "rocksalt");
    private static final ResourceLocation LIMESTONE = new ResourceLocation(MOD_ID, "limestone");
    private static final ResourceLocation CONGLOMERATE = new ResourceLocation(MOD_ID, "conglomerate");
    private static final ResourceLocation DOLOMITE = new ResourceLocation(MOD_ID, "dolomite");
    private static final ResourceLocation CHERT = new ResourceLocation(MOD_ID, "chert");
    private static final ResourceLocation CHALK = new ResourceLocation(MOD_ID, "chalk");
    private static final ResourceLocation RHYOLITE = new ResourceLocation(MOD_ID, "rhyolite");
    private static final ResourceLocation BASALT = new ResourceLocation(MOD_ID, "basalt");
    private static final ResourceLocation ANDESITE = new ResourceLocation(MOD_ID, "andesite");
    private static final ResourceLocation DACITE = new ResourceLocation(MOD_ID, "dacite");
    private static final ResourceLocation QUARTZITE = new ResourceLocation(MOD_ID, "quartzite");
    private static final ResourceLocation SLATE = new ResourceLocation(MOD_ID, "slate");
    private static final ResourceLocation PHYLLITE = new ResourceLocation(MOD_ID, "phyllite");
    private static final ResourceLocation SCHIST = new ResourceLocation(MOD_ID, "schist");
    private static final ResourceLocation GNEISS = new ResourceLocation(MOD_ID, "gneiss");
    private static final ResourceLocation MARBLE = new ResourceLocation(MOD_ID, "marble");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<RockCategory> event)
    {
        IForgeRegistry<RockCategory> r = event.getRegistry();
        r.registerAll(
            new RockCategory(IGNEOUS_INTRUSIVE, ToolMaterialsTFC.IG_IN, true, true, true, -0.4f, 0f),
            new RockCategory(IGNEOUS_EXTRUSIVE, ToolMaterialsTFC.IG_EX, true, true, true, -0.5f, 0f),
            new RockCategory(SEDIMENTARY, ToolMaterialsTFC.SED, true, false, false, 0.3f, 5f),
            new RockCategory(METAMORPHIC, ToolMaterialsTFC.M_M, true, true, false, 0.2f, 0f)
        );
    }

    @SubscribeEvent
    public static void onPreRegisterRock(TFCRegistries.RegisterPreBlock<Rock> event)
    {
        IForgeRegistry<Rock> r = event.getRegistry();
        r.registerAll(
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
