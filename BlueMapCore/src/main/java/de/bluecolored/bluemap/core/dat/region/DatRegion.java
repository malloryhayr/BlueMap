package de.bluecolored.bluemap.core.dat.region;

import com.flowpowered.math.vector.Vector2i;
import de.bluecolored.bluemap.core.dat.DatChunk;
import de.bluecolored.bluemap.core.dat.DatWorld;
import de.bluecolored.bluemap.core.logger.Logger;
import de.bluecolored.bluemap.core.world.Chunk;
import de.bluecolored.bluemap.core.world.EmptyChunk;
import de.bluecolored.bluemap.core.world.Region;
import de.bluecolored.bluemap.core.world.World;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.NBTUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DatRegion implements Region {
    public static final String FILE_SUFFIX = ".dat";
    private final DatWorld world;
    private final Path regionFile;
    private final Vector2i regionPos;

    public DatRegion(World world, Path regionFile) throws IllegalArgumentException {
        this.world = (DatWorld) world;
        this.regionFile = regionFile;

        String[] filenameParts = regionFile.getFileName().toString().split("\\.");
        int rX = Integer.parseInt(filenameParts[1], 36);
        int rZ = Integer.parseInt(filenameParts[2], 36);

        this.regionPos = new Vector2i(rX, rZ);
    }

    @Override
    public Collection<Vector2i> listChunks(long modifiedSince) {
        if (Files.notExists(regionFile)) return Collections.emptyList();

        try {
            long fileLength = Files.size(regionFile);
            if (fileLength == 0) return Collections.emptyList();
        } catch (IOException ex) {
            Logger.global.logWarning("Failed to read file-size for file: " + regionFile);
            return Collections.emptyList();
        }

        List<Vector2i> chunks = new ArrayList<>(1); // 1 chunk per "region" file

        chunks.add(new Vector2i(regionPos.getX(), regionPos.getY()));

        return chunks;
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ, boolean ignoreMissingLightData) throws IOException {
        if (Files.notExists(regionFile)) return EmptyChunk.INSTANCE;

        long fileLength = Files.size(regionFile);
        if (fileLength == 0) return EmptyChunk.INSTANCE;

        try {
            DatChunk chunk = new DatChunk(world, (CompoundTag) NBTUtil.readTag(regionFile.toFile()));

            return chunk;
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Path getRegionFile() {
        return regionFile;
    }

    public static String getRegionFileName(int regionX, int regionZ) {
        return Integer.toString(regionX % 64, 36) + "/" + Integer.toString(regionZ % 64, 36) + "/" + "c." + Integer.toString(regionX, 36) + "." + Integer.toString(regionZ, 36) + FILE_SUFFIX;
    }

}
