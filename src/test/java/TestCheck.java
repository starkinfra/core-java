import com.starkcore.Settings;
import com.starkcore.utils.Check;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;


public class TestCheck {

    private Integer originalTimeout;

    @Before
    public void setUp() {
        originalTimeout = Settings.timeout;
    }

    @After
    public void tearDown() {
        Settings.timeout = originalTimeout;
    }

    @Test
    public void testSetSettingsTimeout() throws Exception {
        Settings.timeout = 30;
        Integer result = Check.timeout(15);
        Assert.assertEquals(Integer.valueOf(30), result);
    }

    @Test
    public void testSetSettingsTimeoutNull() throws Exception {
        Settings.timeout = null;
        Integer result = Check.timeout(15);
        Assert.assertEquals(Integer.valueOf(15), result);
    }

    @Test(expected = Exception.class)
    public void testTimeoutIsZero() throws Exception {
        Settings.timeout = 0;
        Check.timeout(15);
    }

    @Test(expected = Exception.class)
    public void testTimeoutIsNegative() throws Exception {
        Settings.timeout = -5;
        Check.timeout(15);
    }
}