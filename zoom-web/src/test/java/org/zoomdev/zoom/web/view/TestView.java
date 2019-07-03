package org.zoomdev.zoom.web.view;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.DataObject;
import org.zoomdev.zoom.web.action.MockHttpServletResponse;
import org.zoomdev.zoom.web.view.impl.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class TestView extends TestCase {


    public void test() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        StringView view = new StringView(
                "content"
        );
        view.render(response);
        assertEquals(response.getContent(), "content");

        ResScanner scanner = new ResScanner();
        scanner.scan();

        List<ResScanner.Res> list = scanner.findFile("*");

        File file = list.get(0).getFile();
        response = new MockHttpServletResponse();
        FileView fileView = new FileView(file);
        fileView.render(response);

        assertTrue(Arrays.equals(response.getBytes(), Io.readBytes(file)));


        response = new MockHttpServletResponse();
        BytesView bytesView = new BytesView(
                Io.readBytes(file), "1.txt"
        );
        bytesView.render(response);
        assertTrue(Arrays.equals(response.getBytes(), Io.readBytes(file)));

        response = new MockHttpServletResponse();
        final FileInputStream inputStream = new FileInputStream(file);
        OutputStreamView outputStreamView = new OutputStreamView() {
            @Override
            protected void close() throws IOException {
                inputStream.close();
            }

            @Override
            protected String getName() {
                return "test.txt";
            }

            @Override
            protected void writeTo(OutputStream outputStream) throws IOException {

                Io.copy(inputStream, outputStream);

            }
        };


        outputStreamView.render(response);

        response = new MockHttpServletResponse();
        JsonView jsonView = new JsonView(
                DataObject.as(
                        "id", 1,
                        "name", "张三"
                )
        );

        jsonView.render(response);

        assertEquals(response.getContent(), JSON.stringify(
                DataObject.as(
                        "id", 1,
                        "name", "张三"
                )
        ));


    }

}
