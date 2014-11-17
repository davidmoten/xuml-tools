package ${package};

import xuml.tools.util.database.DerbyUtil;
import test.A;
import test.B;
import test.Context;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {
	
	
	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		App.startup();
	}
	
	@Test
	public void test1() {
		// your test goes here
	}

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

	
}