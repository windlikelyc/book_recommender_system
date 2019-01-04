package com.lyc.algorithm.Time_Item_Recommender.book_lda;

public class Input1 {
	public String trainfile;
	public String testfile;
	public String geofile;
//	public String friendfile;

	public Input1(String matrix){
		if("bookdata".equals(matrix)){
			trainfile = String.format("F:\\Data\\book\\train.txt",matrix);
			testfile = String.format("F:\\Data\\book\\test.txt",matrix);
			geofile = String.format("F:\\Data\\book\\data.txt",matrix);
//			friendfile=String.format("F:\\Data\\Yelp\\%s\\hometown\\friend.txt",matrix);
		}
	}
	
}
