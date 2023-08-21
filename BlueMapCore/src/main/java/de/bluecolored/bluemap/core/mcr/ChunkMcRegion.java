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

import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import net.querz.nbt.CompoundTag;

@SuppressWarnings("FieldMayBeFinal")
public class ChunkMcRegion extends MCRChunk {
    private static final long[] EMPTY_LONG_ARRAY = new long[0];

    private boolean isGenerated;
    private boolean hasLight;
    private Section section;

    @SuppressWarnings("unchecked")
    public ChunkMcRegion(MCRWorld world, CompoundTag chunkTag) {
        super(world, chunkTag);

        CompoundTag levelData = chunkTag.getCompoundTag("Level");

        this.isGenerated = levelData.getBoolean("TerrainPopulated");
        this.hasLight = isGenerated;
        
        byte[] blocks = chunkTag.getByteArray("Blocks");

        if (!isGenerated && getWorld().isIgnoreMissingLightData()) {
            isGenerated = blocks.length > 1;
        }

        section = new Section(levelData, world);
    }

    @Override
    public boolean isGenerated() {
        return isGenerated;
    }

    @Override
    public long getInhabitedTime() {
        return 1;
    }
    
    @Override
    public int fromBlocksArray(int x, int y, int z) {
    	x &= 0xF; z &= 0xF;
    	return this.section.blocks[x << 11 | z << 7 | y] & 255;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (y >= 128 || y <= 0) return BlockState.AIR;

        if (this.section == null) return BlockState.AIR;

        return this.section.getBlockState(x, y, z);
    }

    @Override
    public LightData getLightData(int x, int y, int z, LightData target) {
        if (!hasLight) return target.set(getWorld().getSkyLight(), 0);

        if (y >= 128 || y <= 0)
            return (y < 0) ? target.set(0, 0) : target.set(getWorld().getSkyLight(), 0);

        if (this.section == null) return target.set(getWorld().getSkyLight(), 0);

        return this.section.getLightData(x, y, z, target);
    }

