# Walker

Walker is a basic 3D raycasting engine. I made this project mostly for fun and also to better learn OpenGL.

## Features

- 3D rendering using raycasting + trig.
- A minimap (press `k` to view).
- A scary monster with eyes that chases you around (very broken).
- JSON assets & levels.

## How to Run

1. Ensure you have JDK 25 or higher installed.
2. Clone the repository.
3. Run the application using Gradle:
   ```bash
   ./gradlew run
   ```
4. Or build using Gradle:
   ```bash
   ./gradlew build
   ```
5. Then run the fat jar produced!

## Controls

- Move forwards: `W`.
- Move backwards: `S`.
- Turn left: `A`.
- Turn right: `D`
- Toggle minimap view: `K`.