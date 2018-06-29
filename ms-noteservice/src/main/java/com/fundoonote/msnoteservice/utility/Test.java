package com.fundoonote.msnoteservice.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test {

	public static void main(String args[]) {
		List<String> list = new ArrayList<String>();
		init(list);
		System.out.println(streamApi(list));
		System.out.println(forEachLoop(list));

	}

	public static void init(List<String> list) {
		for (int i = 0; i < 10000; i++) {
			list.add(UUID.randomUUID().toString());
		}
	}

	public static long streamApi(List<String> list) {
		List<String> list1 = new ArrayList<String>(); 
		long startTime = System.currentTimeMillis();
		list.stream().forEach(string -> list1.add(string));
		long stopTime = System.currentTimeMillis();
		return stopTime - startTime;
	}

	public static long forEachLoop(List<String> list) {
		List<String> list1 = new ArrayList<String>(); 
		//list.forEach(string -> list.concat(string));
		long startTime = System.currentTimeMillis();
		
		for (String stringlist : list) {
			list1.add(stringlist);
		}
		long stopTime = System.currentTimeMillis();
		return stopTime - startTime;
	}
}
