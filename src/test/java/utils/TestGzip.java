package utils;

import dev.roundtable.beehoven.utils.Gzip;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class TestGzip {

    private static final byte[] DATA = new byte[]{13, 5, 3, 25, 5, 3, 74, 3};

    @Test
    public void testBase64() {
        Assert.assertEquals(Base64.getEncoder().encodeToString("test string".getBytes(StandardCharsets.UTF_8)), "dGVzdCBzdHJpbmc=");
        Assert.assertEquals(new String(Base64.getDecoder().decode("dGVzdCBzdHJpbmc="), StandardCharsets.UTF_8), "test string");
    }

    @Test
    public void testCompression() throws IOException {
        Assert.assertEquals(Base64.getEncoder().encodeToString(Gzip.compress(DATA)), "H4sIAAAAAAAAAONlZZZkZfZiBgAc2nzhCAAAAA==");
    }

    @Test
    public void testDecompression() throws IOException {
        Assert.assertTrue(Objects.deepEquals(DATA, Gzip.decompress(Gzip.compress(DATA))));
    }

}
