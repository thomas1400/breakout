# Design Analysis
## Thomas Owens


what are the project's design goals, specifically what kinds of new features did you want to make easy to add
describe the high-level design of your project, focusing on the purpose and interaction of the core classes
what assumptions or decisions were made to simplify your project's design, especially those that affected adding required features
describe, in detail, how to add new features to your project, especially ones you were not able to complete by the deadlin

### Design Goals

I wanted to make it easy to add new levels as desired. I did this by filling in any "empty" levels with randomly generated ones, allowing the creator to extend the existing levels with new ones while filling in any intermediate levels automatically.

I also made it relatively easy to add new PowerUp and Brick types, just by defining them in the LevelInterpreter class I created to interpret new levels, although I could have certainly improved this design with Inheritance and subclasses.

### High-Level design

The core class of this program is Main. Main runs the game loop and handles all game logic by getting important fields and properties from the Ball, Brick, and PowerUp classes. It tells each object when to update its position and how to update its velocity. It keeps track of the player's score, lives, and all other important information for the game. It also controls the GUI. This class really does too much, and I could have separated it into multiple, more purposeful classes, since this class's purpose is currently to handle almost everything for the game.

The Ball class holds the ball's image, bounds, position and velocity information. It can update its own position according to its velocity, and updates its velocity with given parameters from the Main class.

The Brick class, similarly, holds the brick's image, bounds and position, along with its durability, power-up drop, and score. It can update the brick's durability on a hit and move the brick off-screen when it's broken.

The PowerUp class is a generic class for a power-up that holds an image, bounds, position, and type. It retrieves its own image based on its type and moves itself based on a predetermined downward velocity. It's not a very active class, given that PowerUps are not very active and rely on functionality and variables from Main to do most things.

### Assumptions/Decisions

I decided part of the way through my project that my initial plan to make the bricks move in interesting patterns would be infeasible for me to complete in the limited time available, so I quickly scrapped the idea in favor of having a more fully functional, albeit less interesting game. With the time I saved by cutting this feature, I fine-tuned the ball's bouncing algorithm so that it was much less buggy and bounced in a more realistic way off the bricks.

I also decided against adding asteroids and other brick classes in the interest of time, although I would have liked to add more diversity in my levels.

### Adding New Features

There are generally four classes of features to add to the project, at this point:

- New Brick Types: To add new brick types, you have to edit the LevelInterpreter class to include the new type's characteristics, including image, durability, score, and possible power-up drops. You could then include the new brick as a character in any level layout file that you load.
- New PowerUps: To add a new power-up, you would similarly have to edit the PowerUp class to include the new power-up's type number, along with its image. You would then have to add the code to activate the power-up into Main.
- New Levels: Adding new levels is comparatively easy. All you have to do is add a new "level#layout.txt" file in resources, then change the maximum level constant in Main to reflect the addition. The new level would be automatically loaded. You would also have to change the cheat keys that skip to specific levels to reflect the new addition.
- Brick Movement: To add movement to the bricks, I imagine you would have to use JavaFX's animations or transitions, which include a transition that moves an object along a path. You would have to manually determine many of these paths by creating the corresponding shape objects, which would be an arduous task. Then, you would have to update the ball's bouncing algorithm so that its velocity is updated with the velocity of the brick it collides with, as in an elastic collision.