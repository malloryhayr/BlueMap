package de.bluecolored.bluemap.core.dat;

import de.bluecolored.bluemap.core.logger.Logger;
import de.bluecolored.bluemap.core.mcr.BlockID;
import de.bluecolored.bluemap.core.mcr.LegacyBiomes;
import de.bluecolored.bluemap.core.mcr.NibbleArray;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.Chunk;
import de.bluecolored.bluemap.core.world.LightData;
import net.querz.nbt.CompoundTag;

import java.io.IOException;
import java.util.Map;

public class DatChunk implements Chunk {
    private final DatWorld world;
    private final CompoundTag chunkTag;
    private NibbleArray blockLight;
    private NibbleArray skyLight;
    private NibbleArray metadata;
    protected byte[] blocks;

    public DatChunk() {
        this.world = null;
        this.chunkTag = null;
    }

    public DatChunk(DatWorld world, CompoundTag chunkTag) {
        this.world = world;
        this.chunkTag = chunkTag;

        CompoundTag levelTag = chunkTag.getCompoundTag("Level");

        this.blockLight = new NibbleArray(levelTag.getByteArray("BlockLight"));
        this.skyLight = new NibbleArray(levelTag.getByteArray("SkyLight"));
        this.metadata = new NibbleArray(levelTag.getByteArray("Data"));
        this.blocks = levelTag.getByteArray("Blocks");
    }

    @Override
    public boolean isGenerated() {
        return chunkTag.getCompoundTag("Level").getBoolean("TerrainPopulated");
    }

    @Override
    public long getInhabitedTime() {
        return 1;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (blocks.length == 0) return BlockState.AIR;
        if (y >= 128 || y < 0) return BlockState.AIR;

        int ox = x, oz = z;

        x &= 0xF; z &= 0xF;

        int block_id = this.blocks[x << 11 | z << 7 | y] & 127;
        int metadata = this.metadata.getData(x, y, z);

        if (block_id == 0)
            return BlockState.AIR;

        BlockID bid = BlockID.query(block_id, metadata);

        if (bid == null)
            bid = BlockID.query(block_id);

        if (bid == null)
            return BlockState.MISSING;

        Map<String, String> metadataToProperties = BlockID.metadataToProperties(bid, metadata);

        if (block_id == 2) {
            int block_id_above = 0;

            if (y + 1 < 128) // avoid out of bounds
                block_id_above = this.blocks[x << 11 | z << 7 | (y+1)] & 127;

            if (block_id_above == 78 || block_id_above == 80)
                metadataToProperties.put("snowy", "true");
            else
                metadataToProperties.put("snowy", "false");
        } else if (block_id == 90) {
            // handle portals

            int block_id_xmin = this.world.getChunkAtBlock(ox-1, y, oz).fromBlocksArray(ox-1, y, oz);
            int block_id_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);

            if (block_id_xmin == 90 || block_id_xplus == 90)
                metadataToProperties.put("axis", "x");
            else
                metadataToProperties.put("axis", "z");

        } else if (block_id == 85) {
            // handle fences

            int block_id_xmin = this.world.getChunkAtBlock(ox-1, y, oz).fromBlocksArray(ox-1, y, oz);
            int block_id_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);
            int block_id_zmin = this.world.getChunkAtBlock(ox, y, oz-1).fromBlocksArray(ox, y, oz-1);
            int block_id_zplus = this.world.getChunkAtBlock(ox, y, oz+1).fromBlocksArray(ox, y, oz+1);

            if (block_id_xmin == 85)
                metadataToProperties.put("west", "true");

            if (block_id_xplus == 85)
                metadataToProperties.put("east", "true");

            if (block_id_zmin == 85)
                metadataToProperties.put("north", "true");

