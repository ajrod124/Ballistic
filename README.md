# Ballistic!

## Installation

Make sure you have eclipse with a Gradle plugin before importing the project.

1. Clone repo

In Eclipse...

2. File > Import > Existing file into workspace
3. Select cloned repo destination as root directory for project
4. Make sure all 5 projects are checked and click "Finish"
5. For each project...
  1. Right click the project in the side bar and Configure > Convert to Gradle project
  2. Right click the project in the side bar and Gradle > Refresh all

## Testing

1. Open DesktopLauncher.java in Ballistic-desktop
2. Run

To effectively test changes...

(Any changes to game objects can be effectively tested by making an instance of the class in TestState.java)

1. Comment out line 57 in MenuState.java
2. Uncomment line 58 in MenuState.java
3. Set the argument of gsm.set() to the state currently being tested

To enable frame advance mode...

1. Uncomment the IF statement in line 24 of GSM.java
