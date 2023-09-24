package de.bluecolored.bluemap.core.dat;

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

        this.blockLight = new NibbleArray(chunkTag.getByteArray("BlockLight"));
        this.skyLight = new NibbleArray(chunkTag.getByteArray("SkyLight"));
        this.metadata = new NibbleArray(chunkTag.getByteArray("Data"));
        this.blocks = chunkTag.getByteArray("Blocks");
    }

    @Override
    public boolean isGenerated() {
        return chunkTag.getBoolean("TerrainPopulated");
    }

    @Override
    public long getInhabitedTime() {
        return 1;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (blocks.length == 0) return BlockState.AIR;

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

        // TODO: blockdata

        BlockState bstate = new BlockState(bid.getModernId(), metadataToProperties);

        return bstate;
    }

    @Override
    public LightData getLightData(int x, int y, int z, LightData target) {
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
        return 127;
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
