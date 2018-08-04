/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.*;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultTrees
{

    /**
     * Simple ITreeGenerator instances.
     */
    private static final ITreeGenerator GEN_NORMAL = new TreeGenNormal(1, 3);
    private static final ITreeGenerator GEN_TALL = new TreeGenNormal(3, 3);
    private static final ITreeGenerator GEN_LARGE = new TreeGenNormal(0, 2);
    private static final ITreeGenerator GEN_CONIFER = new TreeGenVariants(false, 7);
    private static final ITreeGenerator GEN_TROPICAL = new TreeGenVariants(true, 7);
    private static final ITreeGenerator GEN_WILLOW = new TreeGenWillow();
    private static final ITreeGenerator GEN_ACACIA = new TreeGenAcacia();
    private static final ITreeGenerator GEN_KAPOK = new TreeGenKapok();
    /**
     * Composite ITreeGenerator (takes a weighted selection of multiple ITreeGenerators. Can be layered)
     */
    public static final ITreeGenerator GEN_KAPOK_COMPOSITE = new TreeGenComposite().add(0.4f, GEN_TALL).add(0.6f, GEN_KAPOK);
    private static final ITreeGenerator GEN_SEQUOIA = new TreeGenSequoia();

    /**
     * Default tree ResourceLocations
     */
    private static final ResourceLocation ACACIA = new ResourceLocation(MOD_ID, "acacia");
    private static final ResourceLocation ASH = new ResourceLocation(MOD_ID, "ash");
    private static final ResourceLocation ASPEN = new ResourceLocation(MOD_ID, "aspen");
    private static final ResourceLocation BIRCH = new ResourceLocation(MOD_ID, "birch");
    private static final ResourceLocation BLACKWOOD = new ResourceLocation(MOD_ID, "blackwood");
    private static final ResourceLocation CHESTNUT = new ResourceLocation(MOD_ID, "chestnut");
    private static final ResourceLocation DOUGLAS_FIR = new ResourceLocation(MOD_ID, "douglas_fir");
    private static final ResourceLocation HICKORY = new ResourceLocation(MOD_ID, "hickory");
    private static final ResourceLocation MAPLE = new ResourceLocation(MOD_ID, "maple");
    private static final ResourceLocation OAK = new ResourceLocation(MOD_ID, "oak");
    private static final ResourceLocation PALM = new ResourceLocation(MOD_ID, "palm");
    private static final ResourceLocation PINE = new ResourceLocation(MOD_ID, "pine");
    private static final ResourceLocation ROSEWOOD = new ResourceLocation(MOD_ID, "rosewood");
    private static final ResourceLocation SEQUOIA = new ResourceLocation(MOD_ID, "sequoia");
    private static final ResourceLocation SPRUCE = new ResourceLocation(MOD_ID, "spruce");
    private static final ResourceLocation SYCAMORE = new ResourceLocation(MOD_ID, "sycamore");
    private static final ResourceLocation WHITE_CEDAR = new ResourceLocation(MOD_ID, "white_cedar");
    private static final ResourceLocation WILLOW = new ResourceLocation(MOD_ID, "willow");
    private static final ResourceLocation KAPOK = new ResourceLocation(MOD_ID, "kapok");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<Tree> event)
    {
        IForgeRegistry<Tree> r = event.getRegistry();
        r.registerAll(
            new Tree.Builder(ACACIA, 30f, 210f, 19f, 31f, 8, GEN_ACACIA).setRadius(3).setGrowthTime(11).setDensity(0.1f, 0.6f).build(),
            new Tree.Builder(ASH, 60f, 140f, -6f, 12f, 5, GEN_TALL).build(),
            new Tree.Builder(ASPEN, 10f, 80f, -10f, 12f, 9, GEN_TALL).setGrowthTime(8).build(),
            new Tree.Builder(BIRCH, 20f, 180f, -15f, 7f, 5, GEN_TALL).build(),
            new Tree.Builder(BLACKWOOD, 0f, 120f, 4f, 28f, 3, GEN_LARGE).setHeight(10).setGrowthTime(8).build(),
            new Tree.Builder(CHESTNUT, 160f, 320f, 11f, 24f, 9, GEN_NORMAL).setRadius(1).build(),
            new Tree.Builder(DOUGLAS_FIR, 280f, 480f, -2f, 14f, 5, GEN_TALL).setHeight(14).setDensity(0.25f, 2f).build(),
            new Tree.Builder(HICKORY, 80f, 250f, 7f, 29f, 7, GEN_TALL).setGrowthTime(10).build(),
            new Tree.Builder(MAPLE, 140f, 360f, 3f, 20f, 6, GEN_NORMAL).setRadius(1).build(),
            new Tree.Builder(OAK, 180f, 430f, -8f, 12f, 5, GEN_TALL).setHeight(14).setGrowthTime(10).build(),
            new Tree.Builder(PALM, 280f, 500f, 16f, 35f, 7, GEN_TROPICAL).build(),
            new Tree.Builder(PINE, 59f, 250f, -15f, 5f, 5, GEN_CONIFER).setConifer().setDensity(0.1f, 0.8f).build(),
            new Tree.Builder(ROSEWOOD, 10f, 190f, 8f, 18f, 8, GEN_LARGE).setHeight(10).setGrowthTime(8).build(),
            new Tree.Builder(SEQUOIA, 250f, 420f, -5f, 8f, 3, GEN_SEQUOIA).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setConifer().setDensity(0.4f, 0.9f).build(),
            new Tree.Builder(SPRUCE, 120f, 380f, -11f, 6f, 6, GEN_CONIFER).setConifer().setDensity(0.1f, 0.8f).build(),
            new Tree.Builder(SYCAMORE, 120f, 290f, 17f, 33f, 2, GEN_NORMAL).setRadius(1).setGrowthTime(8).setDensity(0.25f, 2f).build(),
            new Tree.Builder(WHITE_CEDAR, 10f, 240f, -8f, 17f, 8, GEN_LARGE).setHeight(10).build(),
            new Tree.Builder(WILLOW, 230f, 500f, 10f, 34f, 3, GEN_WILLOW).setGrowthTime(11).build(),
            new Tree.Builder(KAPOK, 180f, 400f, 15f, 35f, 10, GEN_KAPOK_COMPOSITE).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setDensity(0.55f, 2f).build()
        );
    }
}
