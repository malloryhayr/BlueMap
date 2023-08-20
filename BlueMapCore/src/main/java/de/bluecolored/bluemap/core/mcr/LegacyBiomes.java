/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.core.mcr;

import java.util.HashMap;
import java.util.Map;

import de.bluecolored.bluemap.core.mcr.region.WorldChunkManager.BiomeBase;

public class LegacyBiomes {

    private static final Map<BiomeBase, String> BIOME_IDS = new HashMap<BiomeBase, String>();
    static {
    	// biome names to match: https://github.com/SkyDeckAGoGo/neo-beta-datapack
    	BIOME_IDS.put(BiomeBase.DESERT, "minecraft:desert");
    	BIOME_IDS.put(BiomeBase.PLAINS, "minecraft:plains");
    	BIOME_IDS.put(BiomeBase.FOREST, "minecraft:forest");
    	BIOME_IDS.put(BiomeBase.TAIGA, "minecraft:taiga");
    	BIOME_IDS.put(BiomeBase.TUNDRA, "minecraft:tundra");
    	BIOME_IDS.put(BiomeBase.SHRUBLAND, "minecraft:shrubland");
    	BIOME_IDS.put(BiomeBase.RAINFOREST, "minecraft:rain_forest");
    	BIOME_IDS.put(BiomeBase.SAVANNA, "minecraft:savannah");
    	BIOME_IDS.put(BiomeBase.SEASONAL_FOREST, "minecraft:seasonal_forest");
    	BIOME_IDS.put(BiomeBase.ICE_DESERT, "minecraft:tundra");
    	BIOME_IDS.put(BiomeBase.SWAMPLAND, "minecraft:swampland");
    	BIOME_IDS.put(BiomeBase.HELL, "minecraft:nether");
    }

    public static String idFor(BiomeBase base) {
        return BIOME_IDS.get(base);
    }

}
