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

	public static int time = 0;
	public static int startTime = 0;

	public static int instruction = 0;
	public static int waitTimer = 0;
	public static String scenarioNumber = "";

	public static ArrayList<Object>[] valuesLists = (ArrayList<Object>[])new ArrayList[16];

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("The single argument should be the scenario number to load.");
			return;
		}
		scenarioNumber = args[0];
		scenario = Files.readAllLines(new File("Scenario" + scenarioNumber + ".txt").toPath());

		for (int i = 0; i<valuesLists.length; i++) {
			valuesLists[i] = new ArrayList<>();
		}

		floors = new Floor[4];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new Floor(i);
		}

		elev = new Elevator(parseInt(interpret(scenario.get(instruction++))[0]));

		startTime = time = parseInt(interpret(scenario.get(instruction++))[0]);
		while (true) {
			log();
			cycle();
			elev.action();
			time++;
		}
	}

	public static void cycle() throws Exception {
		elev.idleTime++;
		elev.irSensor = true;
		elev.doorOpenButton = false;
		elev.doorCloseButton = false;
		if (waitTimer > 0) {
			waitTimer--;
			return;
		}
		elev.idleTime = 0;
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
				case "firekey":
					if (tokens[1].equals("insert")) {
						floors[parseInt(tokens[2])].insertKey();
					}
					else {
						floors[parseInt(tokens[2])].removeKey();
					}
					break;
				case "door":
					if (tokens[1].equals("open")) {
						elev.doorOpenButton = true;
					}
					else {
						elev.doorCloseButton = true;
					}
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

		valuesLists[0].add(time-startTime);
		for (int i = 0; i < values.length; i++) {
			valuesLists[i+1].add(values[i]);
		}
	}



	public static void flush() throws Exception {
		String[] names = {"time", "location", "irWaitSecs", "idleTime", "direction", "latched", "doorsOpen", "doorsOpenedOnFloor", "sound", "irSensor", "doorOpenButton", "doorCloseButton", "x19", "x20", "x1000", "", "", ""};
		PrintWriter writer = new PrintWriter("Scenario" + scenarioNumber + "Output.txt", "UTF-8");
		for (int i = 0; i < valuesLists.length; i++) {
			String str = rightPad(names[i], 19) + "\t";
			for (Object ob : valuesLists[i]) {
				if (ob instanceof int[]) {
					str += Arrays.toString((int[])ob) + "\t";
				} else {
					str += ob.toString() + "\t";
				}
			}
			str = str.replaceAll("true", "1").replaceAll("false", "0");
			writer.println(str);
		}
		writer.close();
	}

	public static String rightPad(String str, int length) {
		while (str.length() < length) {
			str += " ";
		}
		return str;
	}

	public static Stream<Object> flatten(Object[] array) {
		return Arrays.stream(array).flatMap(o -> o instanceof Object[]? flatten((Object[])o): Stream.of(o));
	}

	public static int parseInt(String str) {
		return Integer.parseInt(str);
	}

	public static String[] interpret(String line) {
		return line.contains("/")?line.substring(0, line.indexOf("/")).split(" ") : line.split(" ");
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
