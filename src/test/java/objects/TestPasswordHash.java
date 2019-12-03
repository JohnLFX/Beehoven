package objects;

import dev.roundtable.beehoven.objects.HashedPassword;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Base64;

public class TestPasswordHash {

    @Test
    public void test() throws Exception {

        byte[] salt = new byte[16];
        Arrays.fill(salt, (byte) 5);

        HashedPassword password = HashedPassword.hash("password", salt);

        String hash = Base64.getEncoder().encodeToString(password.getHash());

        Assert.assertEquals(hash, "eHfsVQHCT3KHChbFjLCUBA==");
        Assert.assertEquals(password.getSalt(), salt);

    }

}
