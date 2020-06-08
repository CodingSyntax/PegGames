package com.supercatgaming.peggames;

import java.util.Scanner;

public class Calc {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		int in;
		do {
			in = input.nextInt();
			System.out.println(in * .2);
		} while (in != -1);
	}
}
