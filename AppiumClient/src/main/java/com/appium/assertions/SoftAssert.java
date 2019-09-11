package com.appium.assertions;

import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;

import com.appium.utils.Screenshots;

public class SoftAssert extends org.testng.asserts.SoftAssert {
	private static final Logger logger = LogManager.getLogger(SoftAssert.class.getName());

	@SuppressWarnings("rawtypes")
	private final Map<AssertionError, IAssert> m_errors = Maps.newHashMap();

	public SoftAssert() {
		m_errors.clear();
	}

	@Override
	public void executeAssert(@SuppressWarnings("rawtypes") IAssert a) {
		try {
			a.doAssert();
		} catch (AssertionError ex) {
			m_errors.put(ex, a);
			onAssertFailure(a, ex);
		}
	}

	@Override
	public void assertAll() {
		if (!m_errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			int i = 1;
			for (Map.Entry<AssertionError, IAssert> ae : m_errors.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append("\n ");
				}
				sb.append(i + "." + ae.getValue().getMessage() + "\n");
			}

			throw new AssertionError(sb.toString());

		}
	}

	@Override
	public void onAssertFailure(IAssert a, AssertionError ex) {
		try {
			logger.info("VERIFICATION FAILED: " + ex.getMessage());
			logger.info("Expected: " + a.getExpected());
			logger.info("Actual: " + a.getActual());
			String sScreenshotPath = Screenshots.takeSnapShotAndRetPath(Screenshots.driver);
			logger.info("Snapshot Path :<a href='" + sScreenshotPath + "'>" + sScreenshotPath + "</a>\n");
			m_errors.put(ex, a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isEqual(int iExpected, int iActual) {
		boolean flag = false;

		if (iExpected == iActual) {
			flag = true;
		}
		return flag;
	}

	public void clearSoftAssertMap() {
		m_errors.clear();
		logger.info("Soft Assert map cleaned");
	}
}