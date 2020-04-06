/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.world.classic.worldgen.trees.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultTrees
{
    /**
     * Default Tree ResourceLocations
     */
    public static final ResourceLocation ACACIA = new ResourceLocation(MOD_ID, "acacia");
    public static final ResourceLocation ASH = new ResourceLocation(MOD_ID, "ash");
    public static final ResourceLocation ASPEN = new ResourceLocation(MOD_ID, "aspen");
    public static final ResourceLocation BIRCH = new ResourceLocation(MOD_ID, "birch");
    public static final ResourceLocation BLACKWOOD = new ResourceLocation(MOD_ID, "blackwood");
    public static final ResourceLocation CHESTNUT = new ResourceLocation(MOD_ID, "chestnut");
    public static final ResourceLocation DOUGLAS_FIR = new ResourceLocation(MOD_ID, "douglas_fir");
    public static final ResourceLocation HICKORY = new ResourceLocation(MOD_ID, "hickory");
    public static final ResourceLocation MAPLE = new ResourceLocation(MOD_ID, "maple");
    public static final ResourceLocation OAK = new ResourceLocation(MOD_ID, "oak");
    public static final ResourceLocation PALM = new ResourceLocation(MOD_ID, "palm");
    public static final ResourceLocation PINE = new ResourceLocation(MOD_ID, "pine");
    public static final ResourceLocation ROSEWOOD = new ResourceLocation(MOD_ID, "rosewood");
    public static final ResourceLocation SEQUOIA = new ResourceLocation(MOD_ID, "sequoia");
    public static final ResourceLocation SPRUCE = new ResourceLocation(MOD_ID, "spruce");
    public static final ResourceLocation SYCAMORE = new ResourceLocation(MOD_ID, "sycamore");
    public static final ResourceLocation WHITE_CEDAR = new ResourceLocation(MOD_ID, "white_cedar");
    public static final ResourceLocation WILLOW = new ResourceLocation(MOD_ID, "willow");
    public static final ResourceLocation KAPOK = new ResourceLocation(MOD_ID, "kapok");

    /**
     * Simple ITreeGenerator instances.
     */
    public static final ITreeGenerator GEN_NORMAL = new TreeGenNormal(1, 3);
    public static final ITreeGenerator GEN_MEDIUM = new TreeGenNormal(2, 2);
    public static final ITreeGenerator GEN_TALL = new TreeGenNormal(3, 3);
    public static final ITreeGenerator GEN_CONIFER = new TreeGenVariants(false, 7);
    public static final ITreeGenerator GEN_TROPICAL = new TreeGenVariants(true, 7);
    public static final ITreeGenerator GEN_WILLOW = new TreeGenWillow();
    public static final ITreeGenerator GEN_ACACIA = new TreeGenAcacia();
    public static final ITreeGenerator GEN_KAPOK = new TreeGenKapok();
    public static final ITreeGenerator GEN_SEQUOIA = new TreeGenSequoia();
    public static final ITreeGenerator GEN_KAPOK_COMPOSITE = new TreeGenComposite().add(0.4f, GEN_TALL).add(0.6f, GEN_KAPOK);
    public static final ITreeGenerator GEN_BUSHES = new TreeGenBushes();

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistryEvent.RegisterPreBlock<Tree> event)
    {
        event.getRegistry().registerAll(
            new Tree.Builder(ACACIA, 30f, 210f, 19f, 31f, GEN_ACACIA).setHeight(12).setGrowthTime(11).setDensity(0.1f, 0.6f).setBurnInfo(650f, 1000).build(),
            new Tree.Builder(ASH, 60f, 140f, -6f, 12f, GEN_NORMAL).setBurnInfo(696f, 1250).build(),
            new Tree.Builder(ASPEN, 10f, 80f, -10f, 16f, GEN_MEDIUM).setRadius(1).setGrowthTime(8).setBurnInfo(611f, 1000).build(),
            new Tree.Builder(BIRCH, 20f, 180f, -15f, 7f, GEN_TALL).setRadius(1).setTannin().setBurnInfo(652f, 1750).build(),
            new Tree.Builder(BLACKWOOD, 0f, 120f, 4f, 33f, GEN_MEDIUM).setHeight(12).setGrowthTime(8).setBurnInfo(720f, 1750).build(),
            new Tree.Builder(CHESTNUT, 160f, 320f, 11f, 35f, GEN_NORMAL).setTannin().setBurnInfo(651f, 1500).build(),
            new Tree.Builder(DOUGLAS_FIR, 280f, 480f, -2f, 14f, GEN_TALL).setDominance(5.2f).setHeight(16).setBushes().setTannin().setDensity(0.25f, 2f).setBurnInfo(707f, 1500).build(),
            new Tree.Builder(HICKORY, 80f, 250f, 7f, 29f, GEN_TALL).setGrowthTime(10).setTannin().setBurnInfo(762f, 2000).build(),
            new Tree.Builder(KAPOK, 210f, 500f, 15f, 35f, GEN_KAPOK_COMPOSITE).setDominance(8.5f).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setBushes().setDensity(0.6f, 2f).setBurnInfo(645f, 1000).build(),
            new Tree.Builder(MAPLE, 140f, 360f, 3f, 20f, GEN_MEDIUM).setDominance(6.3f).setRadius(1).setTannin().setBurnInfo(745f, 2000).build(),
            new Tree.Builder(OAK, 180f, 430f, -8f, 12f, GEN_TALL).setHeight(16).setGrowthTime(10).setTannin().setBurnInfo(728f, 2250).build(),
            new Tree.Builder(PALM, 280f, 500f, 16f, 35f, GEN_TROPICAL).setDecayDist(6).setBurnInfo(730f, 1250).build(),
            new Tree.Builder(PINE, 60f, 250f, -15f, 7f, GEN_CONIFER).setRadius(1).setConifer().setDensity(0.1f, 0.8f).setBurnInfo(627f, 1250).build(),
            new Tree.Builder(ROSEWOOD, 10f, 190f, 8f, 18f, GEN_MEDIUM).setHeight(12).setGrowthTime(8).setBurnInfo(640f, 1500).build(),
            new Tree.Builder(SEQUOIA, 250f, 420f, -5f, 12f, GEN_SEQUOIA).setRadius(3).setHeight(24).setDecayDist(6).setGrowthTime(18).setConifer().setBushes().setTannin().setDensity(0.4f, 0.9f).setBurnInfo(612f, 1750).build(),
            new Tree.Builder(SPRUCE, 120f, 380f, -11f, 6f, GEN_CONIFER).setRadius(1).setConifer().setDensity(0.1f, 0.8f).setBurnInfo(608f, 1500).build(),
            new Tree.Builder(SYCAMORE, 120f, 290f, 17f, 33f, GEN_MEDIUM).setGrowthTime(8).setBushes().setDensity(0.25f, 2f).setBurnInfo(653f, 1750).build(),
            new Tree.Builder(WHITE_CEDAR, 10f, 240f, -8f, 17f, GEN_TALL).setHeight(16).setBurnInfo(625f, 1500).build(),
            new Tree.Builder(WILLOW, 230f, 400f, 15f, 32f, GEN_WILLOW).setRadius(1).setGrowthTime(11).setBushes().setDensity(0.7f, 2f).setBurnInfo(603f, 1000).build()
        );
    }
}
