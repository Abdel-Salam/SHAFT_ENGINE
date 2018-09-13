package com.shaftEngine.supportActionLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.testng.Assert;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.shaftEngine.ioActionLibrary.FileManager;
import com.shaftEngine.ioActionLibrary.ReportManager;

public class SSHActions {

	private SSHActions() {
		throw new IllegalStateException("Utility class");
	}

	private static void passAction(String actionName, String testData, String log) {
		String message = "Successfully performed action [" + actionName + "].";
		if (testData != null) {
			message = message + " With the following test data [" + testData + "].";
		}
		ReportManager.log(message);
		if (log != null) {
			ReportManager.attach("Command Response data", log);
		}
	}

	private static void passAction(String actionName, String testData) {
		passAction(actionName, testData, null);
	}

	private static void failAction(String actionName, String testData, String log) {
		String message = "Failed to perform action [" + actionName + "].";
		if (testData != null) {
			message = message + " With the following test data [" + testData + "].";
		}
		ReportManager.log(message);
		if (log != null) {
			ReportManager.attach("Command Response data", log);
		}
		Assert.fail(message);
	}

	private static void failAction(String actionName, String testData) {
		failAction(actionName, testData, null);
	}

	private static Session createSSHsession(String hostname, int sshPortNumber, String username,
			String keyFileFolderName, String keyFileName) {
		Session session = null;
		String testData = hostname + ", " + sshPortNumber + ", " + username + ", " + keyFileFolderName + ", "
				+ keyFileName;
		try {
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();

			jsch.addIdentity(FileManager.getAbsolutePath(keyFileFolderName, keyFileName));
			session = jsch.getSession(username, hostname, sshPortNumber);
			session.setConfig(config);

			session.connect();
			// System.out.println("Connected");
			passAction("createSSHsession", testData);
		} catch (JSchException e) {
			ReportManager.log(e);
			failAction("createSSHsession", testData);
		}
		return session;
	}

	private static String performSSHcommand(Session session, List<String> commands) {
		// StringBuilder logBuilder = new StringBuilder();
		String log = "";
		String command = "";

		try {
			for (Iterator<String> i = commands.iterator(); i.hasNext();) {
				command = i.next();
				ReportManager.log("Attempting to perform the following command remotely. Command: [" + command + "]");

				ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
				channelExec.setCommand(command);
				channelExec.connect();
				BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					log = log + System.lineSeparator() + line;
					// System.out.println(line);
				}
				while ((line = errorReader.readLine()) != null) {
					log = log + System.lineSeparator() + line;
					// System.out.println(line);
				}

				// Command execution completed here.

				// Retrieve the exit status of the executed command
				int exitStatus = channelExec.getExitStatus();
				if (exitStatus > 0) {
					// System.out.println("Remote script exec error! " + exitStatus);
				}

				reader.close();
				errorReader.close();

			}
			// Disconnect the Session
			session.disconnect();
			// System.out.println("DONE");
		} catch (IOException | NullPointerException | JSchException e) {
			ReportManager.log(e);
			failAction("performSSHcommand", String.join(" && ", commands), log);
			return log;
		}
		passAction("performSSHcommand", String.join(" && ", commands), log);
		return log;
	}

	/**
	 * Establish a connection to a remote SSH server using a key file, then perform
	 * a certain command and return its logs.
	 * 
	 * @param hostname
	 *            IP address of the SSH server
	 * @param sshPortNumber
	 *            Port number of the SSH service on the target server
	 * @param username
	 *            User name used to connect to the target server
	 * @param keyFileFolderName
	 *            Name of the folder that contains the key file, relative to the
	 *            project directory
	 * @param keyFileName
	 *            Name of the key file including its extension (if any)
	 * @param commands
	 *            The target command that should be executed on the SSH server
	 * @return
	 */
	public static String performSSHcommand(String hostname, int sshPortNumber, String username,
			String keyFileFolderName, String keyFileName, List<String> commands) {

		Session session = createSSHsession(hostname, sshPortNumber, username, keyFileFolderName, keyFileName);
		return performSSHcommand(session, commands);
	}

	public static String performSSHcommand(String hostname, int sshPortNumber, String username,
			String keyFileFolderName, String keyFileName, String command) {
		return performSSHcommand(hostname, sshPortNumber, username, keyFileFolderName, keyFileName,
				Arrays.asList(command));
	}

	public static String performSSHcommand(String hostname, int sshPortNumber, String username,
			String keyFileFolderName, String keyFileName, String dockerName, String dockerUsername,
			List<String> commands) {

		List<String> dockerCommands = Arrays.asList();

		commands.forEach(new Consumer<String>() {
			public void accept(String command) {
				dockerCommands.add("docker exec -u " + dockerUsername + " -i " + dockerName + " sh -c " + command);
			}
		});

		Session session = createSSHsession(hostname, sshPortNumber, username, keyFileFolderName, keyFileName);
		return performSSHcommand(session, dockerCommands);
	}

	public static String performSSHcommand(String hostname, int sshPortNumber, String username,
			String keyFileFolderName, String keyFileName, String dockerName, String dockerUsername, String command) {
		String dockerCommand = "docker exec -u " + dockerUsername + " -i " + dockerName + " sh -c " + command;

		return performSSHcommand(hostname, sshPortNumber, username, keyFileFolderName, keyFileName,
				Arrays.asList(dockerCommand));
	}

	public static String executeShellCommand(List<String> commands) {
		String log = "";
		String command = "";

		try {
			for (Iterator<String> i = commands.iterator(); i.hasNext();) {
				command = i.next();

				ReportManager.log("Attempting to perform the following command locally. Command: [" + command + "]");

				Process p = Runtime.getRuntime().exec(command);
				p.waitFor();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					log = log + System.lineSeparator() + line;
				}
				while ((line = errorReader.readLine()) != null) {
					log = log + System.lineSeparator() + line;
				}
				reader.close();
				errorReader.close();
			}
		} catch (IOException | InterruptedException e) {
			ReportManager.log(e);
			failAction("executeShellCommand", command, log);
			return log;
		}

		passAction("executeShellCommand", String.join(" && ", commands), log);
		return log;
	}

	public static String executeShellCommand(String command) {
		return executeShellCommand(Arrays.asList(command));
	}
}
