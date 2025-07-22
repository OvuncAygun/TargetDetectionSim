This project is a project that simulates an environment with enemies and observer friendlies moving around it. The goal of this project is to simulate a radar on top of a vehicle that finds, identifies, and tracks entities in its range.
This simulation is designed using several different modules. Each enemy and observer is a seperate components that connects to the server using TCP/IP sockets.
The server component has the server and GUI modules that work as a single component even though they are programmed as seperate modules for modularity.
The enemy component has the enemy module that simulates the semi-random movement of an entity without a radar on it.
The observer component has the observer module which works similarly to the enemy in movement. But it also has a radar that finds, identies, and tracks entities by communicating with the server.

To run this simulation project, the user must follow the steps down below.

1. Configure the module project libraries or the global libraries so that the JavaFX library is recognized as "javafx" in the project. This step can different in different environments or IDEs. So, this step may be needed to be figured out by the user.
The solution used while developing this project was using IntelliJ IDEA's "Project Structure" settings to add the "javafx" library as a global library. After this is done, this library was added as a dependency from the "modules" tab in the same menu to the "GUI" module.

2. Run the "GUI" module's "GUI" file using the "--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics --enable-native-access=javafx.graphics" argument where, "${PATH_TO_FX}" must be replaced with the absulute path the the javafx library's "lib" folder if "PATH_TO_FX" is not set as an environmental variable with this absulute path as its value. If it is set as one, "${PATH_TO_FX}" can used in this argument without changing it.

3. Run the "enemy" or "observer" modules' "Main" files as many times as wanted. Each instance of these programs will create a new instance of a enemy or observer.

4. To end the simulation, close the server's GUI application window. This will automatically make the server shutdown, which will cause all of the clients to shutdown as well.
