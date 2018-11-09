import com.jzoom.zoom.common.annotations.Inject;

public class B {

	
	@Inject
	private A a;
	
	public B() {
		System.out.println("Create B");
	}
}
