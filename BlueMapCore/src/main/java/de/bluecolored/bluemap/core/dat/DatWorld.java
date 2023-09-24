package de.bluecolored.bluemap.core.dat;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.bluecolored.bluemap.core.BlueMap;
import de.bluecolored.bluemap.core.world.Chunk;
import de.bluecolored.bluemap.core.world.Grid;
import de.bluecolored.bluemap.core.world.Region;
import de.bluecolored.bluemap.core.world.World;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.NBTUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static de.bluecolored.bluemap.core.mcr.MCRWorld.resolveLevelFile;

public class DatWorld implements World {
    private static final Grid CHUNK_GRID = new Grid(16);
    private final Path worldFolder;
    private final String name;
    private final Vector3i spawnPoint;
    private final long seed;
    private final int skyLight;
    private final boolean ignoreMissingLightData;
    private final LoadingCache<Vector2i, Region> regionCache;
    private final LoadingCache<Vector2i, Chunk> chunkCache;

    public DatWorld(Path worldFolder, int skyLight, boolean ignoreMissingLightData) throws IOException {
        this.worldFolder = worldFolder;
        this.skyLight = skyLight;
        this.ignoreMissingLightData = ignoreMissingLightData;

        this.regionCache = Caffeine.newBuilder()
                .executor(BlueMap.THREAD_POOL)
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(this::loadRegion);

        this.chunkCache = Caffeine.newBuilder()
                .executor(BlueMap.THREAD_POOL)
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(this::loadChunk);

        try {
            Path levelFile = resolveLevelFile(worldFolder);
            CompoundTag level = (CompoundTag) NBTUtil.readTag(levelFile.toFile());
            CompoundTag levelData = level.getCompoundTag("Data");

            this.name = "World";

            this.spawnPoint = new Vector3i(
                    levelData.getInt("SpawnX"),
                    levelData.getInt("SpawnY"),
                    levelData.getInt("SpawnZ")
            );

            this.seed = levelData.getLong("RandomSeed");
        } catch (IOException e) {
            throw new IOException("Invalid level.dat format!", e);
        }
    }

    @Override
    public Path getSaveFolder() {
        return worldFolder;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSkyLight() {
        return skyLight;
    }

    @Override
    public Vector3i getSpawnPoint() {
        return spawnPoint;
    }

    @Override
    public int getMaxY(int x, int z) {
        return 0;
    }

    @Override
    public int getMinY(int x, int z) {
        return 0;
    }

    @Override
    public Grid getChunkGrid() {
        return null;
    }

    @Override
    public Grid getRegionGrid() {
        return null;
    }

    @Override
    public Chunk getChunkAtBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return null;
    }

    @Override
    public Region getRegion(int x, int z) {
        return null;
    }

    @Override
    public Collection<Vector2i> listRegions() {
        return null;
    }

    @Override
    public void invalidateChunkCache() {

    }

    @Override
    public void invalidateChunkCache(int x, int z) {

    }

    @Override
    public void cleanUpChunkCache() {

    }
}
