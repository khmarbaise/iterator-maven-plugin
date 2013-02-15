package com.soebes.maven.plugins.itexin;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.InvocationOutputHandler;

public class OutputConsumer implements InvocationOutputHandler {

	private Log log;
	
	public OutputConsumer(Log log) {
		this.log = log;
	}

    @Override
    public void consumeLine(String line) {
        log.info(line);
    }
    
}
