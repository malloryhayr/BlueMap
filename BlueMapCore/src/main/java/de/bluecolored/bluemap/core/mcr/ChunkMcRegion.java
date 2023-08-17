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

import java.util.Arrays;
import java.util.Map;

import de.bluecolored.bluemap.core.world.Biome;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import net.querz.nbt.ByteArrayTag;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.IntArrayTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.NumberTag;
import net.querz.nbt.Tag;

@SuppressWarnings("FieldMayBeFinal")
public class ChunkMcRegion extends MCRChunk {
    private static final long[] EMPTY_LONG_ARRAY = new long[0];

    private boolean isGenerated;
    private boolean hasLight;
    private long inhabitedTime;
    private Section[] sections;
    private int[] biomes;

    @SuppressWarnings("unchecked")
    public ChunkMcRegion(MCRWorld world, CompoundTag chunkTag) {
        super(world, chunkTag);

        CompoundTag levelData = chunkTag.getCompoundTag("Level");

        String status = levelData.getString("Status");
        this.isGenerated = status.equals("full") ||
                status.equals("fullchunk") ||
                status.equals("postprocessed");
        this.hasLight = isGenerated;

        this.inhabitedTime = levelData.getLong("InhabitedTime");

        if (!isGenerated && getWorld().isIgnoreMissingLightData()) {
            isGenerated = !status.equals("empty");
        }

        sections = new Section[32]; //32 supports a max world-height of 512 which is the max that the hightmaps of Minecraft V1.13+ can store with 9 bits, i believe?
        if (levelData.containsKey("Sections")) {
            for (CompoundTag sectionTag : ((ListTag<CompoundTag>) levelData.getListTag("Sections"))) {
                Section section = new Section(sectionTag);
                if (section.getSectionY() >= 0 && section.getSectionY() < sections.length) sections[section.getSectionY()] = section;
            }
        } else {
            sections = new Section[0];
        }

        Tag<?> tag = levelData.get("Biomes"); //tag can be byte-array or int-array
        if (tag instanceof ByteArrayTag) {
            byte[] bs = ((ByteArrayTag) tag).getValue();
            biomes = new int[bs.length];

            for (int i = 0; i < bs.length; i++) {
                biomes[i] = bs[i] & 0xFF;
            }
        }
        else if (tag instanceof IntArrayTag) {
            biomes = ((IntArrayTag) tag).getValue();
        }

        if (biomes == null || biomes.length == 0) {
            biomes = new int[256];
        }

        if (biomes.length < 256) {
            biomes = Arrays.copyOf(biomes, 256);
        }
    }

    @Override
    public boolean isGenerated() {
        return isGenerated;
    }

    @Override
    public long getInhabitedTime() {
        return inhabitedTime;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length) return BlockState.AIR;

        Section section = this.sections[sectionY];
        if (section == null) return BlockState.AIR;

