package dev.roundtable.beehoven.utils;

import spark.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP utility class. Used for compressing and decompressing data.
 */
public class Gzip {

    // No instances
    private Gzip() {
    }

    /**
     * Compresses a byte-array using the Java default implementation of GZIP
     *
     * @param data The data to compress
     * @return The compressed data
     * @throws IOException If compression failed
     */
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    /**
     * Decompresses a byte-array using the Java default implementation of GZIP.
     * This method was designed to be used with {@link Gzip#compress(byte[])}
     *
     * @param compressed The compressed data to decompress
     * @return The decompressed data
     * @throws IOException If decompression failed
     */
    public static byte[] decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        return IOUtils.toByteArray(gis);
    }

}