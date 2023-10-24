<p align="center"><img src="https://raw.githubusercontent.com/Worker20/CreateRobotics-1.18/master/icon.png" alt="Logo" width="200"></p>
<h1 align="center">Create Robotics<br>
	<a href="https://files.minecraftforge.net/"><img src="https://img.shields.io/badge/Loader-Forge-orange?style=flat&logo=curseforge"></a>
	<a href="https://discord.gg/PbBt4PQhpe"><img src="https://img.shields.io/discord/865321790347018241?color=5865f2&label=Discord&style=flat" alt="Discord"></a>
	<br><br>
</h1>

<a href="https://discord.com/invite/create-robotics"><img src="https://discordapp.com/api/guilds/865321790347018241/widget.png?style=banner3" alt="Discord Banner" align="right"/></a>

**Create Robotics** is a create addon that adds robotics and their automation to Minecraft. 

Players can program the robots with our own scripting-language called RoboScript, designed to be user-friendly, simple, but still powerful.

This allows the player to do many things with the robots, whether automating moving items from A to B, building a quarry, or simply using them to automate a mob grinder. 

The mod adds almost infinite ways to automate with the robots!


For help with RoboScript you can look <a href="https://github.com/Worker20/CreateRobotics/wiki/Programming-Mechanic">here</a>.


# New Version of RoboScript concept, currently being worked on
## Variable Declaration
Variables can be declared using the `var` keyword, initialized with an `=` following the name and an expression afterwards, and new to RoboScript, can be set to a specific type.
```go
var my_variable; // declare variable with the default 'any?' type; allowed to be null
var my_variable: any = 0; // redeclare my_variable; not allowed to be null and must be defined
var my_number: number; // variable can only be set to numbers; starts at 0
var my_string: string; // variable can only be set to string; starts at ""
var my_string: bool; // variables can only be set to true or false; starts at false

var redstone_detector: RedstoneDetector; // it even works for classes! (classes must be defined beforehand)
```
*Note: Declaring a type does not currently improve the speed of your program, it only helps with catching possible runtime errors in the compiler.*
### Type safety
Using strict types in RoboScript is essentially a yes or no choice, as the compiler is very strict with how types are used and because type safety is entirely compiler based, an `as` keyword or typecasting does not exist.
### Null safety
Variables by default cannot be `null`, and the compiler will throw an error if it encounters a variable may be `null`. Placing a `?` in front of the variables type will allow it to be `null`.
```go
var my_nullable_number: number? = null;
var my_number: number = null; // ERROR: Variable my_number is a non-null 'number' type.
```
## If Statements
An `if` statement can be created with this simple syntax:
```rust
if cash >= 15 {
	println("Can afford.");
} else {
	println("Cannot afford, sorry.");
}
```
`if`/`else` statements can be chained together:
```rust
if cash >= 30 {
	println("Can afford more than two.");
} elif cash >= 15 { // 'elif' can also be replaced with 'else if'
	println("Can afford one.");
} else {
	println("Cannot afford any, sorry.");
}
```
## While Statements
A `while` statement can be created exactly like an `if` statement, except they cannot have `else` blocks.
```rust
while cash >= 15 {
	cash -= 15; // continues to loop until cash is less than 15
}
```
## Loop Statements
A `loop` statement simply loops the inside of itself forever, until it is stopped by either a `return` or a `break`.
```rust
loop {
	move_over();
	if detect_block(Directions::NORTH).id == "obsidian" { break; }
}
```
## Iterable Types
Here are all the iterable types in RoboScript, that can be used in things like `for` loops.
Some iterable types can be indexed using `[]` too.
```js
var my_range: range = 0..3; // cannot be indexed; 0, 1, 2
var my_exact_range: range = 0..=3; // 0, 1, 2, 3
var my_list: list = [0,1,2]; // can be indexed; my_list[0] == 0
var my_string: string = "Hi!"; // can be indexed; my_string[0] == "H"
```
## For Statements
A `for` statement loops over elements in an iterable type or a range of integers.
```rust
for character in "Hello World" {
	println(character); // prints Hello World with a new line in between each character
}

for index in 0..10 {
	println(index); // prints the numbers 0-9
}
```
## Functions
A function can be defined with the `func` keyword, and can be called using parenthesis.
```go
func characters(): ? {
	for i in ord(" ")..=ord("~") {
		print(chr(i)); // prints every typeable character using ASCII without line breaks
	}
}

characters(); // calls the characters function
```
*Note: The `?` type when defining functions simply means the function is `void`, and will always return `null`.*

Functions are also objects, which means you can pass them in as arguments for functions and variables.
```go
func add(supplier: () -> number): number {
	return supplier() + 1;
}
```

more to be added ...
