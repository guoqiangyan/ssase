package org.ssascaling.actuator.linux;

import java.io.IOException;

import org.ssascaling.actuator.Actuator;
import org.ssascaling.executor.Executor;

public class MemoryActuator implements Actuator {

	final String command = "/bin/sh /home/tao/actuator/memory_actuator.sh";

	@Override
	public boolean execute(String alias, long... value) {
		
		
		if (Executor.isTest) {
			System.out.print("Setting " + alias + " with memory " + value[0] + "\n");
			return true;
		}

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command + " " + alias + " " + value[0]);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			p.destroy();
		}

		return true;
	}

}
