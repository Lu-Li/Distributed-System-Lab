package lab0;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })

public class Configuration {
	private List<Map> network;
	private List<Map> sendRules;
	private List<Map> receiveRules;

	public enum Action {
		Drop, DropAfter, Delay, NoAction
	}

	public enum Direction {
		Send, Receive
	}

	public Configuration() {
		this("src/main/resources/config.yaml");
	}

	public Configuration(String filepath) {
		InputStream input;
		try {
			input = new FileInputStream(new File(filepath));
			Yaml yaml = new Yaml();

			Map config = (Map) yaml.load(input);
			network = (List<Map>) config.get("configuration");
			sendRules = (List<Map>) config.get("sendRules");
			receiveRules = (List<Map>) config.get("receiveRules");

			System.out.println("- Init:");
			System.out.println("network config:" + network);
			System.out.println("send rules:" + sendRules);
			System.out.println("receive rules:" + receiveRules);
			System.out.println();
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}

	public Action getAction(Message message, Direction direction) {
		if (!message.isValid()) {
			System.err.println("message is not valid:" + message);
			return Action.Drop;
		}

		List<Map> rules = direction == Direction.Receive ? receiveRules : sendRules;

		for (Map rule : rules) {
			if (rule.get("src") != null && !message.getSrc().equals(rule.get("src")))
				continue;
			if (rule.get("dest") != null && !message.getDest().equals(rule.get("dest")))
				continue;
			if (rule.get("kind") != null && !message.getKind().equals(rule.get("kind")))
				continue;
			String action = (String) rule.get("action");
			if (action.equals("dropAfter")) {
				if (rule.get("seqNum") != null && message.getSeqNum() < (Integer) rule.get("seqNum"))
					continue;
				return Action.DropAfter;
			} else {
				if (rule.get("seqNum") != null && message.getSeqNum() != (Integer) rule.get("seqNum"))
					continue;
				if (action.equals("drop"))
					return Action.Drop;
				if (action.equals("delay"))
					return Action.Delay;
			}
		}

		return Action.NoAction;
	}

	public static void main(String[] args) {
		Configuration config = new Configuration();
		Message message = new Message("bob", "ACK", null);
		message.set_seqNum(0);
		message.set_source("charlie");
		Action action = config.getAction(message, Direction.Receive);
		System.out.println(action);
	}
}
