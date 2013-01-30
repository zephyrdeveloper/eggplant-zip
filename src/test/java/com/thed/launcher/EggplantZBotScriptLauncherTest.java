package com.thed.launcher;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thed.model.Agent;
import com.thed.model.TestcaseExecution;

public class EggplantZBotScriptLauncherTest {

	private EggplantZBotScriptLauncher launcher;

	@Before
	public void setUp() throws Exception {
		launcher = new EggplantZBotScriptLauncher();
		launcher.setAgent(new Agent());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTestcaseExecutionResult() {
		TestcaseExecution currentTestcaseExecution = new TestcaseExecution();
		String path = Thread.currentThread().getContextClassLoader().getResource("EggPlantPuree.suite").getFile() + File.separator + "Scripts" + File.separator + "CreateProject.script";
		String fullCommandLine = "\"C:\\Program Files (x86)\\Eggplant\\runscript.bat\" " + path + " -host BASEVM -port 5900 -password 1qaz2wsx -CommandLineOutput yes";
		currentTestcaseExecution.setScriptPath(fullCommandLine);
		launcher.setCurrentTestcaseExecution(currentTestcaseExecution);
		launcher.testcaseExecutionResult();
		
	}

}
