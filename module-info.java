/**
 * This module defines the dependencies and exports for the project
 * related to JavaFX and any classes related to the package.
 * 
 * @author angel
 */
module edu.commonwealthu.alm2696.CMSC230 {

    // Requires JavaFX graphics and controls for the JavaFX GUI functionality
    requires transitive javafx.graphics;
    requires javafx.controls;

    // Exports the package to JavaFX graphics, allowing the package's classes to be used in JavaFX applications
    exports mod08_OYO_02 to javafx.graphics;
}
