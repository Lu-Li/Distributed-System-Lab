package driver;

public class Log {
	private static void print(String type, String msg, int color) {
		System.err.println((char) 27 + "[" + color + "m" + "[" + type + "] " + msg + (char) 27 + "[0m");
	}

	public static void info(String type, String msg) {
		print(type,msg,33);
	}

	public static void verbose(String type, String msg) {
		print(type,msg,36);
	}
}
