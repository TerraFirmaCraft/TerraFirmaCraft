/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

/**
 * A collection of fast noise utility functions
 */
public final class NoiseUtil
{
    static final Vec2[] CELL_2D = new Vec2[] {new Vec2(-0.4313539279f, 0.1281943404f), new Vec2(-0.1733316799f, 0.415278375f), new Vec2(-0.2821957395f, -0.3505218461f), new Vec2(-0.2806473808f, 0.3517627718f), new Vec2(0.3125508975f, -0.3237467165f), new Vec2(0.3383018443f, -0.2967353402f), new Vec2(-0.4393982022f, -0.09710417025f), new Vec2(-0.4460443703f, -0.05953502905f), new Vec2(-0.302223039f, 0.3334085102f), new Vec2(-0.212681052f, -0.3965687458f), new Vec2(-0.2991156529f, 0.3361990872f), new Vec2(0.2293323691f, 0.3871778202f), new Vec2(0.4475439151f, -0.04695150755f), new Vec2(0.1777518f, 0.41340573f), new Vec2(0.1688522499f, -0.4171197882f), new Vec2(-0.0976597166f, 0.4392750616f), new Vec2(0.08450188373f, 0.4419948321f), new Vec2(-0.4098760448f, -0.1857461384f), new Vec2(0.3476585782f, -0.2857157906f), new Vec2(-0.3350670039f, -0.30038326f), new Vec2(0.2298190031f, -0.3868891648f), new Vec2(-0.01069924099f, 0.449872789f), new Vec2(-0.4460141246f, -0.05976119672f), new Vec2(0.3650293864f, 0.2631606867f), new Vec2(-0.349479423f, 0.2834856838f), new Vec2(-0.4122720642f, 0.1803655873f), new Vec2(-0.267327811f, 0.3619887311f), new Vec2(0.322124041f, -0.3142230135f), new Vec2(0.2880445931f, -0.3457315612f), new Vec2(0.3892170926f, -0.2258540565f), new Vec2(0.4492085018f, -0.02667811596f), new Vec2(-0.4497724772f, 0.01430799601f), new Vec2(0.1278175387f, -0.4314657307f), new Vec2(-0.03572100503f, 0.4485799926f), new Vec2(-0.4297407068f, -0.1335025276f), new Vec2(-0.3217817723f, 0.3145735065f), new Vec2(-0.3057158873f, 0.3302087162f), new Vec2(-0.414503978f, 0.1751754899f), new Vec2(-0.3738139881f, 0.2505256519f), new Vec2(0.2236891408f, -0.3904653228f), new Vec2(0.002967775577f, -0.4499902136f), new Vec2(0.1747128327f, -0.4146991995f), new Vec2(-0.4423772489f, -0.08247647938f), new Vec2(-0.2763960987f, -0.355112935f), new Vec2(-0.4019385906f, -0.2023496216f), new Vec2(0.3871414161f, -0.2293938184f), new Vec2(-0.430008727f, 0.1326367019f), new Vec2(-0.03037574274f, -0.4489736231f), new Vec2(-0.3486181573f, 0.2845441624f), new Vec2(0.04553517144f, -0.4476902368f), new Vec2(-0.0375802926f, 0.4484280562f), new Vec2(0.3266408905f, 0.3095250049f), new Vec2(0.06540017593f, -0.4452222108f), new Vec2(0.03409025829f, 0.448706869f), new Vec2(-0.4449193635f, 0.06742966669f), new Vec2(-0.4255936157f, -0.1461850686f), new Vec2(0.449917292f, 0.008627302568f), new Vec2(0.05242606404f, 0.4469356864f), new Vec2(-0.4495305179f, -0.02055026661f), new Vec2(-0.1204775703f, 0.4335725488f), new Vec2(-0.341986385f, -0.2924813028f), new Vec2(0.3865320182f, 0.2304191809f), new Vec2(0.04506097811f, -0.447738214f), new Vec2(-0.06283465979f, 0.4455915232f), new Vec2(0.3932600341f, -0.2187385324f), new Vec2(0.4472261803f, -0.04988730975f), new Vec2(0.3753571011f, -0.2482076684f), new Vec2(-0.273662295f, 0.357223947f), new Vec2(0.1700461538f, 0.4166344988f), new Vec2(0.4102692229f, 0.1848760794f), new Vec2(0.323227187f, -0.3130881435f), new Vec2(-0.2882310238f, -0.3455761521f), new Vec2(0.2050972664f, 0.4005435199f), new Vec2(0.4414085979f, -0.08751256895f), new Vec2(-0.1684700334f, 0.4172743077f), new Vec2(-0.003978032396f, 0.4499824166f), new Vec2(-0.2055133639f, 0.4003301853f), new Vec2(-0.006095674897f, -0.4499587123f), new Vec2(-0.1196228124f, -0.4338091548f), new Vec2(0.3901528491f, -0.2242337048f), new Vec2(0.01723531752f, 0.4496698165f), new Vec2(-0.3015070339f, 0.3340561458f), new Vec2(-0.01514262423f, -0.4497451511f), new Vec2(-0.4142574071f, -0.1757577897f), new Vec2(-0.1916377265f, -0.4071547394f), new Vec2(0.3749248747f, 0.2488600778f), new Vec2(-0.2237774255f, 0.3904147331f), new Vec2(-0.4166343106f, -0.1700466149f), new Vec2(0.3619171625f, 0.267424695f), new Vec2(0.1891126846f, -0.4083336779f), new Vec2(-0.3127425077f, 0.323561623f), new Vec2(-0.3281807787f, 0.307891826f), new Vec2(-0.2294806661f, 0.3870899429f), new Vec2(-0.3445266136f, 0.2894847362f), new Vec2(-0.4167095422f, -0.1698621719f), new Vec2(-0.257890321f, -0.3687717212f), new Vec2(-0.3612037825f, 0.2683874578f), new Vec2(0.2267996491f, 0.3886668486f), new Vec2(0.207157062f, 0.3994821043f), new Vec2(0.08355176718f, -0.4421754202f), new Vec2(-0.4312233307f, 0.1286329626f), new Vec2(0.3257055497f, 0.3105090899f), new Vec2(0.177701095f, -0.4134275279f), new Vec2(-0.445182522f, 0.06566979625f), new Vec2(0.3955143435f, 0.2146355146f), new Vec2(-0.4264613988f, 0.1436338239f), new Vec2(-0.3793799665f, -0.2420141339f), new Vec2(0.04617599081f, -0.4476245948f), new Vec2(-0.371405428f, -0.2540826796f), new Vec2(0.2563570295f, -0.3698392535f), new Vec2(0.03476646309f, 0.4486549822f), new Vec2(-0.3065454405f, 0.3294387544f), new Vec2(-0.2256979823f, 0.3893076172f), new Vec2(0.4116448463f, -0.1817925206f), new Vec2(-0.2907745828f, -0.3434387019f), new Vec2(0.2842278468f, -0.348876097f), new Vec2(0.3114589359f, -0.3247973695f), new Vec2(0.4464155859f, -0.0566844308f), new Vec2(-0.3037334033f, -0.3320331606f), new Vec2(0.4079607166f, 0.1899159123f), new Vec2(-0.3486948919f, -0.2844501228f), new Vec2(0.3264821436f, 0.3096924441f), new Vec2(0.3211142406f, 0.3152548881f), new Vec2(0.01183382662f, 0.4498443737f), new Vec2(0.4333844092f, 0.1211526057f), new Vec2(0.3118668416f, 0.324405723f), new Vec2(-0.272753471f, 0.3579183483f), new Vec2(-0.422228622f, -0.1556373694f), new Vec2(-0.1009700099f, -0.4385260051f), new Vec2(-0.2741171231f, -0.3568750521f), new Vec2(-0.1465125133f, 0.4254810025f), new Vec2(0.2302279044f, -0.3866459777f), new Vec2(-0.3699435608f, 0.2562064828f), new Vec2(0.105700352f, -0.4374099171f), new Vec2(-0.2646713633f, 0.3639355292f), new Vec2(0.3521828122f, 0.2801200935f), new Vec2(-0.1864187807f, -0.4095705534f), new Vec2(0.1994492955f, -0.4033856449f), new Vec2(0.3937065066f, 0.2179339044f), new Vec2(-0.3226158377f, 0.3137180602f), new Vec2(0.3796235338f, 0.2416318948f), new Vec2(0.1482921929f, 0.4248640083f), new Vec2(-0.407400394f, 0.1911149365f), new Vec2(0.4212853031f, 0.1581729856f), new Vec2(-0.2621297173f, 0.3657704353f), new Vec2(-0.2536986953f, -0.3716678248f), new Vec2(-0.2100236383f, 0.3979825013f), new Vec2(0.3624152444f, 0.2667493029f), new Vec2(-0.3645038479f, -0.2638881295f), new Vec2(0.2318486784f, 0.3856762766f), new Vec2(-0.3260457004f, 0.3101519002f), new Vec2(-0.2130045332f, -0.3963950918f), new Vec2(0.3814998766f, -0.2386584257f), new Vec2(-0.342977305f, 0.2913186713f), new Vec2(-0.4355865605f, 0.1129794154f), new Vec2(-0.2104679605f, 0.3977477059f), new Vec2(0.3348364681f, -0.3006402163f), new Vec2(0.3430468811f, 0.2912367377f), new Vec2(-0.2291836801f, -0.3872658529f), new Vec2(0.2547707298f, -0.3709337882f), new Vec2(0.4236174945f, -0.151816397f), new Vec2(-0.15387742f, 0.4228731957f), new Vec2(-0.4407449312f, 0.09079595574f), new Vec2(-0.06805276192f, -0.444824484f), new Vec2(0.4453517192f, -0.06451237284f), new Vec2(0.2562464609f, -0.3699158705f), new Vec2(0.3278198355f, -0.3082761026f), new Vec2(-0.4122774207f, -0.1803533432f), new Vec2(0.3354090914f, -0.3000012356f), new Vec2(0.446632869f, -0.05494615882f), new Vec2(-0.1608953296f, 0.4202531296f), new Vec2(-0.09463954939f, 0.4399356268f), new Vec2(-0.02637688324f, -0.4492262904f), new Vec2(0.447102804f, -0.05098119915f), new Vec2(-0.4365670908f, 0.1091291678f), new Vec2(-0.3959858651f, 0.2137643437f), new Vec2(-0.4240048207f, -0.1507312575f), new Vec2(-0.3882794568f, 0.2274622243f), new Vec2(-0.4283652566f, -0.1378521198f), new Vec2(0.3303888091f, 0.305521251f), new Vec2(0.3321434919f, -0.3036127481f), new Vec2(-0.413021046f, -0.1786438231f), new Vec2(0.08403060337f, -0.4420846725f), new Vec2(-0.3822882919f, 0.2373934748f), new Vec2(-0.3712395594f, -0.2543249683f), new Vec2(0.4472363971f, -0.04979563372f), new Vec2(-0.4466591209f, 0.05473234629f), new Vec2(0.0486272539f, -0.4473649407f), new Vec2(-0.4203101295f, -0.1607463688f), new Vec2(0.2205360833f, 0.39225481f), new Vec2(-0.3624900666f, 0.2666476169f), new Vec2(-0.4036086833f, -0.1989975647f), new Vec2(0.2152727807f, 0.3951678503f), new Vec2(-0.4359392962f, -0.1116106179f), new Vec2(0.4178354266f, 0.1670735057f), new Vec2(0.2007630161f, 0.4027334247f), new Vec2(-0.07278067175f, -0.4440754146f), new Vec2(0.3644748615f, -0.2639281632f), new Vec2(-0.4317451775f, 0.126870413f), new Vec2(-0.297436456f, 0.3376855855f), new Vec2(-0.2998672222f, 0.3355289094f), new Vec2(-0.2673674124f, 0.3619594822f), new Vec2(0.2808423357f, 0.3516071423f), new Vec2(0.3498946567f, 0.2829730186f), new Vec2(-0.2229685561f, 0.390877248f), new Vec2(0.3305823267f, 0.3053118493f), new Vec2(-0.2436681211f, -0.3783197679f), new Vec2(-0.03402776529f, 0.4487116125f), new Vec2(-0.319358823f, 0.3170330301f), new Vec2(0.4454633477f, -0.06373700535f), new Vec2(0.4483504221f, 0.03849544189f), new Vec2(-0.4427358436f, -0.08052932871f), new Vec2(0.05452298565f, 0.4466847255f), new Vec2(-0.2812560807f, 0.3512762688f), new Vec2(0.1266696921f, 0.4318041097f), new Vec2(-0.3735981243f, 0.2508474468f), new Vec2(0.2959708351f, -0.3389708908f), new Vec2(-0.3714377181f, 0.254035473f), new Vec2(-0.404467102f, -0.1972469604f), new Vec2(0.1636165687f, -0.419201167f), new Vec2(0.3289185495f, -0.3071035458f), new Vec2(-0.2494824991f, -0.3745109914f), new Vec2(0.03283133272f, 0.4488007393f), new Vec2(-0.166306057f, -0.4181414777f), new Vec2(-0.106833179f, 0.4371346153f), new Vec2(0.06440260376f, -0.4453676062f), new Vec2(-0.4483230967f, 0.03881238203f), new Vec2(-0.421377757f, -0.1579265206f), new Vec2(0.05097920662f, -0.4471030312f), new Vec2(0.2050584153f, -0.4005634111f), new Vec2(0.4178098529f, -0.167137449f), new Vec2(-0.3565189504f, -0.2745801121f), new Vec2(0.4478398129f, 0.04403977727f), new Vec2(-0.3399999602f, -0.2947881053f), new Vec2(0.3767121994f, 0.2461461331f), new Vec2(-0.3138934434f, 0.3224451987f), new Vec2(-0.1462001792f, -0.4255884251f), new Vec2(0.3970290489f, -0.2118205239f), new Vec2(0.4459149305f, -0.06049689889f), new Vec2(-0.4104889426f, -0.1843877112f), new Vec2(0.1475103971f, -0.4251360756f), new Vec2(0.09258030352f, 0.4403735771f), new Vec2(-0.1589664637f, -0.4209865359f), new Vec2(0.2482445008f, 0.3753327428f), new Vec2(0.4383624232f, -0.1016778537f), new Vec2(0.06242802956f, 0.4456486745f), new Vec2(0.2846591015f, -0.3485243118f), new Vec2(-0.344202744f, -0.2898697484f), new Vec2(0.1198188883f, -0.4337550392f), new Vec2(-0.243590703f, 0.3783696201f), new Vec2(0.2958191174f, -0.3391033025f), new Vec2(-0.1164007991f, 0.4346847754f), new Vec2(0.1274037151f, -0.4315881062f), new Vec2(0.368047306f, 0.2589231171f), new Vec2(0.2451436949f, 0.3773652989f), new Vec2(-0.4314509715f, 0.12786735f)};
    static final Vec3[] CELL_3D = new Vec3[] {new Vec3(0.1453787434f, -0.4149781685f, -0.0956981749f), new Vec3(-0.01242829687f, -0.1457918398f, -0.4255470325f), new Vec3(0.2877979582f, -0.02606483451f, -0.3449535616f), new Vec3(-0.07732986802f, 0.2377094325f, 0.3741848704f), new Vec3(0.1107205875f, -0.3552302079f, -0.2530858567f), new Vec3(0.2755209141f, 0.2640521179f, -0.238463215f), new Vec3(0.294168941f, 0.1526064594f, 0.3044271714f), new Vec3(0.4000921098f, -0.2034056362f, 0.03244149937f), new Vec3(-0.1697304074f, 0.3970864695f, -0.1265461359f), new Vec3(-0.1483224484f, -0.3859694688f, 0.1775613147f), new Vec3(0.2623596946f, -0.2354852944f, 0.2796677792f), new Vec3(-0.2709003183f, 0.3505271138f, -0.07901746678f), new Vec3(-0.03516550699f, 0.3885234328f, 0.2243054374f), new Vec3(-0.1267712655f, 0.1920044036f, 0.3867342179f), new Vec3(0.02952021915f, 0.4409685861f, 0.08470692262f), new Vec3(-0.2806854217f, -0.266996757f, 0.2289725438f), new Vec3(-0.171159547f, 0.2141185563f, 0.3568720405f), new Vec3(0.2113227183f, 0.3902405947f, -0.07453178509f), new Vec3(-0.1024352839f, 0.2128044156f, -0.3830421561f), new Vec3(-0.3304249877f, -0.1566986703f, 0.2622305365f), new Vec3(0.2091111325f, 0.3133278055f, -0.2461670583f), new Vec3(0.344678154f, -0.1944240454f, -0.2142341261f), new Vec3(0.1984478035f, -0.3214342325f, -0.2445373252f), new Vec3(-0.2929008603f, 0.2262915116f, 0.2559320961f), new Vec3(-0.1617332831f, 0.006314769776f, -0.4198838754f), new Vec3(-0.3582060271f, -0.148303178f, -0.2284613961f), new Vec3(-0.1852067326f, -0.3454119342f, -0.2211087107f), new Vec3(0.3046301062f, 0.1026310383f, 0.314908508f), new Vec3(-0.03816768434f, -0.2551766358f, -0.3686842991f), new Vec3(-0.4084952196f, 0.1805950793f, 0.05492788837f), new Vec3(-0.02687443361f, -0.2749741471f, 0.3551999201f), new Vec3(-0.03801098351f, 0.3277859044f, 0.3059600725f), new Vec3(0.2371120802f, 0.2900386767f, -0.2493099024f), new Vec3(0.4447660503f, 0.03946930643f, 0.05590469027f), new Vec3(0.01985147278f, -0.01503183293f, -0.4493105419f), new Vec3(0.4274339143f, 0.03345994256f, -0.1366772882f), new Vec3(-0.2072988631f, 0.2871414597f, -0.2776273824f), new Vec3(-0.3791240978f, 0.1281177671f, 0.2057929936f), new Vec3(-0.2098721267f, -0.1007087278f, -0.3851122467f), new Vec3(0.01582798878f, 0.4263894424f, 0.1429738373f), new Vec3(-0.1888129464f, -0.3160996813f, -0.2587096108f), new Vec3(0.1612988974f, -0.1974805082f, -0.3707885038f), new Vec3(-0.08974491322f, 0.229148752f, -0.3767448739f), new Vec3(0.07041229526f, 0.4150230285f, -0.1590534329f), new Vec3(-0.1082925611f, -0.1586061639f, 0.4069604477f), new Vec3(0.2474100658f, -0.3309414609f, 0.1782302128f), new Vec3(-0.1068836661f, -0.2701644537f, -0.3436379634f), new Vec3(0.2396452163f, 0.06803600538f, -0.3747549496f), new Vec3(-0.3063886072f, 0.2597428179f, 0.2028785103f), new Vec3(0.1593342891f, -0.3114350249f, -0.2830561951f), new Vec3(0.2709690528f, 0.1412648683f, -0.3303331794f), new Vec3(-0.1519780427f, 0.3623355133f, 0.2193527988f), new Vec3(0.1699773681f, 0.3456012883f, 0.2327390037f), new Vec3(-0.1986155616f, 0.3836276443f, -0.1260225743f), new Vec3(-0.1887482106f, -0.2050154888f, -0.353330953f), new Vec3(0.2659103394f, 0.3015631259f, -0.2021172246f), new Vec3(-0.08838976154f, -0.4288819642f, -0.1036702021f), new Vec3(-0.04201869311f, 0.3099592485f, 0.3235115047f), new Vec3(-0.3230334656f, 0.201549922f, -0.2398478873f), new Vec3(0.2612720941f, 0.2759854499f, -0.2409749453f), new Vec3(0.385713046f, 0.2193460345f, 0.07491837764f), new Vec3(0.07654967953f, 0.3721732183f, 0.241095919f), new Vec3(0.4317038818f, -0.02577753072f, 0.1243675091f), new Vec3(-0.2890436293f, -0.3418179959f, -0.04598084447f), new Vec3(-0.2201947582f, 0.383023377f, -0.08548310451f), new Vec3(0.4161322773f, -0.1669634289f, -0.03817251927f), new Vec3(0.2204718095f, 0.02654238946f, -0.391391981f), new Vec3(-0.1040307469f, 0.3890079625f, -0.2008741118f), new Vec3(-0.1432122615f, 0.371614387f, -0.2095065525f), new Vec3(0.3978380468f, -0.06206669342f, 0.2009293758f), new Vec3(-0.2599274663f, 0.2616724959f, -0.2578084893f), new Vec3(0.4032618332f, -0.1124593585f, 0.1650235939f), new Vec3(-0.08953470255f, -0.3048244735f, 0.3186935478f), new Vec3(0.118937202f, -0.2875221847f, 0.325092195f), new Vec3(0.02167047076f, -0.03284630549f, -0.4482761547f), new Vec3(-0.3411343612f, 0.2500031105f, 0.1537068389f), new Vec3(0.3162964612f, 0.3082064153f, -0.08640228117f), new Vec3(0.2355138889f, -0.3439334267f, -0.1695376245f), new Vec3(-0.02874541518f, -0.3955933019f, 0.2125550295f), new Vec3(-0.2461455173f, 0.02020282325f, -0.3761704803f), new Vec3(0.04208029445f, -0.4470439576f, 0.02968078139f), new Vec3(0.2727458746f, 0.2288471896f, -0.2752065618f), new Vec3(-0.1347522818f, -0.02720848277f, -0.4284874806f), new Vec3(0.3829624424f, 0.1231931484f, -0.2016512234f), new Vec3(-0.3547613644f, 0.1271702173f, 0.2459107769f), new Vec3(0.2305790207f, 0.3063895591f, 0.2354968222f), new Vec3(-0.08323845599f, -0.1922245118f, 0.3982726409f), new Vec3(0.2993663085f, -0.2619918095f, -0.2103333191f), new Vec3(-0.2154865723f, 0.2706747713f, 0.287751117f), new Vec3(0.01683355354f, -0.2680655787f, -0.3610505186f), new Vec3(0.05240429123f, 0.4335128183f, -0.1087217856f), new Vec3(0.00940104872f, -0.4472890582f, 0.04841609928f), new Vec3(0.3465688735f, 0.01141914583f, -0.2868093776f), new Vec3(-0.3706867948f, -0.2551104378f, 0.003156692623f), new Vec3(0.2741169781f, 0.2139972417f, -0.2855959784f), new Vec3(0.06413433865f, 0.1708718512f, 0.4113266307f), new Vec3(-0.388187972f, -0.03973280434f, -0.2241236325f), new Vec3(0.06419469312f, -0.2803682491f, 0.3460819069f), new Vec3(-0.1986120739f, -0.3391173584f, 0.2192091725f), new Vec3(-0.203203009f, -0.3871641506f, 0.1063600375f), new Vec3(-0.1389736354f, -0.2775901578f, -0.3257760473f), new Vec3(-0.06555641638f, 0.342253257f, -0.2847192729f), new Vec3(-0.2529246486f, -0.2904227915f, 0.2327739768f), new Vec3(0.1444476522f, 0.1069184044f, 0.4125570634f), new Vec3(-0.3643780054f, -0.2447099973f, -0.09922543227f), new Vec3(0.4286142488f, -0.1358496089f, -0.01829506817f), new Vec3(0.165872923f, -0.3136808464f, -0.2767498872f), new Vec3(0.2219610524f, -0.3658139958f, 0.1393320198f), new Vec3(0.04322940318f, -0.3832730794f, 0.2318037215f), new Vec3(-0.08481269795f, -0.4404869674f, -0.03574965489f), new Vec3(0.1822082075f, -0.3953259299f, 0.1140946023f), new Vec3(-0.3269323334f, 0.3036542563f, 0.05838957105f), new Vec3(-0.4080485344f, 0.04227858267f, -0.184956522f), new Vec3(0.2676025294f, -0.01299671652f, 0.36155217f), new Vec3(0.3024892441f, -0.1009990293f, -0.3174892964f), new Vec3(0.1448494052f, 0.425921681f, -0.0104580805f), new Vec3(0.4198402157f, 0.08062320474f, 0.1404780841f), new Vec3(-0.3008872161f, -0.333040905f, -0.03241355801f), new Vec3(0.3639310428f, -0.1291284382f, -0.2310412139f), new Vec3(0.3295806598f, 0.0184175994f, -0.3058388149f), new Vec3(0.2776259487f, -0.2974929052f, -0.1921504723f), new Vec3(0.4149000507f, -0.144793182f, -0.09691688386f), new Vec3(0.145016715f, -0.0398992945f, 0.4241205002f), new Vec3(0.09299023471f, -0.299732164f, -0.3225111565f), new Vec3(0.1028907093f, -0.361266869f, 0.247789732f), new Vec3(0.2683057049f, -0.07076041213f, -0.3542668666f), new Vec3(-0.4227307273f, -0.07933161816f, -0.1323073187f), new Vec3(-0.1781224702f, 0.1806857196f, -0.3716517945f), new Vec3(0.4390788626f, -0.02841848598f, -0.09435116353f), new Vec3(0.2972583585f, 0.2382799621f, -0.2394997452f), new Vec3(-0.1707002821f, 0.2215845691f, 0.3525077196f), new Vec3(0.3806686614f, 0.1471852559f, -0.1895464869f), new Vec3(-0.1751445661f, -0.274887877f, 0.3102596268f), new Vec3(-0.2227237566f, -0.2316778837f, 0.3149912482f), new Vec3(0.1369633021f, 0.1341343041f, -0.4071228836f), new Vec3(-0.3529503428f, -0.2472893463f, -0.129514612f), new Vec3(-0.2590744185f, -0.2985577559f, -0.2150435121f), new Vec3(-0.3784019401f, 0.2199816631f, -0.1044989934f), new Vec3(-0.05635805671f, 0.1485737441f, 0.4210102279f), new Vec3(0.3251428613f, 0.09666046873f, -0.2957006485f), new Vec3(-0.4190995804f, 0.1406751354f, -0.08405978803f), new Vec3(-0.3253150961f, -0.3080335042f, -0.04225456877f), new Vec3(0.2857945863f, -0.05796152095f, 0.3427271751f), new Vec3(-0.2733604046f, 0.1973770973f, -0.2980207554f), new Vec3(0.219003657f, 0.2410037886f, -0.3105713639f), new Vec3(0.3182767252f, -0.271342949f, 0.1660509868f), new Vec3(-0.03222023115f, -0.3331161506f, -0.300824678f), new Vec3(-0.3087780231f, 0.1992794134f, -0.2596995338f), new Vec3(-0.06487611647f, -0.4311322747f, 0.1114273361f), new Vec3(0.3921171432f, -0.06294284106f, -0.2116183942f), new Vec3(-0.1606404506f, -0.358928121f, -0.2187812825f), new Vec3(-0.03767771199f, -0.2290351443f, 0.3855169162f), new Vec3(0.1394866832f, -0.3602213994f, 0.2308332918f), new Vec3(-0.4345093872f, 0.005751117145f, 0.1169124335f), new Vec3(-0.1044637494f, 0.4168128432f, -0.1336202785f), new Vec3(0.2658727501f, 0.2551943237f, 0.2582393035f), new Vec3(0.2051461999f, 0.1975390727f, 0.3484154868f), new Vec3(-0.266085566f, 0.23483312f, 0.2766800993f), new Vec3(0.07849405464f, -0.3300346342f, -0.2956616708f), new Vec3(-0.2160686338f, 0.05376451292f, -0.3910546287f), new Vec3(-0.185779186f, 0.2148499206f, 0.3490352499f), new Vec3(0.02492421743f, -0.3229954284f, -0.3123343347f), new Vec3(-0.120167831f, 0.4017266681f, 0.1633259825f), new Vec3(-0.02160084693f, -0.06885389554f, 0.4441762538f), new Vec3(0.2597670064f, 0.3096300784f, 0.1978643903f), new Vec3(-0.1611553854f, -0.09823036005f, 0.4085091653f), new Vec3(-0.3278896792f, 0.1461670309f, 0.2713366126f), new Vec3(0.2822734956f, 0.03754421121f, -0.3484423997f), new Vec3(0.03169341113f, 0.347405252f, -0.2842624114f), new Vec3(0.2202613604f, -0.3460788041f, -0.1849713341f), new Vec3(0.2933396046f, 0.3031973659f, 0.1565989581f), new Vec3(-0.3194922995f, 0.2453752201f, -0.200538455f), new Vec3(-0.3441586045f, -0.1698856132f, -0.2349334659f), new Vec3(0.2703645948f, -0.3574277231f, 0.04060059933f), new Vec3(0.2298568861f, 0.3744156221f, 0.0973588921f), new Vec3(0.09326603877f, -0.3170108894f, 0.3054595587f), new Vec3(-0.1116165319f, -0.2985018719f, 0.3177080142f), new Vec3(0.2172907365f, -0.3460005203f, -0.1885958001f), new Vec3(0.1991339479f, 0.3820341668f, -0.1299829458f), new Vec3(-0.0541918155f, -0.2103145071f, 0.39412061f), new Vec3(0.08871336998f, 0.2012117383f, 0.3926114802f), new Vec3(0.2787673278f, 0.3505404674f, 0.04370535101f), new Vec3(-0.322166438f, 0.3067213525f, 0.06804996813f), new Vec3(-0.4277366384f, 0.132066775f, 0.04582286686f), new Vec3(0.240131882f, -0.1612516055f, 0.344723946f), new Vec3(0.1448607981f, -0.2387819045f, 0.3528435224f), new Vec3(-0.3837065682f, -0.2206398454f, 0.08116235683f), new Vec3(-0.4382627882f, -0.09082753406f, -0.04664855374f), new Vec3(-0.37728353f, 0.05445141085f, 0.2391488697f), new Vec3(0.1259579313f, 0.348394558f, 0.2554522098f), new Vec3(-0.1406285511f, -0.270877371f, -0.3306796947f), new Vec3(-0.1580694418f, 0.4162931958f, -0.06491553533f), new Vec3(0.2477612106f, -0.2927867412f, -0.2353514536f), new Vec3(0.2916132853f, 0.3312535401f, 0.08793624968f), new Vec3(0.07365265219f, -0.1666159848f, 0.411478311f), new Vec3(-0.26126526f, -0.2422237692f, 0.2748965434f), new Vec3(-0.3721862032f, 0.252790166f, 0.008634938242f), new Vec3(-0.3691191571f, -0.255281188f, 0.03290232422f), new Vec3(0.2278441737f, -0.3358364886f, 0.1944244981f), new Vec3(0.363398169f, -0.2310190248f, 0.1306597909f), new Vec3(-0.304231482f, -0.2698452035f, 0.1926830856f), new Vec3(-0.3199312232f, 0.316332536f, -0.008816977938f), new Vec3(0.2874852279f, 0.1642275508f, -0.304764754f), new Vec3(-0.1451096801f, 0.3277541114f, -0.2720669462f), new Vec3(0.3220090754f, 0.0511344108f, 0.3101538769f), new Vec3(-0.1247400865f, -0.04333605335f, -0.4301882115f), new Vec3(-0.2829555867f, -0.3056190617f, -0.1703910946f), new Vec3(0.1069384374f, 0.3491024667f, -0.2630430352f), new Vec3(-0.1420661144f, -0.3055376754f, -0.2982682484f), new Vec3(-0.250548338f, 0.3156466809f, -0.2002316239f), new Vec3(0.3265787872f, 0.1871229129f, 0.2466400438f), new Vec3(0.07646097258f, -0.3026690852f, 0.324106687f), new Vec3(0.3451771584f, 0.2757120714f, -0.0856480183f), new Vec3(0.298137964f, 0.2852657134f, 0.179547284f), new Vec3(0.2812250376f, 0.3466716415f, 0.05684409612f), new Vec3(0.4390345476f, -0.09790429955f, -0.01278335452f), new Vec3(0.2148373234f, 0.1850172527f, 0.3494474791f), new Vec3(0.2595421179f, -0.07946825393f, 0.3589187731f), new Vec3(0.3182823114f, -0.307355516f, -0.08203022006f), new Vec3(-0.4089859285f, -0.04647718411f, 0.1818526372f), new Vec3(-0.2826749061f, 0.07417482322f, 0.3421885344f), new Vec3(0.3483864637f, 0.225442246f, -0.1740766085f), new Vec3(-0.3226415069f, -0.1420585388f, -0.2796816575f), new Vec3(0.4330734858f, -0.118868561f, -0.02859407492f), new Vec3(-0.08717822568f, -0.3909896417f, -0.2050050172f), new Vec3(-0.2149678299f, 0.3939973956f, -0.03247898316f), new Vec3(-0.2687330705f, 0.322686276f, -0.1617284888f), new Vec3(0.2105665099f, -0.1961317136f, -0.3459683451f), new Vec3(0.4361845915f, -0.1105517485f, 0.004616608544f), new Vec3(0.05333333359f, -0.313639498f, -0.3182543336f), new Vec3(-0.05986216652f, 0.1361029153f, -0.4247264031f), new Vec3(0.3664988455f, 0.2550543014f, -0.05590974511f), new Vec3(-0.2341015558f, -0.182405731f, 0.3382670703f), new Vec3(-0.04730947785f, -0.4222150243f, -0.1483114513f), new Vec3(-0.2391566239f, -0.2577696514f, -0.2808182972f), new Vec3(-0.1242081035f, 0.4256953395f, -0.07652336246f), new Vec3(0.2614832715f, -0.3650179274f, 0.02980623099f), new Vec3(-0.2728794681f, -0.3499628774f, 0.07458404908f), new Vec3(0.007892900508f, -0.1672771315f, 0.4176793787f), new Vec3(-0.01730330376f, 0.2978486637f, -0.3368779738f), new Vec3(0.2054835762f, -0.3252600376f, -0.2334146693f), new Vec3(-0.3231994983f, 0.1564282844f, -0.2712420987f), new Vec3(-0.2669545963f, 0.2599343665f, -0.2523278991f), new Vec3(-0.05554372779f, 0.3170813944f, -0.3144428146f), new Vec3(-0.2083935713f, -0.310922837f, -0.2497981362f), new Vec3(0.06989323478f, -0.3156141536f, 0.3130537363f), new Vec3(0.3847566193f, -0.1605309138f, -0.1693876312f), new Vec3(-0.3026215288f, -0.3001537679f, -0.1443188342f), new Vec3(0.3450735512f, 0.08611519592f, 0.2756962409f), new Vec3(0.1814473292f, -0.2788782453f, -0.3029914042f), new Vec3(-0.03855010448f, 0.09795110726f, 0.4375151083f), new Vec3(0.3533670318f, 0.2665752752f, 0.08105160988f), new Vec3(-0.007945601311f, 0.140359426f, -0.4274764309f), new Vec3(0.4063099273f, -0.1491768253f, -0.1231199324f), new Vec3(-0.2016773589f, 0.008816271194f, -0.4021797064f), new Vec3(-0.07527055435f, -0.425643481f, -0.1251477955f)};