    @Override
    public String getBiome(int x, int y, int z) {
        return LegacyBiomes.idFor(this.getWorld().wcm.getBiome(x, z));
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

        private NibbleArray blockLight;
        private NibbleArray skyLight;
        private NibbleArray metadata;
        protected byte[] blocks;
        private MCRWorld world;

        public Section(CompoundTag sectionData, MCRWorld world) {
        	this.world = world;
            this.blockLight = new NibbleArray(sectionData.getByteArray("BlockLight"));
            this.skyLight = new NibbleArray(sectionData.getByteArray("SkyLight"));
            this.metadata = new NibbleArray(sectionData.getByteArray("Data"));
            this.blocks = sectionData.getByteArray("Blocks");

            if (blocks.length < 256 && blocks.length > 0) blocks = Arrays.copyOf(blocks, 256);
            if (metadata.data.length < 256 && metadata.data.length > 0) metadata.data = Arrays.copyOf(metadata.data, 256);
            if (blockLight.data.length < 2048 && blockLight.data.length > 0) blockLight.data = Arrays.copyOf(blockLight.data, 2048);
            if (skyLight.data.length < 2048 && skyLight.data.length > 0) skyLight.data = Arrays.copyOf(skyLight.data, 2048);
        }

        public int getSectionY() {
            throw new RuntimeException("Method ChunkMcRegion.getSectionY() is unimplemented");
        }

        public BlockState getBlockState(int x, int y, int z) {
            if (blocks.length == 0) return BlockState.AIR;

            x &= 0xF; z &= 0xF; // Math.floorMod(pos.getX(), 16)
            
            int block_id = this.blocks[x << 11 | z << 7 | y] & 255;
            int metadata = this.metadata.getData(x, y, z);
            
            if (block_id == AIR_ID)
            	return BlockState.AIR;
            
            BlockID bid = BlockID.query(block_id, metadata);
            if (bid == null)
            	bid = BlockID.query(block_id);
            if (bid == null)
            	return BlockState.MISSING;
            
            Map<String, String> metadataToProperties = BlockID.metadataToProperties(bid, metadata);
            
            // ugly patch -- if grass block, define whether it's snowy or not
            // (doesn't seem to affect performance much)
            if (block_id == 2) {
            	
            	int block_id_above = this.blocks[x << 11 | z << 7 | (y+1)] & 255;
            	
            	if (block_id_above == 78 || block_id_above == 80)
            		metadataToProperties.put("snowy", "true");
            	else
            		metadataToProperties.put("snowy", "false");
            	
            } else if (block_id == 54) { // handle chests <pain>
            	
            	int block_id_xmin = this.world.getChunkAtBlock(x-1, y, z).fromBlocksArray(x-1, y, z);
            	int block_id_xplus = this.world.getChunkAtBlock(x+1, y, z).fromBlocksArray(x+1, y, z);
            	int block_id_zmin = this.world.getChunkAtBlock(x, y, z-1).fromBlocksArray(x, y, z-1);
            	int block_id_zplus = this.world.getChunkAtBlock(x, y, z+1).fromBlocksArray(x, y, +-1);
            	
            	if (block_id_xmin == 54) {
            		//int block_id_xmin_zmin = this.world.getChunkAtBlock(x-1, y, z-1).fromBlocksArray(x-1, y, z-1);
            		int block_id_xmin_zplus = this.world.getChunkAtBlock(x-1, y, z+1).fromBlocksArray(x-1, y, z+1);
            		//int block_id_x_zmin = this.world.getChunkAtBlock(x, y, z-1).fromBlocksArray(x, y, z-1);
            		int block_id_x_zplus = this.world.getChunkAtBlock(x, y, z+1).fromBlocksArray(x, y, z+1);
            		
            		if (BlockID.isOpaque(block_id_xmin_zplus) || BlockID.isOpaque(block_id_x_zplus)) {
            			metadataToProperties.put("facing", "north");
            			metadataToProperties.put("type", "left");
            		} else {//if (BlockID.isOpaque(block_id_xmin_zmin) || BlockID.isOpaque(block_id_x_zmin)) {
            			metadataToProperties.put("facing", "south");
            			metadataToProperties.put("type", "right");
            		}
            	} else if (block_id_xplus == 54) {
            		//int block_id_xplus_zmin = this.world.getChunkAtBlock(x+1, y, z-1).fromBlocksArray(x+1, y, z-1);
            		int block_id_xplus_zplus = this.world.getChunkAtBlock(x+1, y, z+1).fromBlocksArray(x+1, y, z+1);
            		//int block_id_x_zmin = this.world.getChunkAtBlock(x, y, z-1).fromBlocksArray(x, y, z-1);
            		int block_id_x_zplus = this.world.getChunkAtBlock(x, y, z+1).fromBlocksArray(x, y, z+1);
            		
            		if (BlockID.isOpaque(block_id_xplus_zplus) || BlockID.isOpaque(block_id_x_zplus)) {
            			metadataToProperties.put("facing", "north");
            			metadataToProperties.put("type", "right");
            		} else {//if (BlockID.isOpaque(block_id_xplus_zmin) || BlockID.isOpaque(block_id_x_zmin)) {
            			metadataToProperties.put("facing", "south");
            			metadataToProperties.put("type", "left");
            		}
            	} else if (block_id_zmin == 54) {
            		//int block_id_zmin_xmin = this.world.getChunkAtBlock(x-1, y, z-1).fromBlocksArray(x-1, y, z-1);
            		int block_id_zmin_xplus = this.world.getChunkAtBlock(x+1, y, z-1).fromBlocksArray(x+1, y, z-1);
            		//int block_id_z_xmin = this.world.getChunkAtBlock(x-1, y, z).fromBlocksArray(x-1, y, z);
            		int block_id_z_xplus = this.world.getChunkAtBlock(x+1, y, z).fromBlocksArray(x+1, y, z);
            		
            		if (BlockID.isOpaque(block_id_zmin_xplus) || BlockID.isOpaque(block_id_z_xplus)) {
            			metadataToProperties.put("facing", "west");
            			metadataToProperties.put("type", "right");
            		} else {//if (BlockID.isOpaque(block_id_zmin_xmin) || BlockID.isOpaque(block_id_z_xmin)) {
            			metadataToProperties.put("facing", "east");
            			metadataToProperties.put("type", "left");
            		}
            	} else if (block_id_zplus == 54) {
            		//int block_id_zplus_xmin = this.world.getChunkAtBlock(x-1, y, z+1).fromBlocksArray(x-1, y, z+1);
            		int block_id_zplus_xplus = this.world.getChunkAtBlock(x+1, y, z+1).fromBlocksArray(x+1, y, z+1);
            		//int block_id_z_xmin = this.world.getChunkAtBlock(x-1, y, z).fromBlocksArray(x-1, y, z);
            		int block_id_z_xplus = this.world.getChunkAtBlock(x+1, y, z).fromBlocksArray(x+1, y, z);
            		
            		
            		if (BlockID.isOpaque(block_id_zplus_xplus) || BlockID.isOpaque(block_id_z_xplus)) {
            			metadataToProperties.put("facing", "west");
            			metadataToProperties.put("type", "left");
            		} else {//if (BlockID.isOpaque(block_id_zplus_xmin) || BlockID.isOpaque(block_id_z_xmin)) {
            			metadataToProperties.put("facing", "east");
            			metadataToProperties.put("type", "right");
            		}
            	} else { // singular chest
            		
            		metadataToProperties.put("type", "single");
            		
            		if (BlockID.isOpaque(block_id_zmin))
            			metadataToProperties.put("facing", "south");
            		else if (BlockID.isOpaque(block_id_xmin))
            			metadataToProperties.put("facing", "east");
            		else if (BlockID.isOpaque(block_id_zplus))
            			metadataToProperties.put("facing", "north");
            		else if (BlockID.isOpaque(block_id_xplus))
            			metadataToProperties.put("facing", "west");
            		else
            			metadataToProperties.put("facing", "south");
            	}
            	
            }
            
            BlockState bstate = new BlockState(bid.getModernId(), metadataToProperties);
            
            return bstate;
        }

        public LightData getLightData(int x, int y, int z, LightData target) {
            if (blockLight.data.length == 0 && skyLight.data.length == 0) return target.set(0, 0);

            x &= 0xF; z &= 0xF; // Math.floorMod(pos.getX(), 16)

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
