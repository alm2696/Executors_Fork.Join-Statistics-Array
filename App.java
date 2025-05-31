package mod08_OYO_02;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The App class starts the application. It initializes and displays the main window 
 * for the Array Statistics application, which uses Executors and Fork/Join techniques.
 * 
 * @author angel
 */
public class App extends Application {

	/** StatsArray object to store and process statistics for the array. */
	private StatsArray stats = new StatsArray();

	/**
	 * The main method launches the JavaFX application.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);  // Launch the JavaFX application
	}

	/**
	 * Start method sets up the main window of the application. It
	 * initializes the window title, scene, and displays the main interface.
	 * 
	 * @param primaryStage the main stage for the application
	 */
	@Override
	public void start(Stage primaryStage) {
		// Set the title of the window
		primaryStage.setTitle("Array Statistics with Executors and Fork/Join");

		// Create the main view, passing the StatsArray object for interaction
		StatsScene view = new StatsScene(stats);

		// Set up the scene with the layout from StatsScene and size of the window
		Scene scene = new Scene(view.getLayout(), 400, 300);
		primaryStage.setScene(scene);  // Attach the scene to the stage
		primaryStage.show();  // Display the stage
	}
}
