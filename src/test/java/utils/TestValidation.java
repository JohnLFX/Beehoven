package utils;

import dev.roundtable.beehoven.utils.ValidationUtil;
import org.apache.commons.text.RandomStringGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the methods found in {@link ValidationUtil}
 */
@SuppressWarnings("ConstantConditions")
public class TestValidation {

    private static final RandomStringGenerator STRING_GENERATOR;

    static {
        STRING_GENERATOR = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
    }

    @Test
    public void testName() {

        Assert.assertFalse(ValidationUtil.checkName(null));
        Assert.assertFalse(ValidationUtil.checkName(""));
        Assert.assertFalse(ValidationUtil.checkName(STRING_GENERATOR.generate(128)), "Name is not checking the right string length");

        Assert.assertTrue(ValidationUtil.checkName("Joseph John"));

    }

    @Test
    public void testUsername() {

        Assert.assertFalse(ValidationUtil.checkUsername(null));
        Assert.assertFalse(ValidationUtil.checkUsername(""));
        Assert.assertFalse(ValidationUtil.checkUsername("_" + STRING_GENERATOR.generate(3) + "**?!"));
        Assert.assertFalse(ValidationUtil.checkUsername(STRING_GENERATOR.generate(17)), "Username should be a max of 16 characters (same standards as Twitter)");

        Assert.assertTrue(ValidationUtil.checkUsername(STRING_GENERATOR.generate(5)));
        Assert.assertTrue(ValidationUtil.checkUsername(STRING_GENERATOR.generate(8) + "123"));

    }

    @Test
    public void testEmail() {

        Assert.assertFalse(ValidationUtil.checkEmail(null));
        Assert.assertFalse(ValidationUtil.checkEmail(""));
        Assert.assertFalse(ValidationUtil.checkEmail(STRING_GENERATOR.generate(10)));
        Assert.assertFalse(ValidationUtil.checkEmail("webmaster@roundtable"));

        Assert.assertTrue(ValidationUtil.checkEmail("webmaster@roundtable.dev"));

    }

    @Test
    public void testPassword() {

        Assert.assertFalse(ValidationUtil.checkPassword(null));
        Assert.assertFalse(ValidationUtil.checkPassword(""));
        Assert.assertFalse(ValidationUtil.checkPassword(STRING_GENERATOR.generate(5)));

        Assert.assertTrue(ValidationUtil.checkPassword(STRING_GENERATOR.generate(6, 15)));
        Assert.assertTrue(ValidationUtil.checkPassword(STRING_GENERATOR.generate(6)));
        Assert.assertTrue(ValidationUtil.checkPassword(STRING_GENERATOR.generate(24)));

    }

}
