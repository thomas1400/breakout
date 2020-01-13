# Game Plan
## Thomas Owens


### Breakout Variant

I found the Jet Ball variant interesting. The diverse brick shapes, 
placements, and movements can create satisfying chains of bounces and power-up
activations that keep the player engaged and excited. The diversity
of level design that you find as you progress through the game also provides
sustained novelty.

### General Level Descriptions

In my variant, I'd like to borrow some of the ideas used in the Jet Ball variant: varied brick movement
and placements that create chained power-ups and bounces. I'm planning to follow the
theme of "alien invasion" and create a variety of bricks that represent aliens, planets, and
asteroids.

The game will progress in six stages, which represent the journey of a lone space
pilot to a hostile alien planet and mothership. Three of the stages are pre-set,
while the stages in between are unique and randomly generated.

*Pre-set Level 1:*

Multicolored alien ships and asteroids orbit a central, immobile planet, which has greater
durability than other bricks and takes an increased number of hits to break.

*Pre-set Level 2:*

Asteroids with few alien ships interspersed move across the screen as in an asteroid belt.
When bricks leave one side of the screen, they reappear on the other side.

*Pre-set Level 3:*

A swarm of alien ships slowly descend towards the player as they guard a large 
alien mothership, which has increased durability and periodically spawns 
new alien ships.

### Bricks Ideas

Generic alien ships and asteroids are the standard bricks, which take a single hit
to break and yield no power-ups.

Special, marked alien ships will yield power-ups and increased points.

The large, planet brick will have increased durability and absorb more hits before
breaking. Breaking this brick will give greatly increased points.

The alien mothership is a large, planet-sized brick with increased durability. It
periodically spawns a small number of new alien ships, making defeating it harder
the longer it takes.

### Power-Up Ideas

The power-ups dropped by marked alien ships will include a barrage of 
rockets fired from the player's platform, an explosion centered at the
marked alien ship, and spawning a second (or third, fourth, etc.) ball.

### Cheat Key Ideas

Pressing 'b' allows the player to affect the velocity of the ball directly 
using the four arrow keys, moving it around and aiming it towards bricks. Pressing
it again switches control back to the platform.

Pressing 's', closely followed by 'd', causes a random alien ship to self destruct.

Pressing '<' or '>' makes the ball smaller or larger, respectively, increasing or
decreasing the difficulty of the game.

### Something Extra

Between the three pre-set levels, I plan to add randomly generated levels with a
combination of aliens and asteroids. In these levels, I hope to use a space
partitioning algorithm to divide the screen into sections. In each section will
be a brick with a randomly determined movement pattern. This will ensure that each
run of the game is unique and interesting.