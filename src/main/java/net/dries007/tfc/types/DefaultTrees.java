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
import net.dries007.tfc.api.types.TFCRegistries;
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
    private static final ITreeGenerator GEN_MEDIUM = new TreeGenNormal(2, 2);
    private static final ITreeGenerator GEN_TALL = new TreeGenNormal(3, 3);
    private static final ITreeGenerator GEN_CONIFER = new TreeGenVariants(false, 7);
    private static final ITreeGenerator GEN_TROPICAL = new TreeGenVariants(true, 7);
    private static final ITreeGenerator GEN_WILLOW = new TreeGenWillow();
    private static final ITreeGenerator GEN_ACACIA = new TreeGenAcacia();
    private static final ITreeGenerator GEN_KAPOK = new TreeGenKapok();
    private static final ITreeGenerator GEN_SEQUOIA = new TreeGenSequoia();
    /**
     * Composite ITreeGenerator (takes a weighted selection of multiple ITreeGenerators. Can be layered)
     */
    private static final ITreeGenerator GEN_KAPOK_COMPOSITE = new TreeGenComposite().add(0.4f, GEN_TALL).add(0.6f, GEN_KAPOK);

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
            new Tree.Builder(ACACIA, 30f, 210f, 19f, 31f, GEN_ACACIA).setRadius(3).setGrowthTime(11).setDensity(0.1f, 0.6f).build(),
            new Tree.Builder(ASH, 60f, 140f, -6f, 12f, GEN_NORMAL).build(),
            new Tree.Builder(ASPEN, 10f, 80f, -10f, 16f, GEN_MEDIUM).setGrowthTime(8).build(),
            new Tree.Builder(BIRCH, 20f, 180f, -15f, 7f, GEN_TALL).build(),
            new Tree.Builder(BLACKWOOD, 0f, 120f, 4f, 33f, GEN_MEDIUM).setHeight(10).setGrowthTime(8).build(),
            new Tree.Builder(CHESTNUT, 160f, 320f, 11f, 35f, GEN_NORMAL).setRadius(1).build(),
            new Tree.Builder(DOUGLAS_FIR, 280f, 480f, -2f, 14f, GEN_TALL).setDominance(5.2f).setHeight(14).setBushes().setDensity(0.25f, 2f).build(),
            new Tree.Builder(HICKORY, 80f, 250f, 7f, 29f, GEN_TALL).setGrowthTime(10).build(),
            new Tree.Builder(MAPLE, 140f, 360f, 3f, 20f, GEN_MEDIUM).setDominance(6.3f).setRadius(1).build(),
            new Tree.Builder(OAK, 180f, 430f, -8f, 12f, GEN_TALL).setHeight(14).setGrowthTime(10).build(),
            new Tree.Builder(PALM, 280f, 500f, 16f, 35f, GEN_TROPICAL).setDecayDist(6).build(),
            new Tree.Builder(PINE, 59f, 250f, -15f, 7f, GEN_CONIFER).setConifer().setDensity(0.1f, 0.8f).build(),
            new Tree.Builder(ROSEWOOD, 10f, 190f, 8f, 18f, GEN_MEDIUM).setHeight(10).setGrowthTime(8).build(),
            new Tree.Builder(SEQUOIA, 250f, 420f, -5f, 12f, GEN_SEQUOIA).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setConifer().setBushes().setDensity(0.4f, 0.9f).build(),
            new Tree.Builder(SPRUCE, 120f, 380f, -11f, 6f, GEN_CONIFER).setConifer().setDensity(0.1f, 0.8f).build(),
            new Tree.Builder(SYCAMORE, 120f, 290f, 17f, 33f, GEN_MEDIUM).setRadius(1).setGrowthTime(8).setBushes().setDensity(0.25f, 2f).build(),
            new Tree.Builder(WHITE_CEDAR, 10f, 240f, -8f, 17f, GEN_TALL).setHeight(10).build(),
            new Tree.Builder(WILLOW, 230f, 400f, 15f, 32f, GEN_WILLOW).setGrowthTime(11).setBushes().setDensity(0.7f, 2f).build(),
            new Tree.Builder(KAPOK, 210f, 500f, 15f, 35f, GEN_KAPOK_COMPOSITE).setDominance(8.5f).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setBushes().setDensity(0.6f, 2f).build()
        );
    }
}
