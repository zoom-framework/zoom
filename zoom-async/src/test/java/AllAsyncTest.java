import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zoomdev.zoom.async.impl.AsyncServiceTest;
import org.zoomdev.zoom.async.impl.AsyncsTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        AsyncsTest.class,
        AsyncServiceTest.class
})
public class AllAsyncTest {


}
