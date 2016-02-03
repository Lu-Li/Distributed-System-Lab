package logger;
/*
 * Logger is an additional member of the distributed system 
 * (and thus needs to be initialized using the config file).
 */
public class Logger {
	// TODO: config file design?
	public Logger(String filename) {
		this.parseConfigFile(filename);
	}
	
	public void parseConfigFile(String filename) {
		
	}
}
