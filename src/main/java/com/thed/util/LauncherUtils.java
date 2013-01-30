package com.thed.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class LauncherUtils {

	/**
	 * Parse path for three types of string
	 * 1. Strings without any space or quotes							 - [^\\s\"'] 
	 * 2. Strings that start with double quote and end with double quote - \"([^\"]*)\"
	 * 3. Strings that start with single quote and end with single quote - \'([^\']*)\'
	 * @param scriptPath
	 * @return
	 */
	public static String getFilePathFromCommand(String scriptPath) {
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(scriptPath);
		String path = null;
		while (regexMatcher.find()) {
		    if (regexMatcher.group(1) != null) {
		        // Add double-quoted string without the quotes
		    	path = regexMatcher.group(1);
		    } else if (regexMatcher.group(2) != null) {
		        // Add single-quoted string without the quotes
		    	path = regexMatcher.group(2);
		    } else {
		        // Add unquoted word
		    	path = regexMatcher.group();
		    }
		    if(StringUtils.contains(path, ".script"))
				return path;
		}
		return null;
	}

}
