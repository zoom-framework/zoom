import com.jzoom.zoom.ioc.IocObject;
import com.jzoom.zoom.ioc.impl.ZoomIoc;
import com.jzoom.zoom.ioc.impl.ZoomIocKey;
import junit.framework.TestCase;

public class Test  extends TestCase {

    public void test(){
        ZoomIoc ioc = new ZoomIoc();
        IocObject a = ioc.get(new ZoomIocKey(A.class));
        IocObject b = ioc.get(new ZoomIocKey(B.class));
    }
}
