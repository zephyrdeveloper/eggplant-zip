/**
 * ///////////////////////////////////////////////////////////////////////////////////////
 * //																					//
 * //  D SOFTWARE INCORPORATED															//
 * //  Copyright 2007-2011 D Software Incorporated										//
 * //  All Rights Reserved.																//
 * //																					//
 * //  NOTICE: D Software permits you to use, modify, and distribute this file			//
 * //  in accordance with the terms of the license agreement accompanying it.			//
 * //																					//
 * //  Unless required by applicable law or agreed to in writing, software				//
 * //  distributed under the License is distributed on an "AS IS" BASIS,				//
 * //  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.			//
 * //																					//
 * //////////////////////////////////////////////////////////////////////////////////////
 */
package com.thed.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.thed.util.LauncherUtils;
import com.thed.util.ScriptUtil;

/**
 * Hello world!
 *
 */
public class EggplantZBotScriptLauncher extends DefaultZBotScriptLauncher{
	
	private final Logger logger = Logger.getLogger(EggplantZBotScriptLauncher.class.getName());
	private Integer currentTcExecutionResult;

	@Override
	public void testcaseExecutionResult() {
		String scriptPath = LauncherUtils.getFilePathFromCommand(currentTestcaseExecution.getScriptPath());
		logger.info("Script Path is " + scriptPath);
		File scriptFile = new File(scriptPath);
		String scriptName = scriptFile.getName().split("\\.")[0];
		logger.info("Script Name is " + scriptName);
		File resultsFolder = new File(scriptFile.getParentFile().getParentFile().getAbsolutePath() + File.separator + "Results");
		File statisticalFile = resultsFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return StringUtils.endsWith(s, "Statistics.xml");
			}
		})[0];
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Document statisticalDoc = getDoc(statisticalFile);
			String result = xpath.compile("/statistics/script[@name='"+ scriptName +"']/LastStatus/text()").evaluate(statisticalDoc, XPathConstants.STRING).toString();
			logger.info("Result is " + result);
			String comments;
			if(StringUtils.equalsIgnoreCase(result, "Success")){
				logger.info("Test success detected");
				status = currentTestcaseExecution.getTcId() + ": "+ currentTestcaseExecution.getScriptId() + " STATUS: Script Successfully Executed";
				currentTcExecutionResult = new Integer(1);
				comments = " Successfully executed on " + agent.getAgentHostAndIp();
			}else{
				logger.info("Test failure detected, getting error message");
				status = currentTestcaseExecution.getTcId() + ": "+ currentTestcaseExecution.getScriptId() + " STATUS: Script Successfully Executed";
				currentTcExecutionResult = new Integer(2);
				comments = " Error in test: " ;
				String lastRunDateString = xpath.compile("/statistics/script[@name='"+ scriptName +"']/LastRun/text()").evaluate(statisticalDoc);
				//Date lastRunDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(lastRunDateString);
				File historyFile = new File(resultsFolder.getAbsolutePath() + File.separator + scriptName + File.separator + "RunHistory.xml");
				Document historyDoc = getDoc(historyFile);
				//xpath = XPathFactory.newInstance().newXPath();
				String xpathExpr = "/runHistory[@script='"+scriptName+"']/run[contains(RunDate, '"+ StringUtils.substringBeforeLast(lastRunDateString, " ") +"')]/ErrorMessage/text()";
				logger.info("Using xPath to find errorMessage " + xpathExpr);
				comments += xpath.compile(xpathExpr).evaluate(historyDoc, XPathConstants.STRING);
				logger.info("Sending comments: " + comments);
			}
			if(currentTcExecutionResult != null){
	        	ScriptUtil.updateTestcaseExecutionResult(url, currentTestcaseExecution, currentTcExecutionResult, comments);
	        }
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in reading process steams \n", e);
		}
	}

	private Document getDoc(File xmlFile) throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, ParseException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(xmlFile);
	}
}
