import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zoomdev.zoom.async.impl.AsyncModuleTest;
import org.zoomdev.zoom.async.impl.AsyncServiceTest;
import org.zoomdev.zoom.async.impl.AsyncsTest;
import org.zoomdev.zoom.async.modules.AsyncModule;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        AsyncsTest.class,
        AsyncServiceTest.class,
        AsyncModuleTest.class
})
public class AllAsyncTest {


}