            if (block_id_zplus == 85)
                metadataToProperties.put("south", "true");

        } else if (block_id == 54) {
            // handle chests <pain>

            int block_id_xmin = this.world.getChunkAtBlock(ox-1, y, oz).fromBlocksArray(ox-1, y, oz);
            int block_id_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);
            int block_id_zmin = this.world.getChunkAtBlock(ox, y, oz-1).fromBlocksArray(ox, y, oz-1);
            int block_id_zplus = this.world.getChunkAtBlock(ox, y, oz+1).fromBlocksArray(ox, y, oz+1);

            if (block_id_xmin == 54) {

                int block_id_xmin_zplus = this.world.getChunkAtBlock(ox-1, y, oz+1).fromBlocksArray(ox-1, y, oz+1);
                int block_id_x_zplus = this.world.getChunkAtBlock(ox, y, oz+1).fromBlocksArray(ox, y, oz+1);

                if (BlockID.isOpaque(block_id_xmin_zplus) || BlockID.isOpaque(block_id_x_zplus)) {

                    metadataToProperties.put("facing", "north");
                    metadataToProperties.put("type", "right");


                } else {

                    metadataToProperties.put("facing", "south");
                    metadataToProperties.put("type", "left");

                }
            } else if (block_id_xplus == 54) {

                int block_id_xplus_zplus = this.world.getChunkAtBlock(ox+1, y, oz+1).fromBlocksArray(ox+1, y, oz+1);
                int block_id_x_zplus = this.world.getChunkAtBlock(ox, y, oz+1).fromBlocksArray(ox, y, oz+1);

                if (BlockID.isOpaque(block_id_xplus_zplus) || BlockID.isOpaque(block_id_x_zplus)) {

                    metadataToProperties.put("facing", "north");
                    metadataToProperties.put("type", "left");

                } else {

                    metadataToProperties.put("facing", "south");
                    metadataToProperties.put("type", "right");

                }
            } else if (block_id_zmin == 54) {

                int block_id_zmin_xplus = this.world.getChunkAtBlock(ox+1, y, oz-1).fromBlocksArray(ox+1, y, oz-1);
                int block_id_z_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);

                if (BlockID.isOpaque(block_id_zmin_xplus) || BlockID.isOpaque(block_id_z_xplus)) {

                    metadataToProperties.put("facing", "west");
                    metadataToProperties.put("type", "left");

                } else {

                    metadataToProperties.put("facing", "east");
                    metadataToProperties.put("type", "right");

                }
            } else if (block_id_zplus == 54) {

                int block_id_zplus_xplus = this.world.getChunkAtBlock(ox+1, y, oz+1).fromBlocksArray(ox+1, y, oz+1);
                int block_id_z_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);


                if (BlockID.isOpaque(block_id_zplus_xplus) || BlockID.isOpaque(block_id_z_xplus)) {

                    metadataToProperties.put("facing", "west");
                    metadataToProperties.put("type", "right");

                } else {

                    metadataToProperties.put("facing", "east");
                    metadataToProperties.put("type", "left");

                }
            } else {
                // singular chest

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
        } else if (block_id == 64 || block_id == 71) {
            // handle doors

            // the hinge is always on the left. right-hinge doors are just of different facing
            metadataToProperties.put("hinge", "left");
            metadataToProperties.put("powered", "false");

            if (metadata < 8)
                metadataToProperties.put("half", "lower");
            else
                metadataToProperties.put("half", "upper");


            metadata %= 8;

            if (metadata < 4)
                metadataToProperties.put("open", "false");
            else
                metadataToProperties.put("open", "true");

            metadata %= 4;

            if (metadata == 0)
                metadataToProperties.put("facing", "east");
            else if (metadata == 1)
                metadataToProperties.put("facing", "south");
            else if (metadata == 2)
                metadataToProperties.put("facing", "west");
            else if (metadata == 3)
                metadataToProperties.put("facing", "north");


        } else if (block_id == 51) {
            // handle fire

            int block_id_below = 0;

            if (y - 1 >= 0) // avoid out of bounds
                block_id_below = this.blocks[x << 11 | z << 7 | (y-1)] & 127;

            if (BlockID.isOpaque(block_id_below) || block_id_below == 30 || block_id_below == 52 ||
                    block_id_below == 85) { // + web, spawner, fence

                metadataToProperties.put("west", "false");
                metadataToProperties.put("east", "false");
                metadataToProperties.put("north", "false");
                metadataToProperties.put("south", "false");
                metadataToProperties.put("up", "false");

            } else {

                if (y + 1 < 128) { // avoid out of bounds

                    int block_id_above = this.blocks[x << 11 | z << 7 | (y+1)] & 127;

                    if (BlockID.isFlammable(block_id_above))
                        metadataToProperties.put("up", "true");

                }

                int block_id_xmin = this.world.getChunkAtBlock(ox-1, y, oz).fromBlocksArray(ox-1, y, oz);
                int block_id_xplus = this.world.getChunkAtBlock(ox+1, y, oz).fromBlocksArray(ox+1, y, oz);
                int block_id_zmin = this.world.getChunkAtBlock(ox, y, oz-1).fromBlocksArray(ox, y, oz-1);
                int block_id_zplus = this.world.getChunkAtBlock(ox, y, oz+1).fromBlocksArray(ox, y, oz+1);

                if (BlockID.isFlammable(block_id_xmin))
                    metadataToProperties.put("west", "true");

                if (BlockID.isFlammable(block_id_zmin))
                    metadataToProperties.put("north", "true");

                if (BlockID.isFlammable(block_id_xplus))
                    metadataToProperties.put("east", "true");

                if (BlockID.isFlammable(block_id_zplus))
                    metadataToProperties.put("south", "true");

            }

        } else if (block_id == 55) {
            // handle redstone wire TODO


        } else if (block_id == 63) {
            // sign support is non-existent at this point

//            	for (int i = 0; i < tileentities.size(); i++) {
//
//            		Tag<?> tag = tileentities.get(i);
//
//            		if (!(tag instanceof CompoundTag))
//            			continue;
//
//            		CompoundTag tileentity = (CompoundTag) tag;
//
//            		int tx = tileentity.getInt("x");
//            		int ty = tileentity.getInt("y");
//            		int tz = tileentity.getInt("z");
//
//            		if (tx != ox || ty != y || tz != oz)
//            			continue;
//
//            		if (!"Sign".equals(tileentity.getString("id")))
//        				break;
//
//            		String line1 = ((StringTag)tileentity.get("Text1")).getValue();
//            		String line2 = ((StringTag)tileentity.get("Text2")).getValue();
//            		String line3 = ((StringTag)tileentity.get("Text3")).getValue();
//            		String line4 = ((StringTag)tileentity.get("Text4")).getValue();
//            	}
        }

        BlockState bstate = new BlockState(bid.getModernId(), metadataToProperties);

        return bstate;
    }

    @Override
    public LightData getLightData(int x, int y, int z, LightData target) {
        if (y >= 127 || y < 0) return target.set(15, 0);
        if (blockLight.data.length == 0 && skyLight.data.length == 0) return target.set(0, 0);

        x &= 0xF; z &= 0xF;

        int blocklight = this.blockLight.data.length > 0 ? blockLight.getData(x, y, z) : 0;

        int block_id = this.blocks[x << 11 | z << 7 | y] & 127;

        // mcr: if slab or stairs, force light value to 7 (otherwise it looks weird)
        if (block_id == 44 || block_id == 53 || block_id == 67)
            blocklight = 7;

        return target.set(
                this.skyLight.data.length > 0 ? skyLight.getData(x, y, z) : 0,
                blocklight
        );
    }

    @Override
    public String getBiome(int x, int y, int z) {
        return LegacyBiomes.idFor(this.world.wcm.getBiome(x, z));
    }

    @Override
    public int getMaxY(int x, int z) {
        return 255;
    }

    @Override
    public int getMinY(int x, int z) {
        return 0;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return 63;
    }

    @Override
    public int getOceanFloorY(int x, int z) {
        return 50;
    }
}
