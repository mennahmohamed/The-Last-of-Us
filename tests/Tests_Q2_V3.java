package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.junit.Test;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.characters.Character;
import model.characters.Direction;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class Tests_Q2_V3 {

	String characterPath = "model.characters.Character";
	String collectiblePath = "model.collectibles.Collectible";
	String vaccinePath = "model.collectibles.Vaccine";
	String supplyPath = "model.collectibles.Supply";
	String fighterPath = "model.characters.Fighter";

	private String charCell = "model.world.CharacterCell";
	private String cellPath = "model.world.Cell";
	private String collectibleCellPath = "model.world.CollectibleCell";
	private String trapCellPath = "model.world.TrapCell";
	private String enumDirectionPath = "model.characters.Direction";

	String gamePath = "engine.Game";
	String medicPath = "model.characters.Medic";
	String explorerPath = "model.characters.Explorer";
	String heroPath = "model.characters.Hero";

	String gameActionExceptionPath = "exceptions.GameActionException";
	String invalidTargetExceptionPath = "exceptions.InvalidTargetException";
	String movementExceptionPath = "exceptions.MovementException";
	String noAvailableResourcesExceptionPath = "exceptions.NoAvailableResourcesException";
	String notEnoughActionsExceptionPath = "exceptions.NotEnoughActionsException";

	String zombiePath = "model.characters.Zombie";
	String ladderPath = "model.collectibles.Ladder";

	public void generateGameWithCharacter(Character c1, Character c2, Point p1, Point p2)
			throws IllegalArgumentException, IllegalAccessException {
		Hero h = new Medic("Medic", 100, 10, 2);
		Game.startGame(h);
		for (int i = 0; i < Game.map.length; i++) {
			for (int j = 0; j < Game.map[i].length; j++) {
				Game.map[i][j] = new CharacterCell(null);
			}
		}

		Field f = null;

		try {
			f = Game.class.getDeclaredField("agents");
		} catch (NoSuchFieldException e) {
			assertFalse("Attribute agents not found.", true);
		} catch (SecurityException e) {
			assertFalse("Attribute agents not found.", true);
		}

		f.setAccessible(true);

		Game.heroes.clear();
		Game.zombies.clear();

		Game.map[p1.x][p1.y] = new CharacterCell(c1);
		Game.map[p2.x][p2.y] = new CharacterCell(c2);
		c1.setLocation(p1);
		c2.setLocation(p2);

		if (c1 instanceof Hero) {
			Game.heroes.add((Hero) c1);
		} else if (c1 instanceof Zombie) {
			Game.zombies.add((Zombie) c1);
		}

		if (c2 instanceof Hero) {
			Game.heroes.add((Hero) c2);
		} else if (c2 instanceof Zombie) {
			Game.zombies.add((Zombie) c2);
		}
	}

	public void testIsSwitchedToAtticHelper(Object hero, Cell[][] mapBefore, Cell[][] atticMapBefore,
			Cell[][] originalMapBefore) throws Exception {

		// Get the Game class using the provided path
		Class<?> gameClass = Class.forName(gamePath);

		// Get the updated values of 'map', 'atticMap', and 'originalMap'
		Field mapField = gameClass.getField("map");
		Cell[][] mapAfter = (Cell[][]) mapField.get(null);

		// Check if 'map' contains the cells from 'atticMap' and does not contain the
		// cells from 'originalMap'
		boolean isIdenticalNewAndOldMap = true;
		boolean isIdenticalNewAndAttic = true;
		boolean isIdenticalNewAndOriginal = true;
		boolean isIdenticalOriginalAndAttic = true;

		for (int row = 0; row < mapAfter.length; row++) {
			for (int col = 0; col < mapAfter[row].length; col++) {
				Cell cell = mapAfter[row][col];
				// if a single cell is not the same, they are not the same.

				if (!cell.equals(mapBefore[row][col])) {
					// System.out.println("Objection");
					isIdenticalNewAndOldMap = false;
				}

				if (!(cell.equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 2");
					isIdenticalNewAndAttic = false;
				}

				if (!(cell.equals(originalMapBefore[row][col]))) {
					// System.out.println("Objection 3");
					isIdenticalNewAndOriginal = false;
				}

				if (!(originalMapBefore[row][col].equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 4");
					isIdenticalOriginalAndAttic = false;
				}

			}
		}
		
		Field isInAtticField = gameClass.getDeclaredField("isInAttic");
		isInAtticField.setAccessible(true);
		boolean isInAttic = (boolean) isInAtticField.get(gameClass);

		// Cell is from atticMap
		assertFalse("The 'map' should change", isIdenticalNewAndOldMap);

		assertFalse("The 'map' should not contain cells from 'originalMap'", isIdenticalNewAndOriginal);

		assertFalse("The 'originalMap' should not contain cells from 'atticMap'", isIdenticalOriginalAndAttic);

		// Cell is not from atticMap
		assertTrue("The 'map' should contain cells from 'atticMap'", isIdenticalNewAndAttic);
		assertTrue("isAttic must be set to true",isInAttic);
	}

	public void testIsSwitchedToOriginalHelper(Object hero, Cell[][] mapBefore, Cell[][] atticMapBefore,
			Cell[][] originalMapBefore) throws Exception {

		// Get the Game class using the provided path
		Class<?> gameClass = Class.forName(gamePath);

		// Get the updated values of 'map', 'atticMap', and 'originalMap'
		Field mapField = gameClass.getField("map");
		Cell[][] mapAfter = (Cell[][]) mapField.get(null);

		// Check if 'map' contains the cells from 'atticMap' and does not contain the
		// cells from 'originalMap'
		boolean isIdenticalNewAndOldMap = true;
		boolean isIdenticalNewAndAttic = true;
		boolean isIdenticalNewAndOriginal = true;
		boolean isIdenticalOriginalAndAttic = true;

		for (int row = 0; row < mapAfter.length; row++) {
			for (int col = 0; col < mapAfter[row].length; col++) {
				Cell cell = mapAfter[row][col];
				// if a single cell is not the same, they are not the same.

				if (!cell.equals(mapBefore[row][col])) {
					// System.out.println("Objection");
					isIdenticalNewAndOldMap = false;
				}

				if (!(cell.equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 2");
					isIdenticalNewAndAttic = false;
				}

				if (!(cell.equals(originalMapBefore[row][col]))) {
					// System.out.println("Objection 3");
					isIdenticalNewAndOriginal = false;
				}

				if (!(originalMapBefore[row][col].equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 4");
					isIdenticalOriginalAndAttic = false;
				}

			}
		}
		
		Field isInAtticField = gameClass.getDeclaredField("isInAttic");
		isInAtticField.setAccessible(true);
		boolean isInAttic = (boolean) isInAtticField.get(gameClass);

		// Cell is from atticMap
		assertFalse("The 'map' should change", isIdenticalNewAndOldMap);

		assertTrue("The 'map' should contain cells from 'originalMap'", isIdenticalNewAndOriginal);

		assertFalse("The 'originalMap' should not contain cells from 'atticMap'", isIdenticalOriginalAndAttic);

		// Cell is not from atticMap
		assertFalse("The 'map' should not contain cells from 'atticMap'", isIdenticalNewAndAttic);
		assertFalse("IsInAttic must be set to false",isInAttic);
	}

	public void testIsNotSwitchedToOriginalHelper(Object hero, Cell[][] mapBefore, Cell[][] atticMapBefore,
			Cell[][] originalMapBefore) throws Exception {

		// Get the Game class using the provided path
		Class<?> gameClass = Class.forName(gamePath);

		// Get the updated values of 'map', 'atticMap', and 'originalMap'
		Field mapField = gameClass.getField("map");
		Cell[][] mapAfter = (Cell[][]) mapField.get(null);

		// Check if 'map' contains the cells from 'atticMap' and does not contain the
		// cells from 'originalMap'
		boolean isIdenticalNewAndOldMap = true;
		boolean isIdenticalNewAndAttic = true;
		boolean isIdenticalNewAndOriginal = true;
		boolean isIdenticalOriginalAndAttic = true;

		for (int row = 0; row < mapAfter.length; row++) {
			for (int col = 0; col < mapAfter[row].length; col++) {
				Cell cell = mapAfter[row][col];
				// if a single cell is not the same, they are not the same.

				if (!cell.equals(mapBefore[row][col])) {
					// System.out.println("Objection");
					isIdenticalNewAndOldMap = false;
				}

				if (!(cell.equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 2");
					isIdenticalNewAndAttic = false;
				}

				if (!(cell.equals(originalMapBefore[row][col]))) {
					// System.out.println("Objection 3");
					isIdenticalNewAndOriginal = false;
				}

				if (!(originalMapBefore[row][col].equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 4");
					isIdenticalOriginalAndAttic = false;
				}

			}
		}

		// Cell is from atticMap
		assertTrue("The 'map' should not change", isIdenticalNewAndOldMap);

		assertFalse("The 'map' should not contain cells from 'originalMap'", isIdenticalNewAndOriginal);

		assertFalse("The 'originalMap' should not contain cells from 'atticMap'", isIdenticalOriginalAndAttic);

		// Cell is not from atticMap
		assertTrue("The 'map' should contain cells from 'atticMap'", isIdenticalNewAndAttic);

	}

	public void testIsNotSwitchedToAtticHelper(Object hero, Cell[][] mapBefore, Cell[][] atticMapBefore,
			Cell[][] originalMapBefore) throws Exception {

		// Get the Game class using the provided path
		Class<?> gameClass = Class.forName(gamePath);

		// Get the updated values of 'map', 'atticMap', and 'originalMap'
		Field mapField = gameClass.getField("map");
		Cell[][] mapAfter = (Cell[][]) mapField.get(null);

		// Check if 'map' contains the cells from 'atticMap' and does not contain the
		// cells from 'originalMap'
		boolean isIdenticalNewAndOldMap = true;
		boolean isIdenticalNewAndAttic = true;
		boolean isIdenticalNewAndOriginal = true;
		boolean isIdenticalOriginalAndAttic = true;

		for (int row = 0; row < mapAfter.length; row++) {
			for (int col = 0; col < mapAfter[row].length; col++) {
				Cell cell = mapAfter[row][col];
				// if a single cell is not the same, they are not the same.

				if (!cell.equals(mapBefore[row][col])) {
					// System.out.println("Objection");
					isIdenticalNewAndOldMap = false;
				}

				if (!(cell.equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 2");
					isIdenticalNewAndAttic = false;
				}

				if (!(cell.equals(originalMapBefore[row][col]))) {
					// System.out.println("Objection 3");
					isIdenticalNewAndOriginal = false;
				}

				if (!(originalMapBefore[row][col].equals(atticMapBefore[row][col]))) {
					// System.out.println("Objection 4");
					isIdenticalOriginalAndAttic = false;
				}

			}
		}

		// Cell is from atticMap
		assertTrue("The 'map' should not change", isIdenticalNewAndOldMap);

		assertTrue("The 'map' should contain cells from 'originalMap'", isIdenticalNewAndOriginal);

		assertFalse("The 'originalMap' should not contain cells from 'atticMap'", isIdenticalOriginalAndAttic);

		// Cell is not from atticMap
		assertFalse("The 'map' should not contain cells from 'atticMap'", isIdenticalNewAndAttic);
	}

	public void ResetGame() {
		try {
			// Get the Game class using its path
			Class<?> gameClass = Class.forName(gamePath);
			
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			// Get the atticMap field from the Game class
			Field atticMapField = gameClass.getDeclaredField("atticMap");
			atticMapField.setAccessible(true);

			// Get the originalMap field from the Game class
			Field originalMapField = gameClass.getDeclaredField("originalMap");
			originalMapField.setAccessible(true);

			// Get the isInAttic field from the Game class
			Field isInAtticField = gameClass.getDeclaredField("isInAttic");
			isInAtticField.setAccessible(true);

			// Reset the atticMap and originalMap attributes to new Cell arrays
			atticMapField.set(null, new Cell[15][15]);
			mapField.set(null, new Cell[15][15]);
			originalMapField.set(null, mapField.get(gameClass));

			// Set the isInAttic attribute to false
			isInAtticField.set(null, false);

		} catch (ClassNotFoundException e) {
			System.err.println("The Game class was not found.");
		} catch (NoSuchFieldException e) {
			System.err.println("One or more required fields were not found in the Game class.");
		} catch (IllegalAccessException e) {
			System.err.println("Failed to access the Game class fields.");
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 1000)
	public void testAttributeAtticMapInGame() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the AtticMap attribute exists
			Field atticMapField = gameClass.getDeclaredField("atticMap");
			assertTrue("The atticMap attribute in the Game class should be static.",
					java.lang.reflect.Modifier.isStatic(atticMapField.getModifiers()));

			assertTrue("The atticMap attribute is missing in the Game class.", atticMapField != null);

			// Check if the field is of type Cell[][]
			Class<?> fieldType = atticMapField.getType();
			assertTrue("The atticMap attribute in the Game class should be of type Cell[][].",
					fieldType == Cell[][].class);

		} catch (ClassNotFoundException e) {
			fail("The Game class was not found.");
		} catch (NoSuchFieldException e) {
			fail("The atticMap attribute is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 1000)
	public void testAttributeOriginalMapInGame() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the AtticMap field is present
			Field atticMapField = gameClass.getDeclaredField("originalMap");

			assertTrue("The originalMap attribute in the Game class should be static.",
					java.lang.reflect.Modifier.isStatic(atticMapField.getModifiers()));

			assertTrue("The AtticMap attribute is missing in the Game class.", atticMapField != null);

			// Check if the field is of type Cell[][]
			Class<?> fieldType = atticMapField.getType();
			assertTrue("The originalMap attribute in the Game class should be of type Cell[][].",
					fieldType == Cell[][].class);

		} catch (ClassNotFoundException e) {
			fail("The Game class was not found.");
		} catch (NoSuchFieldException e) {
			fail("The originalMap attribute is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 1000)
	public void testAttributeIsInAtticInGame() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the AtticMap field is present
			Field atticMapField = gameClass.getDeclaredField("isInAttic");

			assertTrue("The isInAttic attribute in the Game class should be static.",
					java.lang.reflect.Modifier.isStatic(atticMapField.getModifiers()));

			assertTrue("The isInAttic attribute is missing in the Game class.", atticMapField != null);

			// Check if the field is of type Cell[][]
			Class<?> fieldType = atticMapField.getType();
			assertTrue("The isInAttic attribute in the Game class should be of type boolean.",
					fieldType == boolean.class);

		} catch (ClassNotFoundException e) {
			fail("The Game class was not found.");
		} catch (NoSuchFieldException e) {
			fail("The AtticMap attribute is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 3000)
	public void testStartGameInitialisesAttickInGame() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Check if the atticMap attribute is properly initialized
			Field atticMapField = gameClass.getDeclaredField("atticMap");
			atticMapField.setAccessible(true);

			atticMapField.set(null, new Cell[15][15]);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			Cell[][] atticMap = (Cell[][]) atticMapField.get(gameClass);

			assertNotNull("The atticMap attribute is not properly initialized.", atticMap);
			assertEquals("The atticMap attribute has an incorrect number of rows.", 15, atticMap.length);
			assertEquals("The atticMap attribute has an incorrect number of columns.", 15, atticMap[0].length);

			int collectibleCellCount = 0;
			int ladderCellCount = 0;

			for (Cell[] row : atticMap) {
				for (Cell cell : row) {
					if (cell instanceof CharacterCell) {
						CharacterCell characterCell = (CharacterCell) cell;
						assertNull("The CharacterCell's character should be null.", characterCell.getCharacter());
					} else if (cell instanceof CollectibleCell) {
						CollectibleCell collectibleCell = (CollectibleCell) cell;
						assertNotNull("The CollectibleCell's collectible should not be null.",
								collectibleCell.getCollectible());
						if (collectibleCell.getCollectible().getClass().getName().equals(ladderPath)) {
							ladderCellCount++;
						}
						collectibleCellCount++;
					}
				}
			}

			assertEquals("There should be exactly 1 CollectibleCell in the atticMap.", 1, collectibleCellCount);
			assertEquals("The CollectibleCell in the atticMap should be of type Ladder.", 1, ladderCellCount);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The startGame method is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 3000)
	public void testStartGameInitialisesLadderAtRandomLocation() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Check if the atticMap attribute is properly initialized
			Field atticMapField = gameClass.getDeclaredField("atticMap");
			atticMapField.setAccessible(true);

			atticMapField.set(null, new Cell[15][15]);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			Cell[][] atticMap = (Cell[][]) atticMapField.get(gameClass);

			assertNotNull("The atticMap attribute is not properly initialized.", atticMap);
			assertEquals("The atticMap attribute has an incorrect number of rows.", 15, atticMap.length);
			assertEquals("The atticMap attribute has an incorrect number of columns.", 15, atticMap[0].length);

			int collectibleCellCount = 0;
			int ladderCellCount = 0;

			int ladderCellRow = -1;
			int ladderCellColumn = -1;

			int expectedRow = -1;
			int expectedColumn = -1;

			for (int row = 0; row < atticMap.length; row++) {
				for (int column = 0; column < atticMap[row].length; column++) {
					Cell cell = atticMap[row][column];
					if (cell instanceof CharacterCell) {
						CharacterCell characterCell = (CharacterCell) cell;
						assertNull("The CharacterCell's character should be null.", characterCell.getCharacter());
					} else if (cell instanceof CollectibleCell) {
						CollectibleCell collectibleCell = (CollectibleCell) cell;
						assertNotNull("The CollectibleCell's collectible should not be null.",
								collectibleCell.getCollectible());
						if (collectibleCell.getCollectible().getClass().getName().equals(ladderPath)) {
							ladderCellCount++;
							expectedRow = row;
							expectedColumn = column;
						}
						collectibleCellCount++;
					}
				}
			}

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			atticMap = (Cell[][]) atticMapField.get(gameClass);

			for (int row = 0; row < atticMap.length; row++) {
				for (int column = 0; column < atticMap[row].length; column++) {
					Cell cell = atticMap[row][column];
					if (cell instanceof CharacterCell) {
						CharacterCell characterCell = (CharacterCell) cell;
						assertNull("The CharacterCell's character should be null.", characterCell.getCharacter());
					} else if (cell instanceof CollectibleCell) {
						CollectibleCell collectibleCell = (CollectibleCell) cell;
						assertNotNull("The CollectibleCell's collectible should not be null.",
								collectibleCell.getCollectible());
						if (collectibleCell.getCollectible().getClass().getName().equals(ladderPath)) {
							ladderCellCount++;
							ladderCellRow = row;
							ladderCellColumn = column;
						}
						collectibleCellCount++;
					}
				}
			}

			assertEquals("There should be exactly 1 CollectibleCell in the atticMap.", 2, collectibleCellCount);
			assertEquals("The CollectibleCell in the atticMap should be of type Ladder.", 2, ladderCellCount);

			// Check if the ladder cell is placed at a random location
			assertTrue("The CollectibleCell with a ladder is not placed at a random location.",
					expectedRow != ladderCellRow && expectedColumn != ladderCellColumn);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The startGame method is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

		
	@Test(timeout = 1000)
	public void testLadderUseMovesToOriginalMap() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Get the Ladder class using the provided path
			Class<?> ladderClass = Class.forName(ladderPath);

			// Create an instance of the Ladder class
			Object ladder = ladderClass.getDeclaredConstructor().newInstance();

			// Get the 'use' method in the Ladder class
			Method useMethod = ladderClass.getMethod("use", Class.forName(heroPath));

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getDeclaredConstructor(String.class, int.class, int.class, int.class)
					.newInstance("Medic", 100, 10, 2);

			// Get the 'map' and 'atticMap' fields in the Game class
			Field mapField = gameClass.getField("map");
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", Class.forName(heroPath)).invoke(null, hero);

			// Set to attic
			// Get the 'isInAttic' field in the Game class
			Field isInAtticField = gameClass.getDeclaredField("isInAttic");

			// Set the 'isInAttic' field to false
			isInAtticField.set(null, true);

			// Set the 'map' field to 'atticMap'
			mapField.set(null, atticMapField.get(null));

			// mapField.set(null, atticMapField.get(null));

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Call the 'use' method with the Hero instance
			useMethod.invoke(ladder, hero);

			testIsSwitchedToOriginalHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

		} catch (ClassNotFoundException e) {
			fail("The Game, Ladder, or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The 'use' method is missing in the Ladder class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 1000)
	public void testLadderUseUpdatesIsInAttic() {

		ResetGame();

		try {
			// Get the Game and Ladder classes using their paths
			Class<?> gameClass = Class.forName(gamePath);
			Class<?> ladderClass = Class.forName(ladderPath);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getDeclaredConstructor(String.class, int.class, int.class, int.class)
					.newInstance("Medic", 100, 10, 2);

			// Get the isInAttic field from the Game class
			Field isInAtticField = gameClass.getDeclaredField("isInAttic");
			isInAtticField.setAccessible(true);

			// Check if the isInAttic field exists
			assertNotNull("The isInAttic field does not exist in the Game class", isInAtticField);

			// Check if the isInAttic field exists
			assertNotNull("The isInAttic field does not exist in the Game class", isInAtticField);

			// Get the map field from the Game class
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			// Check if the map field exists
			assertNotNull("The map field does not exist in the Game class", mapField);

			// Get the atticMap field from the Game class
			Field atticMapField = gameClass.getDeclaredField("atticMap");
			atticMapField.setAccessible(true);

			// Check if the atticMap field exists
			assertNotNull("The atticMap field does not exist in the Game class", atticMapField);

			// Get the originalMap field from the Game class
			Field originalMapField = gameClass.getDeclaredField("originalMap");
			originalMapField.setAccessible(true);

			// Check if the originalMap field exists
			assertNotNull("The originalMap field does not exist in the Game class", originalMapField);

			// Create an instance of the Game and Ladder classes
			Object ladder = ladderClass.getDeclaredConstructor().newInstance();

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", Class.forName(heroPath)).invoke(null, hero);

			// Check if the isInAttic field is updated correctly
			boolean isInAttic = (boolean) isInAtticField.get(gameClass);
			assertFalse("isInAttic should be false before using the ladder", isInAttic);

			// Call the use() method of the Ladder instance

			// Get the 'use' method in the Ladder class
			Method useMethod = ladderClass.getMethod("use", Class.forName(heroPath));
			useMethod.invoke(ladder, hero);

			// Check if the isInAttic field is updated correctly
			isInAttic = (boolean) isInAtticField.get(gameClass);
			assertFalse("isInAttic should be false after using the ladder", isInAttic);

			// Check if the map field contains the atticMap and not the originalMap
			Object map = mapField.get(gameClass);
			Object atticMap = atticMapField.get(gameClass);
			Object originalMap = originalMapField.get(gameClass);
			assertSame("map should reference originalMap after using the ladder", originalMap, map);
			assertNotSame("map should not reference atticMap after using the ladder", atticMap, map);

		} catch (ClassNotFoundException e) {
			fail("The Game or Ladder class was not found.");
		} catch (NoSuchFieldException e) {
			fail("One or more required fields were not found in the Game class.");
		} catch (NoSuchMethodException e) {
			fail("The use method is missing in the Ladder class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 1000)
	public void testLadderAttributeInHero() {

		ResetGame();

		// Get the Hero class using the provided path
		Class<?> heroClass = null;
		try {
			heroClass = Class.forName(heroPath);
		} catch (ClassNotFoundException e) {
			fail("The Hero class was not found.");
		}

		// Get the Ladder class using the provided path
		Class<?> ladderClass = null;
		try {
			ladderClass = Class.forName(ladderPath);
		} catch (ClassNotFoundException e) {
			fail("The Ladder class was not found.");
		}

		// Check if the ladder variable exists in the Hero class
		Field ladderField = null;
		try {
			ladderField = heroClass.getDeclaredField("ladder");
			//ladderField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			fail("The ladder variable was not found in the Hero class.");
		}

		// Verify that the ladder variable has the correct type
		assertEquals("The ladder variable in the Hero class should be of type Ladder.", ladderClass,
				ladderField.getType());
	}

	@Test(timeout = 1000)
	public void testHeroMoveOnTrapCellInOriginalMap() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			Class<?> trapCellClass = Class.forName(trapCellPath);

			Object trapCell = trapCellClass.getConstructor().newInstance();

			map[1][0] = (TrapCell) trapCell;

			// Get the 'map' and 'atticMap' fields in the Game class
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Call the move method to up.
			heroClass.getMethod("move", Direction.class).invoke(hero, Direction.UP);

			testIsSwitchedToAtticHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

			int hp = (int) heroClass.getMethod("getCurrentHp").invoke(hero);

			assertEquals("The Hero should not take the trap damage as normal when the trap is in the Original Map ", hp,
					100);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The startGame method is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 1000)
	public void testHeroMoveCollectsLadder() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			mapField.setAccessible(true);
			atticMapField.setAccessible(true);
			originalMapField.setAccessible(true);

			Field isInAtticField = gameClass.getDeclaredField("isInAttic");
			isInAtticField.set(null, true);

			// Set the 'map' field to 'atticMap'
			mapField.set(null, atticMapField.get(null));

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			Class<?> collectibleCellClass = Class.forName(collectibleCellPath);

			Object collectibleCell = collectibleCellClass.getConstructor(Class.forName(collectiblePath))
					.newInstance(Class.forName(ladderPath).getConstructor().newInstance());

			map[1][0] = (CollectibleCell) collectibleCell;

			// Get the 'map' and 'atticMap' fields in the Game class

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Add ladder to Hero
			Method ladderSetterMethod = Class.forName(heroPath).getDeclaredMethod("setLadder",
					Class.forName(ladderPath));

			Object ladder = null;
			ladderSetterMethod.invoke(hero,ladder);


			// Call the move method to up.
			heroClass.getMethod("move", Direction.class).invoke(hero, Direction.UP);


			Method ladderGetterMethod = Class.forName(heroPath).getDeclaredMethod("getLadder");
			
			ladder = ladderGetterMethod.invoke(hero);

			testIsNotSwitchedToOriginalHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

			assertNotNull(
					"The hero should pick up the ladder after stepping on the collectible, but the ladder varaible in hero was null",
					ladder);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The startGame method is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 1000)
	public void testHeroClimbUsesLadder() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			// Get the 'map' and 'atticMap' fields in the Game class
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);
			mapBefore = atticMapBefore;

			// Add ladder to Hero
			Method ladderSetterMethod = Class.forName(heroPath).getDeclaredMethod("setLadder",
					Class.forName(ladderPath));

			ladderSetterMethod.invoke(hero, Class.forName(ladderPath).getConstructor().newInstance());

			Method ladderGetterMethod = Class.forName(heroPath).getDeclaredMethod("getLadder");
			
			Object ladder = ladderGetterMethod.invoke(hero);
			

			// Call the move method to up.
			heroClass.getMethod("climb").invoke(hero);

			testIsSwitchedToOriginalHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

			assertNotNull("The ladder should not be destroyed when used", ladder);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The climb method is missing in the Hero class.");
		} catch (NoSuchFieldException e) {
			fail("The ladder variable is missing from the Hero class: " + e.getCause());
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 1000)
	public void testHeroClimbThrowsNoAvailableResourcesException() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 2);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			// Get the 'map' and 'atticMap' fields in the Game class
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Add ladder to Hero
			Method ladderSetterMethod = Class.forName(heroPath).getDeclaredMethod("setLadder",
					Class.forName(ladderPath));

			Object ladder = null;
			
			ladderSetterMethod.invoke(hero, ladder);

			// Call the move method to up.
			try {
				heroClass.getMethod("climb").invoke(hero);
				fail("Trying to climb with no ladder, an exception should be thrown");

			} catch (NoSuchMethodException e) {
				fail("Hero class should have climb method");
			} catch (InvocationTargetException e) {
				try {
					if (!(Class.forName(noAvailableResourcesExceptionPath).equals(e.getCause().getClass())))
						fail("Trying to climb with no ladder, an noAvailableResourcesExceptionPath exception should be thrown, "
								+ e.getCause() + "Was thrown instead");

				} catch (ClassNotFoundException e1) {

					fail("There should be a noAvailableResourcesExceptionPath class");
				}
			}

			testIsNotSwitchedToAtticHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchFieldException e) {
			fail("The ladder variable is missing from the Hero class: " + e.getCause());
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}

	@Test(timeout = 1000)
	public void testHeroClimbUsesTwoActionPoints() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			int apBefore = (int) (Math.random() * 5 + 2);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, apBefore);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			mapField.setAccessible(true);
			atticMapField.setAccessible(true);
			originalMapField.setAccessible(true);

			Field isInAtticField = gameClass.getDeclaredField("isInAttic");
			isInAtticField.set(null, true);

			// Set the 'map' field to 'atticMap'
			mapField.set(null, atticMapField.get(null));

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			Class<?> collectibleCellClass = Class.forName(collectibleCellPath);

			Object collectibleCell = collectibleCellClass.getConstructor(Class.forName(collectiblePath))
					.newInstance(Class.forName(ladderPath).getConstructor().newInstance());

			map[1][0] = (CollectibleCell) collectibleCell;

			// Get the 'map' and 'atticMap' fields in the Game class

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Add ladder to Hero

			// Add ladder to Hero
			Method ladderSetterMethod = Class.forName(heroPath).getDeclaredMethod("setLadder",
					Class.forName(ladderPath));

			ladderSetterMethod.invoke(hero, Class.forName(ladderPath).getConstructor().newInstance());

			// Call the move method to climb.
			heroClass.getMethod("climb").invoke(hero);

			Method ladderGetterMethod = Class.forName(heroPath).getDeclaredMethod("getLadder");

			Object ladder = ladderGetterMethod.invoke(hero);

			testIsSwitchedToOriginalHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

			int ap = (int) heroClass.getMethod("getActionsAvailable").invoke(hero);

			assertEquals("The Hero should use 2 action points when they climb ", apBefore - 2, ap);

			assertNotNull(
					"The hero should pick up the ladder after stepping on the collectible, but the ladder varaible in hero was null",
					ladder);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchMethodException e) {
			fail("The startGame method is missing in the Game class.");
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}
	}

	@Test(timeout = 1000)
	public void testHeroClimbThrowsNotEnoughActionsException() {

		ResetGame();

		try {
			// Get the Game class using the provided path
			Class<?> gameClass = Class.forName(gamePath);

			// Check if the start method exists in the Game class
			Class<?>[] parameterTypes = { Class.forName(heroPath) };
			gameClass.getDeclaredMethod("startGame", parameterTypes);

			// Create an instance of the Hero class
			Class<?> heroClass = Class.forName(medicPath);
			Object hero = heroClass.getConstructor(String.class, int.class, int.class, int.class).newInstance("Medic",
					100, 10, 1);

			// Call the start method with the Hero instance
			gameClass.getMethod("startGame", parameterTypes).invoke(null, hero);

			// Check if the atticMap attribute is properly initialized
			Field mapField = gameClass.getDeclaredField("map");
			mapField.setAccessible(true);

			Cell[][] map = (Cell[][]) mapField.get(gameClass);

			// Get the 'map' and 'atticMap' fields in the Game class
			Field atticMapField = gameClass.getField("atticMap");
			Field originalMapField = gameClass.getField("originalMap");

			// Get the current values of 'map', 'atticMap', and 'originalMap'
			Cell[][] mapBefore = (Cell[][]) mapField.get(null);
			Cell[][] atticMapBefore = (Cell[][]) atticMapField.get(null);
			Cell[][] originalMapBefore = (Cell[][]) originalMapField.get(null);

			// Add ladder to Hero
			Method ladderSetterMethod = Class.forName(heroPath).getDeclaredMethod("setLadder",
					Class.forName(ladderPath));

			ladderSetterMethod.invoke(hero, Class.forName(ladderPath).getConstructor().newInstance());

			// Call the move method to up.
			try {
				heroClass.getMethod("climb").invoke(hero);
				fail("Trying to climb with not enough action points, an exception should be thrown");

			} catch (NoSuchMethodException e) {
				fail("Hero class should have climb method");
			} catch (InvocationTargetException e) {
				try {
					if (!(Class.forName(notEnoughActionsExceptionPath).equals(e.getCause().getClass())))
						fail("Trying to climb with no ladder, an notEnoughActionsExceptionPath exception should be thrown, "
								+ e.getCause() + "Was thrown instead");

				} catch (ClassNotFoundException e1) {

					fail("There should be a notEnoughActionsExceptionPath class");
				}
			}

			testIsNotSwitchedToAtticHelper(hero, mapBefore, atticMapBefore, originalMapBefore);

		} catch (ClassNotFoundException e) {
			fail("The Game or Hero class was not found.");
		} catch (NoSuchFieldException e) {
			fail("The ladder variable is missing from the Hero class: " + e.getCause());
		} catch (Exception e) {
			fail("An unexpected error occurred: " + e.getCause());
		}

	}
}