        return section.getBlockState(x, y, z);
    }

    @Override
    public LightData getLightData(int x, int y, int z, LightData target) {
        if (!hasLight) return target.set(getWorld().getSkyLight(), 0);

        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length)
            return (y < 0) ? target.set(0, 0) : target.set(getWorld().getSkyLight(), 0);

        Section section = this.sections[sectionY];
        if (section == null) return target.set(getWorld().getSkyLight(), 0);

        return section.getLightData(x, y, z, target);
    }

    @Override
    public String getBiome(int x, int y, int z) {
        x &= 0xF; z &= 0xF;
        int biomeIntIndex = z * 16 + x;

        if (biomeIntIndex >= this.biomes.length) return Biome.DEFAULT.getFormatted();

        return LegacyBiomes.idFor(biomes[biomeIntIndex]);
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return 63;
    }

    @Override
    public int getOceanFloorY(int x, int z) {
        return 50; // TODO figure out the actual min noise value
    }

    private static class Section {
        private static final int AIR_ID = 0;

        private int sectionY;
        private NibbleArray blockLight;
        private NibbleArray skyLight;
        private NibbleArray metadata;
        private byte[] blocks;
        private BlockState[] palette;

        //private int bitsPerBlock;

        public Section(CompoundTag sectionData) {
            this.sectionY = sectionData.get("Y", NumberTag.class).asInt();
            this.blockLight = new NibbleArray(sectionData.getByteArray("BlockLight"));
            this.skyLight = new NibbleArray(sectionData.getByteArray("SkyLight"));
            this.metadata = new NibbleArray(sectionData.getByteArray("Data"));
            this.blocks = sectionData.getByteArray("Blocks");

            if (blocks.length < 256 && blocks.length > 0) blocks = Arrays.copyOf(blocks, 256);
            if (metadata.data.length < 256 && metadata.data.length > 0) metadata.data = Arrays.copyOf(metadata.data, 256);
            if (blockLight.data.length < 2048 && blockLight.data.length > 0) blockLight.data = Arrays.copyOf(blockLight.data, 2048);
            if (skyLight.data.length < 2048 && skyLight.data.length > 0) skyLight.data = Arrays.copyOf(skyLight.data, 2048);
            
            //read block palette
//            ListTag<CompoundTag> paletteTag = (ListTag<CompoundTag>) sectionData.getListTag("Palette");
//            if (paletteTag != null) {
//                this.palette = new BlockState[paletteTag.size()];
//                for (int i = 0; i < this.palette.length; i++) {
//                    CompoundTag stateTag = paletteTag.get(i);
//
//                    String id = stateTag.getString("Name"); //shortcut to save time and memory
//                    if (id.equals(AIR_ID)) {
//                        palette[i] = BlockState.AIR;
//                        continue;
//                    }
//
//                    Map<String, String> properties = new HashMap<>();
//
//                    if (stateTag.containsKey("Properties")) {
//                        CompoundTag propertiesTag = stateTag.getCompoundTag("Properties");
//                        for (Entry<String, Tag<?>> property : propertiesTag) {
//                            properties.put(property.getKey().toLowerCase(), ((StringTag) property.getValue()).getValue().toLowerCase());
//                        }
//                    }
//
//                    palette[i] = new BlockState(id, properties);
//                }
//            } else {
//                this.palette = new BlockState[0];
//            }

            //this.bitsPerBlock = this.blocks.length >> 6; // available longs * 64 (bits per long) / 4096 (blocks per section) (floored result)
        }

        public int getSectionY() {
            return sectionY;
        }

        public BlockState getBlockState(int x, int y, int z) {
            if (palette.length == 1) return palette[0];
            if (blocks.length == 0) return BlockState.AIR;

            x &= 0xF; y &= 0xF; z &= 0xF; // Math.floorMod(pos.getX(), 16)
            
            int block_id = this.blocks[x << 11 | z << 7 | y];
            int metadata = this.metadata.getData(x, y, z);
            
            BlockID bid = BlockID.query(block_id, metadata);
            if (bid == null)
            	bid = BlockID.query(block_id);
            if (bid == null)
            	return BlockState.MISSING;
            
            Map<String, String> metadataToProperties = BlockID.metadataToProperties(bid, metadata);
            BlockState bstate = new BlockState(bid.getModernId(), metadataToProperties);

            return bstate;
        }

        public LightData getLightData(int x, int y, int z, LightData target) {
            if (blockLight.data.length == 0 && skyLight.data.length == 0) return target.set(0, 0);

            x &= 0xF; y &= 0xF; z &= 0xF; // Math.floorMod(pos.getX(), 16)

            //int blockByteIndex = (x * 16 + z) * 16 + y;
            //int blockHalfByteIndex = blockByteIndex >> 1; // blockByteIndex / 2
            //boolean largeHalf = (blockByteIndex & 0x1) != 0; // (blockByteIndex % 2) == 0

            return target.set(
                    this.skyLight.data.length > 0 ? skyLight.getData(x, y, z) : 0,
                    this.blockLight.data.length > 0 ? blockLight.getData(x, y, z) : 0
            );
        }
    }

}
