package org.zoomdev.zoom.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.zoomdev.zoom.common.codec.CodecTest;
import org.zoomdev.zoom.common.codec.TestBcd;
import org.zoomdev.zoom.common.codec.TestHash;
import org.zoomdev.zoom.common.codec.TestHex;
import org.zoomdev.zoom.common.config.PropertiesConfigReaderTest;
import org.zoomdev.zoom.common.config.TestConfigReader;
import org.zoomdev.zoom.common.decrypt.TestDecrypt;
import org.zoomdev.zoom.common.filtter.PatternFilterFactoryTest;
import org.zoomdev.zoom.common.json.JSONTest;
import org.zoomdev.zoom.common.pattern.TestPatternUtils;
import org.zoomdev.zoom.common.record.TestRecord;
import org.zoomdev.zoom.common.res.TestRes;
import org.zoomdev.zoom.common.utils.OrderedListTest;
import org.zoomdev.zoom.common.utils.ProcessUtilTest;
import org.zoomdev.zoom.common.utils.ValidateUtilTest;
import org.zoomdev.zoom.common.validate.ValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({TestRes.class,
        CodecTest.class,
        PropertiesConfigReaderTest.class,
        TestDecrypt.class,
        PatternFilterFactoryTest.class,
        JSONTest.class,
        OrderedListTest.class,
        ValidateUtilTest.class,
        ProcessUtilTest.class,
        TestRecord.class,
        TestBcd.class,
        TestHex.class,
        TestHash.class,
        TestConfigReader.class,
        ValidatorTest.class,
        TestPatternUtils.class})
public class AllTests {

}
