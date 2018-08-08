package com.gwg.logback;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * logback使用的默认配置文件是logback
 * logback使用外部配置文件
 * https://www.cnblogs.com/h--d/p/5671528.html
 */
public class LogbackReadSpecifiedConfigFile {

	public static void main(String[] args) throws IOException, JoranException {
		System.out.println("用户home目录："+System.getProperty("user.home"));
		String externalConfigFileLocation = "E:/rocketmq_workspace/rocketmq-parent/conf/logback_namesrv.xml";
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		configurator.doConfigure(externalConfigFileLocation);

		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		Logger logger = LoggerFactory.getLogger(LogbackReadSpecifiedConfigFile.class);

		logger.debug("现在的时间是 {}", new Date().toString());

		logger.info(" This time is {}", new Date().toString());

		logger.warn(" This time is {}", new Date().toString());

		logger.error(" This time is {}", new Date().toString());

		@SuppressWarnings("unused")
		int n = 1 / 0;
	}


}