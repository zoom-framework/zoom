import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.impl.SimpleIocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;
import junit.framework.TestCase;

public class Test  extends TestCase {

    public void test(){
        SimpleIocContainer ioc = new SimpleIocContainer();
        IocObject a = ioc.get(new ZoomIocKey(A.class));
        IocObject b = ioc.get(new ZoomIocKey(B.class));
    }
}
