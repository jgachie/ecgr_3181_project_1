import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;


public class Main {

	public static Elevator elev;
	public static Floor[] floors;

	public static List<String> scenario;

	public static int currentTime = 0;

	public static int instruction = 0;
	public static int waitTimer = 0;

	public static ArrayList<Object>[] valuesLists = (ArrayList<Object>[])new ArrayList[16];

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("The single argument should be the scenario number to load.");
			return;
		}
		scenario = Files.readAllLines(new File("Scenario" + args[0] + ".txt").toPath());

		for (int i = 0; i<valuesLists.length; i++) {
			valuesLists[i] = new ArrayList<>();
		}

		floors = new Floor[4];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new Floor(i);
		}

		elev = new Elevator(parseInt(interpret(scenario.get(instruction++))[0]));

		currentTime = parseInt(interpret(scenario.get(instruction++))[0]);
		while (true) {
			cycle();
			elev.action();
			log();
			currentTime++;
		}
	}

	public static void cycle() throws Exception {
		elev.irSensor = true;
		elev.doorOpenButton = false;
		elev.doorCloseButton = false;
		while (waitTimer-- > 0) {
			return;
		}
		while (true) {
			String[] tokens = interpret(scenario.get(instruction++));
			switch (tokens[0]) {
				case "call": 
					System.out.println("Called");
					elev.registers.setFloorCalled(parseInt(tokens[2]), parseInt(tokens[1]));
					break;
				case "irbreak":
					System.out.println("IRBreak");
					elev.irSensor = false;
					break;
				case "wait":
					System.out.println("Wait");
					waitTimer = parseInt(tokens[1]);
					return;
				case "end":
					System.out.println("End");
					System.out.println("Scenario ended without issue");
					flush();
					System.exit(0);
				default:
					throw new RuntimeException("Bad command token");
			}
		}
	}

	public static void log() {
		Object[] values = flatten(new Object[]{elev.log(), elev.registers.log()}).toArray();
		for (int i = 0; i < values.length; i++) {
			valuesLists[i].add(values[i]);
		}
	}

	public static void flush() throws Exception {
		String[] names = {"location", "irWaitSecs", "idleTime", "direction", "latched", "doorsOpen", "doorsOpenedOnFloor", "sound", "irSensor", "doorOpenButton", "doorCloseButton", "x19", "x1000", "x20", "", "", ""};
		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
		for (int i = 0; i < valuesLists.length; i++) {
			String str = names[i] + "\t\t\t\t";
			for (Object ob : valuesLists[i]) {
				str += ob.toString() + "\t";
			}
			writer.println(str);
		}
		writer.close();
	}

	public static Stream<Object> flatten(Object[] array) {
		return Arrays.stream(array).flatMap(o -> o instanceof Object[]? flatten((Object[])o): Stream.of(o));
	}

	public static int parseInt(String str) {
		return Integer.parseInt(str);
	}

	public static String[] interpret(String line) {
		return line.substring(0, line.indexOf("/")).split(" ");
	}

	public static int fireCall() {
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].fireKey && !floors[i].fireKeyVisited) {
				return i;
			}
		}
		return -1;
	}

	public static boolean fireMode() {
		for (Floor floor : Main.floors) {
			if (floor.fireKey) {
				return true;
			}
		}
		return false;
	}
	
}
