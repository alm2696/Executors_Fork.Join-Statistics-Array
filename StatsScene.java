package mod08_OYO_02;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * This StatsScene class makes a JavaFX scene for displaying statistics of a dataset. It provides input 
 * fields for array size and thread count, and buttons to compute results using Executors or Fork/Join.
 * 
 * @author angel
 */
public class StatsScene {

	private TextField textSize = new TextField();  // TextField for entering the array size
	private TextField textThreadCount = new TextField();  // TextField for entering the thread count
	private TextArea output = new TextArea();  // TextArea for displaying computed statistics
	private StatsArray stats;  // Instance of StatsArray that holds the data and computation logic

	/**
	 * Constructor to initialize the StatsScene with a StatsArray.
	 * @param array The StatsArray object used to perform calculations and hold the data.
	 */
	public StatsScene(StatsArray array) {
		this.stats = array;
	}

	/**
	 * Builds the layout for the JavaFX scene.
	 * @return A GridPane containing all the UI elements.
	 */
	public GridPane getLayout() {
		// Create and configure the GridPane layout
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));  // Padding around the grid
		grid.setVgap(8);  // Vertical gap between components
		grid.setHgap(10);  // Horizontal gap between components

		// Create labels and input fields for array size and thread count
		Label labelSize = new Label("Array Size:");
		grid.add(labelSize, 0, 0);  // Add label for size
		grid.add(textSize, 1, 0);  // Add text field for size

		Label labelThreadCount = new Label("Thread Count:");
		grid.add(labelThreadCount, 0, 1);  // Add label for thread count
		grid.add(textThreadCount, 1, 1);  // Add text field for thread count

		// Create buttons for Executors and Fork/Join computation
		Button buttonComputeExecutor = new Button("Executors Compute");
		Button buttonComputeForkJoin = new Button("Fork/Join Compute");

		// Set up actions for each button
		setUpButton(buttonComputeExecutor, "Executors Compute", () -> {
			int size = Integer.parseInt(textSize.getText());
			int threadCount = Integer.parseInt(textThreadCount.getText());
			stats.create(size);  // Create an array of the specified size
			stats.populateWithExecutors(threadCount);  // Populate array using Executors
			stats.computeWithExecutors(threadCount);  // Compute statistics using Executors
		});

		setUpButton(buttonComputeForkJoin, "Fork/Join Compute", () -> {
			int size = Integer.parseInt(textSize.getText());
			stats.create(size);  // Create an array of the specified size
			stats.populateWithForkJoin();  // Populate array using Fork/Join
			stats.computeWithForkJoin();  // Compute statistics using Fork/Join
		});

		// Add buttons to the layout
		grid.add(buttonComputeExecutor, 0, 2, 2, 1);  // Executors button spanning 2 columns
		grid.add(buttonComputeForkJoin, 0, 3, 2, 1);  // Fork/Join button spanning 2 columns

		// Set properties for output area and add it to the layout
		output.setPrefRowCount(10);  // Set preferred number of rows for the output
		grid.add(output, 0, 4, 2, 1);  // Add output TextArea spanning 2 columns

		return grid;  // Return the GridPane layout
	}

	/**
	 * Sets up a button with the specified action and error handling.
	 * @param button The button to configure.
	 * @param label  The label for the button.
	 * @param task   The task to execute when the button is pressed.
	 */
	private void setUpButton(Button button, String label, Runnable task) {
		button.setOnAction(event -> {
			try {
				int size = Integer.parseInt(textSize.getText());  // Get the array size
				int threadCount = Integer.parseInt(textThreadCount.getText());  // Get the thread count

				// Validate the input values
				if (size <= 0 || threadCount <= 0) {
					logMessage("ERROR: Size and thread count must be positive integers.");
					return;  // Exit if the inputs are invalid
				}

				stats.create(size);  // Create the array of the specified size

				long startTime = System.currentTimeMillis();  // Record start time
				task.run();  // Run the specified task
				long endTime = System.currentTimeMillis();  // Record end time

				showResults(endTime - startTime);  // Display the results with elapsed time
			} catch (NumberFormatException e) {
				logMessage("ERROR: Enter valid integer values for size and thread count.");
			} catch (Exception e) {
				logMessage("ERROR: " + e.getMessage());  // Catch any other exceptions
			}
		});
	}

	/**
	 * Displays the computed statistics in the output area.
	 * @param timeElapsed The time in milliseconds that the computation took.
	 */
	private void showResults(long timeElapsed) {
		output.clear();  // Clear the output area
		output.appendText("Minimum: " + stats.getMin() + "\n");  // Display minimum
		output.appendText("Maximum: " + stats.getMax() + "\n");  // Display maximum
		output.appendText("Mean: " + stats.getMean() + "\n");  // Display mean
		output.appendText("Time Elapsed: " + timeElapsed + " ms\n");  // Display time elapsed
	}

	/**
	 * Displays an error message in an alert dialog.
	 * @param message The error message to display.
	 */
	private void logMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");  // Set alert title
		alert.setHeaderText("An error occurred");  // Set header text
		alert.setContentText(message);  // Set the content text of the alert
		alert.showAndWait();  // Show the alert and wait for the user to acknowledge it
	}
}