    public static float lerp(float start, float end, float t)
    {
        return start * (1 - t) + end * t;
    }

    public static float lerpGrid(float valueNW, float valueNE, float valueSW, float valueSE, float tNS, float tEW)
    {
        float valueN = lerp(valueNW, valueNE, tEW);
        float valueS = lerp(valueSW, valueSE, tEW);
        return lerp(valueN, valueS, tNS);
    }

    /**
     * 3rd degree smoothstep function
     */
    public static float smooth3(float t)
    {
        return (3 - 2 * t) * t * t;
    }

    public static int fastFloor(float f)
    {
        return f < 0 ? (int) f - 1 : (int) f;
    }

    public static int fastRound(float f)
    {
        return f >= 0 ? (int) (f + 0.5f) : (int) (f - 0.5f);
    }

    public static int hash(long seed, int x, int y)
    {
        seed ^= 1619 * x;
        seed ^= 31337 * y;
        seed = seed * seed * seed * 60493;
        seed = (seed >> 13) ^ seed;
        return (int) seed;
    }

    public static int hash(long seed, int x, int y, int z)
    {
        seed ^= 1619 * x;
        seed ^= 31337 * y;
        seed ^= 6971 * z;
        seed = seed * seed * seed * 60493;
        seed = (seed >> 13) ^ seed;
        return (int) seed;
    }

    /**
     * Returns a random value between -1 and 1
     */
    public static float random(long seed, int x, int y)
    {
        seed ^= 1619 * x;
        seed ^= 31337 * y;
        return (int) (seed * seed * seed * 60493) / (float) 2147483648.0;
    }
}