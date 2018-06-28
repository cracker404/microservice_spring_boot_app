package com.test;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		pattern1(3, 4);
	}
	
	public static void pattern1(int start, int n) {
		
		int count = n*2;
		for(int i = 0; i<count/2; i++) {
			for(int j=0; j<i+1; j++) {
				System.out.print(start);
			}
			start++;
			System.out.println();
		}
		for(int i= count/2; i>0; i--){
			start--;
			for(int j =0 ;j<i;j++) {
				System.out.print(start);
			}
			
			System.out.println();
		}
	}

}
