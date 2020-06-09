package com.supercatgaming.peggames;

import java.util.Scanner;

public class Calc {
	public static void main(String[] args) {
//		Scanner input = new Scanner(System.in);
//		String in;
//		do {
//			in = input.next();
//			in = in.replaceAll("\\{", "").replaceAll("}", "").replaceAll(" ", "");
//			String[] a = in.split(",");
//			for (String b : a) {
//				int c = Integer.parseInt(b);
//				System.out.println(c + " -> " + c*.2);
//			}
//		} while (!in.equals("-1"));
		
		int[][][] i = new int[][][] {
				{{41, 52}, {104, 52}, {165, 51}, {228, 52}, {289, 52}, {352, 52}, {412, 54}, {475, 54},
						{537, 54}, {600, 53}},
				{{40, 192}, {103, 192}, {164, 194}, {227, 194}, {288, 193}, {352, 192}, {411, 200},
						{475, 194}, {536, 194}, {598, 194}}
		};
		
		System.out.println("new int[][][] {");
		for (int[][] i2 : i) {
			System.out.print("{");
			for (int[] i3 : i2) {
				System.out.print("{");
				//for (int i4 : i3) {
					System.out.print(i3[0] + ", ");
					System.out.print((i3[1] + 1));
				//}
				//System.out.print("\b\b");
				System.out.print("}, ");
			}
			System.out.print("\b\b");
			System.out.println("},");
		}
		System.out.println("}");
	}
	
	static int round(double n) {
		int i = (int)n;
		if ((int)(n + .5) == i) {
			return i;
		} else return i + 1;
	}
}
