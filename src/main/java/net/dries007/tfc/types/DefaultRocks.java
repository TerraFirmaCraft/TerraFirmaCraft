/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.types.TFCRegistries;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultRocks
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
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<RockCategory> event)
    {
        IForgeRegistry<RockCategory> r = event.getRegistry();
        r.register(new RockCategory(SEDIMENTARY, ToolMaterialsTFC.SED));
        r.register(new RockCategory(METAMORPHIC, ToolMaterialsTFC.M_M));
        r.register(new RockCategory(IGNEOUS_INTRUSIVE, ToolMaterialsTFC.IG_EX));
        r.register(new RockCategory(IGNEOUS_EXTRUSIVE, ToolMaterialsTFC.IG_IN));
    }

    @SubscribeEvent
    public static void onPreRegisterRock(TFCRegistries.RegisterPreBlock<Rock> event)
    {
        IForgeRegistry<Rock> r = event.getRegistry();

        Set<ResourceLocation> ROCK_LAYER_1 = ImmutableSet.of(SHALE, CLAYSTONE, ROCKSALT, LIMESTONE, CONGLOMERATE, DOLOMITE, CHERT, CHALK, RHYOLITE, BASALT, ANDESITE, DACITE, QUARTZITE, SLATE, PHYLLITE, SCHIST, GNEISS, MARBLE, GRANITE, DIORITE, GABBRO);
        Set<ResourceLocation> ROCK_LAYER_2 = ImmutableSet.of(RHYOLITE, BASALT, ANDESITE, DACITE, QUARTZITE, SLATE, PHYLLITE, SCHIST, GNEISS, MARBLE, GRANITE, DIORITE, GABBRO);
        Set<ResourceLocation> ROCK_LAYER_3 = ImmutableSet.of(RHYOLITE, BASALT, ANDESITE, DACITE, GRANITE, DIORITE, GABBRO);

        r.register(new Rock(GRANITE, O.IGNEOUS_INTRUSIVE, ROCK_LAYER_1.contains(GRANITE), ROCK_LAYER_2.contains(GRANITE), ROCK_LAYER_3.contains(GRANITE)));
        r.register(new Rock(DIORITE, O.IGNEOUS_INTRUSIVE, ROCK_LAYER_1.contains(DIORITE), ROCK_LAYER_2.contains(DIORITE), ROCK_LAYER_3.contains(DIORITE)));
        r.register(new Rock(GABBRO, O.IGNEOUS_INTRUSIVE, ROCK_LAYER_1.contains(GABBRO), ROCK_LAYER_2.contains(GABBRO), ROCK_LAYER_3.contains(GABBRO)));
        r.register(new Rock(SHALE, O.SEDIMENTARY, ROCK_LAYER_1.contains(SHALE), ROCK_LAYER_2.contains(SHALE), ROCK_LAYER_3.contains(SHALE)));
        r.register(new Rock(CLAYSTONE, O.SEDIMENTARY, ROCK_LAYER_1.contains(CLAYSTONE), ROCK_LAYER_2.contains(CLAYSTONE), ROCK_LAYER_3.contains(CLAYSTONE)));
        r.register(new Rock(ROCKSALT, O.SEDIMENTARY, ROCK_LAYER_1.contains(ROCKSALT), ROCK_LAYER_2.contains(ROCKSALT), ROCK_LAYER_3.contains(ROCKSALT)));
        r.register(new Rock(LIMESTONE, O.SEDIMENTARY, ROCK_LAYER_1.contains(LIMESTONE), ROCK_LAYER_2.contains(LIMESTONE), ROCK_LAYER_3.contains(LIMESTONE)));
        r.register(new Rock(CONGLOMERATE, O.SEDIMENTARY, ROCK_LAYER_1.contains(CONGLOMERATE), ROCK_LAYER_2.contains(CONGLOMERATE), ROCK_LAYER_3.contains(CONGLOMERATE)));
        r.register(new Rock(DOLOMITE, O.SEDIMENTARY, ROCK_LAYER_1.contains(DOLOMITE), ROCK_LAYER_2.contains(DOLOMITE), ROCK_LAYER_3.contains(DOLOMITE)));
        r.register(new Rock(CHERT, O.SEDIMENTARY, ROCK_LAYER_1.contains(CHERT), ROCK_LAYER_2.contains(CHERT), ROCK_LAYER_3.contains(CHERT)));
        r.register(new Rock(CHALK, O.SEDIMENTARY, ROCK_LAYER_1.contains(CHALK), ROCK_LAYER_2.contains(CHALK), ROCK_LAYER_3.contains(CHALK)));
        r.register(new Rock(RHYOLITE, O.IGNEOUS_EXTRUSIVE, ROCK_LAYER_1.contains(RHYOLITE), ROCK_LAYER_2.contains(RHYOLITE), ROCK_LAYER_3.contains(RHYOLITE)));
        r.register(new Rock(BASALT, O.IGNEOUS_EXTRUSIVE, ROCK_LAYER_1.contains(BASALT), ROCK_LAYER_2.contains(BASALT), ROCK_LAYER_3.contains(BASALT)));
        r.register(new Rock(ANDESITE, O.IGNEOUS_EXTRUSIVE, ROCK_LAYER_1.contains(ANDESITE), ROCK_LAYER_2.contains(ANDESITE), ROCK_LAYER_3.contains(ANDESITE)));
        r.register(new Rock(DACITE, O.IGNEOUS_EXTRUSIVE, ROCK_LAYER_1.contains(DACITE), ROCK_LAYER_2.contains(DACITE), ROCK_LAYER_3.contains(DACITE)));
        r.register(new Rock(QUARTZITE, O.METAMORPHIC, ROCK_LAYER_1.contains(QUARTZITE), ROCK_LAYER_2.contains(QUARTZITE), ROCK_LAYER_3.contains(QUARTZITE)));
        r.register(new Rock(SLATE, O.METAMORPHIC, ROCK_LAYER_1.contains(SLATE), ROCK_LAYER_2.contains(SLATE), ROCK_LAYER_3.contains(SLATE)));
        r.register(new Rock(PHYLLITE, O.METAMORPHIC, ROCK_LAYER_1.contains(PHYLLITE), ROCK_LAYER_2.contains(PHYLLITE), ROCK_LAYER_3.contains(PHYLLITE)));
        r.register(new Rock(SCHIST, O.METAMORPHIC, ROCK_LAYER_1.contains(SCHIST), ROCK_LAYER_2.contains(SCHIST), ROCK_LAYER_3.contains(SCHIST)));
        r.register(new Rock(GNEISS, O.METAMORPHIC, ROCK_LAYER_1.contains(GNEISS), ROCK_LAYER_2.contains(GNEISS), ROCK_LAYER_3.contains(GNEISS)));
        r.register(new Rock(MARBLE, O.METAMORPHIC, ROCK_LAYER_1.contains(MARBLE), ROCK_LAYER_2.contains(MARBLE), ROCK_LAYER_3.contains(MARBLE)));
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static final class O
    {
        public static final RockCategory SEDIMENTARY = null;
        public static final RockCategory METAMORPHIC = null;
        public static final RockCategory IGNEOUS_INTRUSIVE = null;
        public static final RockCategory IGNEOUS_EXTRUSIVE = null;

        public static final Rock GRANITE = null;
        public static final Rock DIORITE = null;
        public static final Rock GABBRO = null;
        public static final Rock SHALE = null;
        public static final Rock CLAYSTONE = null;
        public static final Rock ROCKSALT = null;
        public static final Rock LIMESTONE = null;
        public static final Rock CONGLOMERATE = null;
        public static final Rock DOLOMITE = null;
        public static final Rock CHERT = null;
        public static final Rock CHALK = null;
        public static final Rock RHYOLITE = null;
        public static final Rock BASALT = null;
        public static final Rock ANDESITE = null;
        public static final Rock DACITE = null;
        public static final Rock QUARTZITE = null;
        public static final Rock SLATE = null;
        public static final Rock PHYLLITE = null;
        public static final Rock SCHIST = null;
        public static final Rock GNEISS = null;
        public static final Rock MARBLE = null;
    }
}
