package com.gwg.commons.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * https://mp.weixin.qq.com/s?__biz=MzU2NjIzNDk5NQ==&mid=2247484159&idx=1&sn=e491b9853ccc598662f344e12911ebb7&scene=21#wechat_redirect
 * @author hp
 *
 */
public class CommonsCliTest {

	public static void main(String[] args) {

		//1,定义阶段 定义有哪些参数
		Options options = new Options();
		Option opt = new Option("h", "help", false, "Print help");
		opt.setRequired(false);
		options.addOption(opt);
      
		/**
		 * opt: 选项的简短表示
		 * longOpt: 选项的长表示
		 * hasArg: 该选项是否有参数
		 * description: 描述
		 */
		opt = new Option("n", "namesrvAddr", true, "Name server address list, eg: 192.168.0.1:9876;192.168.0.2:9876");
		opt.setRequired(false);
		options.addOption(opt);

		// 解析阶段
		CommandLineParser parser = new PosixParser();
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth(110);
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				hf.printHelp("mqnamesrv", options, true);
				System.exit(-1);
			}
		} catch (ParseException e) {
			hf.printHelp("mqnamesrv", options, true);
		}

		if (null == commandLine) {
			System.exit(-1);
		}

		// 3.询问阶段
		if (commandLine.hasOption('n')) {
			String file = commandLine.getOptionValue('n');// 获取可选参数对应的值
			System.out.println(file);
		}
	}

}
