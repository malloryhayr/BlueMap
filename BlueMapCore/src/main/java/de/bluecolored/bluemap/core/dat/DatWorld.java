package de.bluecolored.bluemap.core.dat;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.bluecolored.bluemap.core.BlueMap;
import de.bluecolored.bluemap.core.logger.Logger;
import de.bluecolored.bluemap.core.mcr.region.RegionType;
import de.bluecolored.bluemap.core.mcr.region.WorldChunkManager;
import de.bluecolored.bluemap.core.util.Vector2iCache;
import de.bluecolored.bluemap.core.world.*;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.NBTUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.bluecolored.bluemap.core.mcr.MCRWorld.resolveLevelFile;

public class DatWorld implements World {
    private static final Grid CHUNK_GRID = new Grid(16);
    private static final Grid REGION_GRID = new Grid(1).multiply(CHUNK_GRID);
    private static final Vector2iCache VECTOR_2_I_CACHE = new Vector2iCache();
    private final Path worldFolder;
    private final String name;
    private final Vector3i spawnPoint;
    private final long seed;
    private final int skyLight;
    private final boolean ignoreMissingLightData;
    private final LoadingCache<Vector2i, Region> regionCache;
    private final LoadingCache<Vector2i, Chunk> chunkCache;
    protected WorldChunkManager wcm;

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

        this.wcm = new WorldChunkManager(this);
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
        return getChunk(x >> 4, z >> 4).getMaxY(x, z);
    }

    @Override
    public int getMinY(int x, int z) {
        return getChunk(x >> 4, z >> 4).getMinY(x, z);
    }

    @Override
    public Grid getChunkGrid() {
        return CHUNK_GRID;
    }

    @Override
    public Grid getRegionGrid() {
        return REGION_GRID;
    }

    @Override
    public Chunk getChunkAtBlock(int x, int y, int z) {
        return getChunk(x >> 4, z >> 4);
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return getChunk(VECTOR_2_I_CACHE.get(x, z));
    }

    private Chunk getChunk(Vector2i pos) {
        return chunkCache.get(pos);
    }

    @Override
    public Region getRegion(int x, int z) {
        return getRegion(VECTOR_2_I_CACHE.get(x, z));
    }

    private Region getRegion(Vector2i pos) {
        return regionCache.get(pos);
    }

    @Override
    public Collection<Vector2i> listRegions() {
        ArrayList<File> regionFiles = new ArrayList<>();
        File[] firstFolders = worldFolder.toFile().listFiles();
        firstFolders = (File[]) Arrays.stream(firstFolders).filter(s -> s.isDirectory() && !s.getName().equals("region") && !s.getName().equals("players")).toArray();
        for (int i = 0; i < firstFolders.length; i++) {
            File[] secondFolders = firstFolders[i].listFiles();
            for (File file : secondFolders[i].listFiles()) {
                regionFiles.add(file);
            }
        }

        List<Vector2i> regions = new ArrayList<>(regionFiles.size());

        for (File file : regionFiles) {
            if (RegionType.forFileName(file.getName()) == null) continue;
            if (file.length() <= 0) continue;

            try {
                String[] filenameParts = file.getName().split("\\.");
                int rX = Integer.parseInt(filenameParts[1], 36);
                int rZ = Integer.parseInt(filenameParts[2], 36);

                regions.add(new Vector2i(rX, rZ));
            } catch (NumberFormatException ignore) {}
        }

        return regions;
    }

    @Override
    public void invalidateChunkCache() {
        chunkCache.invalidateAll();
    }

    @Override
    public void invalidateChunkCache(int x, int z) {
        chunkCache.invalidate(VECTOR_2_I_CACHE.get(x, z));
    }

    @Override
    public void cleanUpChunkCache() {
        chunkCache.cleanUp();
    }

    private Region loadRegion(Vector2i regionPos) {
        return loadRegion(regionPos.getX(), regionPos.getY());
    }

    Region loadRegion(int x, int z) {
        return RegionType.loadRegion(this, worldFolder, x, z);
    }

    private Chunk loadChunk(Vector2i chunkPos) {
        return loadChunk(chunkPos.getX(), chunkPos.getY());
    }

    Chunk loadChunk(int x, int z) {
        final int tries = 3;
        final int tryInterval = 1000;

        Exception loadException = null;
        for (int i = 0; i < tries; i++) {
            try {
                return getRegion(x >> 5, z >> 5)
                        .loadChunk(x, z, ignoreMissingLightData);
            } catch(IOException | RuntimeException e) {
                if (loadException != null) e.addSuppressed(loadException);
                loadException = e;

                if (i + 1 < tries) {
                    try {
                        Thread.sleep(tryInterval);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        Logger.global.logDebug("Unexpected exception trying to load chunk (x:" + x + ", z:" + z + "):" + loadException);
        return EmptyChunk.INSTANCE;
    }
}
