package com.bdps.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler {

	public static String exceptionAsString(Exception e) {
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}
	
}
