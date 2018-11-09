package com.jzoom.zoom.common;

import com.jzoom.zoom.common.async.AsyncServiceTest;
import com.jzoom.zoom.common.codec.CodecTest;
import com.jzoom.zoom.common.config.PropertiesConfigReaderTest;
import com.jzoom.zoom.common.decrypt.TestDecrypt;
import com.jzoom.zoom.common.filtter.PatternFilterFactoryTest;
import com.jzoom.zoom.common.json.JSONTest;
import com.jzoom.zoom.common.utils.OrderedListTest;
import com.jzoom.zoom.common.utils.ProcessUtilTest;
import com.jzoom.zoom.common.utils.ValidateUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CodecTest.class,PropertiesConfigReaderTest.class,TestDecrypt.class,PatternFilterFactoryTest.class,
        JSONTest.class, OrderedListTest.class, ValidateUtilTest.class, ProcessUtilTest.class, AsyncServiceTest.class})
public class AllTests {

}
