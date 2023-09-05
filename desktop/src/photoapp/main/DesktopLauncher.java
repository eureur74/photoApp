package photoapp.main;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setDecorated(false);
		// config.setHdpiMode(HdpiMode.Logical);

		config.setMaximized(true);
		config.setResizable(false);
		// config.setWindowIcon("icon.png");
		// config.setWindowedMode(1920, 1080);
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setTitle("PhotoApp");

		new Lwjgl3Application(new Main(), config);
	}
}
