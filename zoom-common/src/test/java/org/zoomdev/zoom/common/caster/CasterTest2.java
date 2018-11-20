package org.zoomdev.zoom.common.caster;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.res.ResScanner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CasterTest2 extends TestCase {

    public void testDate(){

        Date date = Caster.to("20001010",Date.class);
        assertEquals( new SimpleDateFormat("yyyyMMdd").format(date),"20001010");

        date = Caster.to("2000101010",Date.class);
        assertEquals( new SimpleDateFormat("yyyyMMddHH").format(date),"2000101010");

        date = Caster.to("2000-10-10 10:10:10",Date.class);
        assertEquals( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date),"2000-10-10 10:10:10");

        date = Caster.to("2000-10-10 10:10:10:123",Date.class);
        assertEquals( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(date),"2000-10-10 10:10:10:123");

    }


    public void testFile() throws IOException {

        ResScanner scanner = ResScanner.me();
        scanner.scan();
        List<ResScanner.Res> resList = scanner.findFile("*");
        File file = resList.get(0).getFile();
        Caster.to(file,byte[].class);

    }
}
