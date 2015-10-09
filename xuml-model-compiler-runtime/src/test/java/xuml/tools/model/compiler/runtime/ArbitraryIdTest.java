package xuml.tools.model.compiler.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class ArbitraryIdTest {

    @Test
    public void testLength() {
        assertEquals(32, ArbitraryId.next().length());
    }

    @Test
    public void testConstructorIsPrivate() {
        Asserts.assertIsUtilityClass(ArbitraryId.class);
    }
}
