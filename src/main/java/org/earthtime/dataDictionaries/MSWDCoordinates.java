/*
 * MSWDCoordinates.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.dataDictionaries;

/**
 *
 * @author James F. Bowring
 */
public class MSWDCoordinates {
// produced by Noah McClean for the ...

    /**
     * 
     */
    public final static double[][] valuesByPointCount = new double[][]{
        {0, 0, 0, 0, 0, 0},
        {1, 0, 0, 0, 0, 0},
        {2, 0, 0, 2.995732274, 6.907755279, 0.75},
        {3, 0, 0.0010541, 2.60525, 5.422078732, 0.725912174},
        {4, 0, 0.021182, 2.382574589, 4.616706738, 0.735758882},
        {5, 0, 0.059248, 2.238288991, 4.10300113, 0.770901649},
        {6, 0, 0.101167, 2.133739487, 3.742957414, 0.812011699},
        {7, 0, 0.141319, 2.052654134, 3.474555193, 0.854290649},
        {8, 0, 0.178126, 1.987069967, 3.265560195, 0.896167231},
        {9, 0, 0.211399, 1.932471747, 3.097462763, 0.937077973},
        {10, 0, 0.241393, 1.88603946, 2.958829845, 0.976834074},
        {11, 0, 0.268475, 1.845900814, 2.842193965, 1.015399783},
        {12, 0, 0.293014, 1.810738905, 2.742457534, 1.052804219},
        {13, 0, 0.315342, 1.779599044, 2.656013767, 1.089103221},
        {14, 0, 0.335748, 1.751762345, 2.580233834, 1.124361987},
        {15, 0, 0.354476, 1.726684904, 2.513153215, 1.158646987},
        {16, 0, 0.371733, 1.703940639, 2.453272174, 1.192022237},
        {17, 0, 0.387697, 1.683183648, 2.399424512, 1.224547711},
        {18, 0, 0.402515, 1.664146228, 2.350688685, 1.256278788},
        {19, 0, 0.416316, 1.646599909, 2.306326103, 1.287266229},
        {20, 0, 0.429209, 1.630359852, 2.265737331, 1.3175564},
        {21, 0.306984598, 0.441286, 1.615276068, 2.228430383, 1.347191594},
        {22, 0.317407656, 0.452631, 1.601211574, 2.193997377, 1.376210393},
        {23, 0.327358251, 0.463312, 1.588061878, 2.162097064, 1.40464803},
        {24, 0.336870066, 0.473391, 1.575731346, 2.132441574, 1.432536723},
        {25, 0.345973745, 0.482923, 1.564136294, 2.104786231, 1.459905984},
        {26, 0.354697186, 0.491954, 1.55320959, 2.07892163, 1.486782902},
        {27, 0.363065812, 0.500528, 1.542885505, 2.054667415, 1.513192388},
        {28, 0.371102826, 0.50868, 1.533115153, 2.031867335, 1.539157399},
        {29, 0.378829431, 0.516444, 1.523849471, 2.010385293, 1.564699138},
        {30, 0.386265035, 0.523851, 1.515043921, 1.990102143, 1.589837219},
        {31, 0.393427431, 0.530926, 1.506664707, 1.970913099, 1.61458983},
        {32, 0.400332956, 0.537693, 1.498678584, 1.952725596, 1.638973867},
        {33, 0.406996637, 0.544175, 1.491053181, 1.935457531, 1.66300505},
        {34, 0.413432317, 0.550389, 1.483766942, 1.919035808, 1.686698038},
        {35, 0.41965277, 0.556356, 1.476789573, 1.90339511, 1.710066519},
        {36, 0.4256698, 0.562089, 1.470105527, 1.888476879, 1.7331233},
        {37, 0.431494332, 0.567605, 1.463690835, 1.874228446, 1.755880379},
        {38, 0.437136496, 0.572917, 1.457528592, 1.8606023, 1.778349013},
        {39, 0.442605692, 0.578036, 1.451605064, 1.84755546, 1.800539784},
        {40, 0.447910663, 0.582974, 1.445904148, 1.835048938, 1.822462649},
        {41, 0.453059546, 0.587742, 1.440411216, 1.823047278, 1.844126992},
        {42, 0.458059925, 0.592349, 1.435114932, 1.81151816, 1.865541665},
        {43, 0.462918883, 0.596805, 1.430001825, 1.800432052, 1.886715036},
        {44, 0.467643035, 0.601116, 1.425065322, 1.789761914, 1.907655015},
        {45, 0.472238575, 0.605291, 1.420293185, 1.779482934, 1.928369098},
        {46, 0.476711304, 0.609337, 1.415676534, 1.769572297, 1.948864388},
        {47, 0.481066663, 0.61326, 1.411208027, 1.76000899, 1.969147629},
        {48, 0.485309762, 0.617067, 1.406878383, 1.750773619, 1.989225225},
        {49, 0.489445403, 0.620763, 1.402681641, 1.741848257, 2.00910327},
        {50, 0.493478105, 0.624353, 1.398611663, 1.733216304, 2.028787563},
        {51, 0.497412127, 0.627842, 1.394662164, 1.724862362, 2.048283629},
        {52, 0.501251482, 0.631235, 1.390826736, 1.716772131, 2.067596738},
        {53, 0.504999959, 0.634537, 1.387098876, 1.708932308, 2.08673192},
        {54, 0.508661136, 0.637751, 1.383475354, 1.701330498, 2.105693978},
        {55, 0.512238398, 0.640881, 1.379951141, 1.693955141, 2.124487507},
        {56, 0.515734946, 0.643931, 1.376521129, 1.68679544, 2.143116901},
        {57, 0.519153813, 0.646903, 1.373183452, 1.679841295, 2.161586369},
        {58, 0.522497876, 0.649803, 1.369929546, 1.673083251, 2.179899942},
        {59, 0.525769862, 0.652631, 1.366760696, 1.666512443, 2.198061487},
        {60, 0.52897236, 0.655392, 1.363669882, 1.660120551, 2.216074714},
        {61, 0.532107831, 0.658087, 1.360656588, 1.653899759, 2.233943185},
        {62, 0.535178616, 0.66072, 1.357715345, 1.647842715, 2.251670321},
        {63, 0.538186941, 0.663292, 1.354845513, 1.641942497, 2.269259412},
        {64, 0.541134925, 0.665807, 1.352041554, 1.636192582, 2.286713625},
        {65, 0.544024589, 0.668265, 1.34930433, 1.630586817, 2.304036004},
        {66, 0.546857859, 0.67067, 1.346628238, 1.625119391, 2.321229483},
        {67, 0.549636573, 0.673023, 1.344012453, 1.619784813, 2.338296889},
        {68, 0.552362485, 0.675326, 1.341454509, 1.614577889, 2.355240948},
        {69, 0.555037271, 0.67758, 1.338953494, 1.609493704, 2.372064287},
        {70, 0.557662534, 0.679788, 1.336505306, 1.604527598, 2.388769444},
        {71, 0.560239806, 0.681951, 1.334108979, 1.599675154, 2.405358868},
        {72, 0.562770552, 0.68407, 1.33176351, 1.594932182, 2.421834926},
        {73, 0.565256177, 0.686148, 1.32946475, 1.5902947, 2.438199905},
        {74, 0.567698025, 0.688184, 1.327214765, 1.585758927, 2.454456017},
        {75, 0.570097386, 0.690181, 1.32500937, 1.581321264, 2.470605399},
        {76, 0.572455496, 0.69214, 1.322847475, 1.576978288, 2.486650123},
        {77, 0.574773541, 0.694063, 1.320726433, 1.572726739, 2.502592193},
        {78, 0.57705266, 0.695949, 1.318648189, 1.56856351, 2.51843355},
        {79, 0.579293945, 0.697801, 1.316608527, 1.564485639, 2.534176075},
        {80, 0.581498449, 0.699619, 1.314607817, 1.5604903, 2.549821591},
        {81, 0.583667181, 0.701404, 1.31264488, 1.556574794, 2.565371867},
        {82, 0.585801111, 0.703158, 1.310717008, 1.552736544, 2.580828616},
        {83, 0.587901176, 0.704881, 1.308824522, 1.548973085, 2.596193505},
        {84, 0.589968274, 0.706574, 1.306966208, 1.545282062, 2.611468147},
        {85, 0.592003271, 0.708239, 1.30513934, 1.541661221, 2.626654113},
        {86, 0.594007002, 0.709875, 1.303345699, 1.538108402, 2.641752926},
        {87, 0.595980272, 0.711483, 1.301584038, 1.534621538, 2.656766067},
        {88, 0.597923856, 0.713065, 1.299851611, 1.531198648, 2.671694976},
        {89, 0.599838501, 0.714621, 1.298148658, 1.527837832, 2.686541052},
        {90, 0.601724928, 0.716152, 1.296473918, 1.524537268, 2.701305657},
        {91, 0.603583836, 0.717659, 1.294826125, 1.521295206, 2.715990116},
        {92, 0.605415895, 0.719141, 1.293206969, 1.518109968, 2.730595718},
        {93, 0.607221755, 0.720601, 1.291612207, 1.514979938, 2.745123717},
        {94, 0.609002045, 0.722037, 1.290044985, 1.511903568, 2.759575337},
        {95, 0.610757369, 0.723452, 1.288501061, 1.508879366, 2.773951768},
        {96, 0.612488316, 0.724845, 1.286982086, 1.505905898, 2.788254169},
        {97, 0.614195451, 0.726217, 1.285486755, 1.502981784, 2.80248367},
        {98, 0.615879325, 0.727569, 1.284013767, 1.500105697, 2.816641375},
        {99, 0.617540467, 0.728901, 1.282563278, 1.497276355, 2.830728358},
        {100, 0.619179392, 0.730213, 1.281135435, 1.494492528, 2.844745665},
        {101, 0.620796598, 0.731506, 1.27972892, 1.491753026, 2.858694321},
        {102, 0.622392568, 0.732781, 1.278342419, 1.489056706, 2.872575323},
        {103, 0.623967768, 0.734038, 1.276976067, 1.486402461, 2.886389644},
        {104, 0.625522652, 0.735277, 1.275629989, 1.483789226, 2.900138235},
        {105, 0.627057659, 0.736498, 1.274304302, 1.481215973, 2.913822025},
        {106, 0.628573214, 0.737703, 1.272996238, 1.478681708, 2.92744192},
        {107, 0.630069731, 0.738892, 1.271705915, 1.476185471, 2.940998806},
        {108, 0.631547611, 0.740064, 1.270434877, 1.473726336, 2.954493549},
        {109, 0.633007242, 0.741221, 1.269180358, 1.471303407, 2.967926994},
        {110, 0.634449003, 0.742362, 1.267943892, 1.468915817, 2.981299969},
        {111, 0.63587326, 0.743488, 1.266724143, 1.466562729, 2.994613282},
        {112, 0.63728037, 0.744599, 1.265521204, 1.464243332, 3.007867723},
        {113, 0.638670678, 0.745696, 1.26433374, 1.461956842, 3.021064067},
        {114, 0.640044521, 0.746779, 1.26316184, 1.459702501, 3.034203069},
        {115, 0.641402226, 0.747849, 1.26200417, 1.457479573, 3.047285471},
        {116, 0.642744111, 0.748904, 1.260863646, 1.455287346, 3.060311996},
        {117, 0.644070485, 0.749947, 1.259736095, 1.45312513, 3.073283354},
        {118, 0.645381648, 0.750976, 1.258624423, 1.450992258, 3.086200239},
        {119, 0.646677892, 0.751993, 1.257525876, 1.448888081, 3.099063331},
        {120, 0.647959504, 0.752997, 1.256441937, 1.44681197, 3.111873297},
        {121, 0.649226758, 0.753989, 1.255371264, 1.444763318, 3.124630788},
        {122, 0.650479926, 0.75497, 1.25431252, 1.442741532, 3.137336444},
        {123, 0.65171927, 0.7563, 1.25276148, 1.440746039, 3.149990891},
        {124, 0.652945045, 0.756895, 1.252236687, 1.438776283, 3.162594743},
        {125, 0.654157501, 0.757841, 1.251216915, 1.436831722, 3.1751486},
        {126, 0.655356881, 0.758776, 1.250209319, 1.434911832, 3.187653054},
        {127, 0.65654342, 0.7597, 1.249213954, 1.433016104, 3.200108682},
        {128, 0.657717349, 0.760614, 1.248229481, 1.431144042, 3.212516051},
        {129, 0.658878894, 0.761517, 1.247257345, 1.429295164, 3.224875718},
        {130, 0.660028273, 0.76241, 1.246296206, 1.427469003, 3.237188228},
        {131, 0.6611657, 0.763293, 1.245346113, 1.425665104, 3.249454117},
        {132, 0.662291384, 0.764166, 1.244407114, 1.423883025, 3.261673909},
        {133, 0.663405527, 0.765029, 1.243479254, 1.422122335, 3.273848121},
        {134, 0.664508329, 0.765883, 1.242561194, 1.420382617, 3.285977258},
        {135, 0.665599982, 0.766728, 1.241652981, 1.418663463, 3.298061816},
        {136, 0.666680677, 0.767563, 1.240756035, 1.416964476, 3.310102284},
        {137, 0.667750597, 0.76839, 1.239867641, 1.415285271, 3.32209914},
        {138, 0.668809923, 0.769208, 1.238989218, 1.413625472, 3.334052855},
        {139, 0.66985883, 0.770017, 1.238120803, 1.411984714, 3.345963889},
        {140, 0.670897491, 0.770817, 1.237262431, 1.410362641, 3.357832698},
        {141, 0.671926073, 0.77161, 1.236411398, 1.408758904, 3.369659726},
        {142, 0.672944741, 0.772394, 1.235570481, 1.407173167, 3.381445412},
        {143, 0.673953654, 0.77317, 1.234738344, 1.405605099, 3.393190185},
        {144, 0.67495297, 0.773938, 1.23391502, 1.404054379, 3.404894469},
        {145, 0.675942842, 0.774698, 1.233100542, 1.402520694, 3.416558679},
        {146, 0.676923419, 0.775451, 1.232293579, 1.401003739, 3.428183224},
        {147, 0.677894848, 0.776196, 1.231495524, 1.399503215, 3.439768505},
        {148, 0.678857271, 0.776934, 1.230705045, 1.398018833, 3.451314918},
        {149, 0.67981083, 0.777664, 1.229923533, 1.396550308, 3.462822851},
        {150, 0.68075566, 0.778387, 1.229149654, 1.395097365, 3.474292685},
        {151, 0.681691897, 0.779103, 1.228383438, 1.393659733, 3.485724797},
        {152, 0.682619671, 0.779813, 1.227623558, 1.392237149, 3.497119556},
        {153, 0.68353911, 0.780515, 1.226872748, 1.390829356, 3.508477326},
        {154, 0.68445034, 0.781211, 1.226128328, 1.389436101, 3.519798465},
        {155, 0.685353485, 0.7819, 1.225391673, 1.388057142, 3.531083323},
        {156, 0.686248663, 0.782583, 1.224661459, 1.386692236, 3.542332249},
        {157, 0.687135994, 0.783259, 1.223939058, 1.385341152, 3.553545582},
        {158, 0.688015593, 0.783929, 1.223223144, 1.384003659, 3.564723658},
        {159, 0.688887572, 0.784593, 1.222513742, 1.382679535, 3.575866809},
        {160, 0.689752043, 0.78525, 1.221812217, 1.381368562, 3.586975357},
        {161, 0.690609114, 0.785902, 1.221115902, 1.380070525, 3.598049625},
        {162, 0.691458891, 0.786547, 1.220427504, 1.378785217, 3.609089927},
        {163, 0.692301479, 0.787187, 1.21974436, 1.377512432, 3.620096574},
        {164, 0.693136979, 0.787821, 1.219067831, 1.376251973, 3.631069872},
        {165, 0.693965491, 0.78845, 1.218396599, 1.375003643, 3.642010121},
        {166, 0.694787114, 0.789073, 1.217732019, 1.373767252, 3.652917619},
        {167, 0.695601943, 0.78969, 1.21707411, 1.372542614, 3.663792657},
        {168, 0.696410073, 0.790302, 1.216421553, 1.371329545, 3.674635524},
        {169, 0.697211597, 0.790909, 1.215774367, 1.370127866, 3.685446503},
        {170, 0.698006604, 0.79151, 1.215133902, 1.368937404, 3.696225874},
        {171, 0.698795185, 0.792107, 1.214497511, 1.367757986, 3.706973913},
        {172, 0.699577425, 0.792697, 1.213869202, 1.366589446, 3.71769089},
        {173, 0.70035341, 0.793283, 1.213245001, 1.365431619, 3.728377075},
        {174, 0.701123225, 0.793864, 1.212626254, 1.364284344, 3.739032729},
        {175, 0.701886952, 0.79444, 1.212012974, 1.363147464, 3.749658114},
        {176, 0.702644671, 0.795012, 1.211403853, 1.362020826, 3.760253487},
        {177, 0.703396462, 0.795578, 1.210801554, 1.360904277, 3.770819098},
        {178, 0.704142402, 0.79614, 1.210203443, 1.359797671, 3.781355199},
        {179, 0.704882569, 0.796697, 1.209610857, 1.358700862, 3.791862035},
        {180, 0.705617036, 0.79725, 1.209022487, 1.357613708, 3.802339848},
        {181, 0.706345879, 0.797798, 1.208439669, 1.356536071, 3.812788877},
        {182, 0.707069167, 0.798342, 1.207861095, 1.355467814, 3.823209358},
        {183, 0.707786974, 0.798881, 1.207288097, 1.354408804, 3.833601524},
        {184, 0.708499369, 0.799416, 1.206719368, 1.353358908, 3.843965605},
        {185, 0.709206419, 0.799947, 1.206154923, 1.352318, 3.854301826},
        {186, 0.709908193, 0.800474, 1.205594772, 1.351285953, 3.864610412},
        {187, 0.710604756, 0.800996, 1.205040242, 1.350262643, 3.874891581},
        {188, 0.711296174, 0.801514, 1.20449003, 1.34924795, 3.885145553},
        {189, 0.71198251, 0.802029, 1.203942835, 1.348241755, 3.895372542},
        {190, 0.712663826, 0.802539, 1.203401292, 1.34724394, 3.905572759},
        {191, 0.713340185, 0.803045, 1.202864099, 1.346254393, 3.915746414},
        {192, 0.714011646, 0.803548, 1.202329958, 1.345273, 3.925893712},
        {193, 0.71467827, 0.804046, 1.201801497, 1.344299652, 3.936014859},
        {194, 0.715340115, 0.804541, 1.201276109, 1.34333424, 3.946110054},
        {195, 0.715997238, 0.805032, 1.200755112, 1.342376659, 3.956179496},
        {196, 0.716649697, 0.805519, 1.200238515, 1.341426805, 3.966223382},
        {197, 0.717297545, 0.806003, 1.199725022, 1.340484575, 3.976241904},
        {198, 0.717940839, 0.806483, 1.199215947, 1.33954987, 3.986235255},
        {199, 0.718579631, 0.80696, 1.198709997, 1.33862259, 3.996203623},
        {200, 0.719213975, 0.807433, 1.198208482, 1.337702639, 4.006147193}};
}
