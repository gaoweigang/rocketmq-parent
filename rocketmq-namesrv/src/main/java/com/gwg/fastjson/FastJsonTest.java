package com.gwg.fastjson;

import com.alibaba.fastjson.JSON;

public class FastJsonTest {
	
	
	public static void main(String[] args) {
		
		String jsonString = "{"
			    +"\"dataVersion\":{" 
					+"\"counter\":7,"
					+"\"timestamp\":1533635923566"
			    +"},"
			    +"\"topicConfigTable\":{"
					+"\"%RETRY%Push_Consumer_Test\":{"
						+"\"order\":false,"
						+"\"perm\":6,"
						+"\"readQueueNums\":1,"
						+"\"topicFilterType\":\"SINGLE_TAG\","
						+"\"topicName\":\"%RETRY%Push_Consumer_Test\","
						+"\"topicSysFlag\":0,"
						+"\"writeQueueNums\":1"
					+"},"
					+"\"TopicTest\":{"
					    +"\"order\":false,"
						+"\"perm\":6,"
						+"\"readQueueNums\":4,"
						+"\"topicFilterType\":\"SINGLE_TAG\","
						+"\"topicName\":\"TopicTest\","
						+"\"topicSysFlag\":0,"
						+"\"writeQueueNums\":4"
					+"}"
			    +"}"
		    +"}";
		System.out.println(jsonString);
		TopicConfigSerializeWrapper topicConfigSerializeWrapper = TopicConfigSerializeWrapper.fromJson(jsonString, TopicConfigSerializeWrapper.class);
		System.out.println(topicConfigSerializeWrapper);
	}

}
