package yolo.octo.dangerzone;

public class Application {
	private static android.app.Application app = null;
	public static android.app.Application get() {
		return app;
	}
	public static void set(android.app.Application a) {
		app = a;
	}
}
