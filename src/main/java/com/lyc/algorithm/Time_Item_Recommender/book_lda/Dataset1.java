package com.lyc.algorithm.Time_Item_Recommender.book_lda;

import com.lyc.algorithm.Time_Item_Recommender.book_lda.Train;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class Dataset1 {
	public static int U; // 用户数
	public static int I; // 地点数数目
	public static int W; // 标签数目
	public static int A; // 作者数目
	public static int P; // 出版商数目

	public static int C;
//	public static Map<Integer,Integer> userPro;


	public static Train readSamples(String fileName, String fileName1) throws IOException{
		    Map<Integer,Double>stampsum=readstampsum(fileName1);
		    Map<Integer,Integer> usercheck=readcheckinfo(fileName);
            Map<Integer,Integer> usersq=new HashMap<Integer,Integer>(); // user sequence 维护了用户连续的序号，如 0，1，。。。U
            int [][]sample= new int[usercheck.size()][];  //用户地点模型
		    double[][] sample1=new double[usercheck.size()][]; // 每一个sample 对应一个主题，800*用户签到次数，用户时间模型
		    String[][] sample2=new String[usercheck.size()][]; // 用户区域模型
		    String[][] sample3=new String[usercheck.size()][]; // 用户？？str【4】 模型

			String[][] samplea = new String[usercheck.size()][];
			String[][] samplep = new String[usercheck.size()][];


		    double[][] sample4=new double[usercheck.size()][]; // 用户小时模型
		    int[][] sample5= new int[usercheck.size()][];// 用户星期模型
		    int[][] sample10= new int[usercheck.size()][];// 用户str【11】模型
		    Train t= new Train();
		    File read = new File(fileName);
		    BufferedReader br = new BufferedReader(new FileReader(read));
	        String temp=br.readLine();
	        while(temp!=null){
	        	String[] str=temp.split("\t");
	        	int uid=Integer.parseInt(str[0]);
	        	String wd=str[3];
	        	String ad= str[5]; //第五个栏目是作者
				String pd = str[6]; // 第六个栏目是出版社

				String cd=str[4];
	        	int num=usercheck.get(uid);
	        	if(!usersq.containsKey(uid)){
	        		sample[uid]=new int[num];
		        	sample1[uid]=new double[num];
		        	sample2[uid]=new String[num];
		        	sample3[uid]=new String[num];
					samplea[uid] = new String[num];
					samplep[uid] = new String[num];


					sample4[uid]=new double[num];
		        	sample5[uid]=new int[num];
		        	sample10[uid]=new int[num];
		        	usersq.put(uid, 0);

	        	}else{
	        		usersq.put(uid, usersq.get(uid)+1);
	        	}

	        	Integer pid=Integer.parseInt(str[1]);
	        	sample[uid][usersq.get(uid)]=pid;
	        	Double tid=Double.parseDouble(str[2]);
	        	sample1[uid][usersq.get(uid)]=tid*1.0/stampsum.get(uid);
	        	sample2[uid][usersq.get(uid)]=wd;
	        	sample3[uid][usersq.get(uid)]=cd;

				samplea[uid][usersq.get(uid)] = ad;
				samplep[uid][usersq.get(uid)] = pd;

	        	long t1=Long.parseLong(str[2]);
	        	int week=CalculateWeekDay(t1);
	        	sample5[uid][usersq.get(uid)]=week;
	        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	String sd = sdf.format(new Date(t1));
	        	double hour=Double.parseDouble(sd.split(" ")[1].split(":")[0]);
	        	if(hour==0){
	        		sample4[uid][usersq.get(uid)]=0.01;
	        	}else{
	        		sample4[uid][usersq.get(uid)]=hour/24;
	        	}
	        	int loc=Integer.parseInt(str[6]);
	        	sample10[uid][usersq.get(uid)]=loc;
	        	temp=br.readLine();
	        }
	         br.close();
                t.trainI=sample;
	            t.trainT=sample1;
	            t.trainW=sample2;
				t.trainAu = samplea;
				t.trainPub = samplep;
	            t.trainC=sample3;
	            t.trainJimT=sample4;
	            t.trainS=sample5;
	            t.trainL=sample10;
	            return t;
	}

	//ForYelp
	public static int CalculateWeekDay(long date){
		SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd");
		String time = df.format(date);
		String[] week = time.split("-");
		int y =Integer.parseInt(week[0]);
		int m =Integer.parseInt(week[1]);
		int d =Integer.parseInt(week[2]);
		 if (m == 1 || m == 2) {
	            m += 12;
	            y--;
	        }

		 int iWeek = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400)% 7 ;


		return iWeek;
	}
	// 返回一个map，key是用户id，value是签到次数
	public static Map<Integer,Integer> readcheckinfo(String fileName)throws IOException{
		Map<Integer,Integer> usercheck= new HashMap<Integer,Integer>();
		File read = new File(fileName);
	    BufferedReader br = new BufferedReader(new FileReader(read));
	    String temp=br.readLine();
	    while(temp!=null){
	    	String[] str=temp.split("\t");
	    	Integer uid=Integer.parseInt(str[0]);
	    	if(usercheck.isEmpty()){
	    		usercheck.put(uid, 1);
	    	}else{
	    		if(!usercheck.containsKey(uid)){
	    			usercheck.put(uid, 1);
	    		}else{
	    			usercheck.put(uid, usercheck.get(uid)+1);
	    		}
	    	}

	    	temp=br.readLine();
	    }
		br.close();
		return usercheck;
	}

	// 返回一个map，key是用户id，value是评分时间戳
	public static Map<Integer,Double> readstampsum(String fileName)throws IOException{
		List<String> wordlist=new ArrayList<String>();
		List<String> authorlist = new ArrayList<>();
		List<String> publist = new ArrayList<>();

		Map<Integer,Double> stampsum=new HashMap<Integer,Double>();
		File read = new File(fileName);
	    BufferedReader br = new BufferedReader(new FileReader(read));
	    String temp=br.readLine();
	    while(temp!=null){
	    	String[] str=temp.split("\t");
	    	Integer uid=Integer.parseInt(str[0]);
	    	Double stamp=Double.parseDouble(str[2]);

	    	String word=str[3];
	    	String[] wset=word.split("\\.");
	    	for(int i=0;i<wset.length;i++){
	    		String wd=wset[i];
	    		if(!wordlist.contains(wd)){
    				wordlist.add(wd);
    			}
	    	}

			String authots = str[4];
			String[] aset = authots.split("\\.");
			for(int i = 0 ; i < aset.length ;i++) {
				String ad = aset[i];
				if (!authorlist.contains(ad)) {
					authorlist.add(ad);
				}
			}

			String ppub = str[5];
			if (!publist.contains(ppub)) {
				publist.add(ppub);
			}



			if(!stampsum.containsKey(uid)){
	    		stampsum.put(uid, stamp);
	    	}else{
	    		stampsum.put(uid, stampsum.get(uid)+stamp); // 为什么要将两个时间戳相加
	    	}

	    	temp=br.readLine();
	    }
	    br.close();
//	    wordlist.sort(null);
	    W=wordlist.size(); // wordlist 是标签的集合？W是标签数目

		A = authorlist.size(); // authorlist 是作者的集合
		P = publist.size(); // publist 是出版社的集合
		return stampsum;
	}

	public static Map<Integer,Map<Double,String>> readstamp(String fileName,String fileName1) throws IOException{
        Map<Integer,Double>stampsum=readstampsum(fileName1);
        Map<Integer,Map<Double,String>> checkins= new HashMap<Integer,Map<Double,String>>();
        File read = new File(fileName);
   	    BufferedReader br = new BufferedReader(new FileReader(read));
   	    String temp=br.readLine();
   	    while(temp!=null){
   	       String[] str=temp.split("\t");
   	       int uid=Integer.valueOf(str[0]);
    	   double stamp= Double.valueOf(str[2]);
    	   stamp/=stampsum.get(uid);
    	   String cid=str[4].split("\\|")[0];
    	   if(!checkins.containsKey(uid)){
    		   Map<Double,String> stamp_precid=new HashMap<Double,String>();
    		   stamp_precid.put(stamp, cid);
    		   checkins.put(uid, stamp_precid);
    	   }else{
    		   Map<Double,String> stamp_precid=checkins.get(uid);
    		   if(!stamp_precid.containsKey(stamp)){
    			   stamp_precid.put(stamp, cid);
    		   }
    	   }
   	       temp=br.readLine();
   	    }
        br.close();

        return checkins;
}

	/**
	 * 通过地理位置词典生成一个map，key 为位置id，value 是 类别，可能有多个类别，用·隔开
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
 public static Map<Integer,String> readPoiCat(String fileName)throws IOException{
	 Map<Integer,String> poicat=new HashMap<Integer,String>();
	 File read = new File(fileName);
	 BufferedReader br = new BufferedReader(new FileReader(read));
	 String temp=br.readLine();
	 while(temp!=null){
	   String[] str=temp.split("\t");
	   int pid=Integer.parseInt(str[1]);
//	   String cset=str[4].split("\\|")[1];
	   String cset=str[3];
	   if(!poicat.containsKey(pid)){ // 说明一个地点id和一个类别是一一对应的
		  poicat.put(pid, cset);
	   }
	   temp=br.readLine();
	 }
	 br.close();
	 return poicat;
 }

	/**
	 * 返回地点类别的Map，key为地点id，value是一个list，list中的元素是地点的类别 一个地点最多有4个word
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
    public static Map<Integer,List<Integer>> readPoiWord(String fileName)throws IOException{
    	Map<Integer,List<Integer>> poiwd= new HashMap<Integer,List<Integer>>();
    	File read = new File(fileName);
   	    BufferedReader br = new BufferedReader(new FileReader(read));
   	    String temp=br.readLine();
   	    while(temp!=null){
   	      String[] str=temp.split("\t");
   	      int pid=Integer.parseInt(str[1]);
   	      if(!poiwd.containsKey(pid)){
   	    	String word=str[3];
     	      String[] wset=word.split("\\.");
     	      List<Integer> tm=new ArrayList<Integer>();
     	      for(int i=0;i<wset.length;i++){
     	    	 int wid=Integer.parseInt(wset[i]);
   	    	  tm.add(wid);
     	      }
     	      poiwd.put(pid, tm);
   	      }

   	      temp=br.readLine();
   	    }
    	br.close();
    	return poiwd;
    }
    public static Train readSamples1(String fileName, String fileName1, int UserNum) throws IOException{
	    Map<Integer,Double>stampsum=readstampsum(fileName1);
	    Map<Integer,Integer> usercheck=readcheckinfo(fileName);
        Map<Integer,Integer> usersq=new HashMap<Integer,Integer>();
        int [][]sample= new int[UserNum][];
	    double[][] sample1=new double[UserNum][];
	    String[][] sample2=new String[UserNum][];
	    String[][] sample3=new String[UserNum][];
	    double[][] sample4=new double[UserNum][];
	    int[][] sample5= new int[UserNum][];

		String[][] sample6=new String[UserNum][];
		String[][] sample7=new String[UserNum][];

	    int[][] sample10=new int[UserNum][];
	    Train t= new Train();
	    File read = new File(fileName);
	    BufferedReader br = new BufferedReader(new FileReader(read));
        String temp=br.readLine();
        while(temp!=null){
        	String[] str=temp.split("\t");
        	int uid=Integer.parseInt(str[0]);
        	String wd=str[3];
        	String cd=str[4];

			String authord = str[5]; // 约定第5列 是 作者号
			String pubd = str[6]; // 约定第6列是 出版社 号
 
        	int num=usercheck.get(uid);
        	if(!usersq.containsKey(uid)){
        		sample[uid]=new int[num];	
	        	sample1[uid]=new double[num];
	        	sample2[uid]=new String[num];
	        	sample3[uid]=new String[num];
	        	sample4[uid]=new double[num];
	        	sample5[uid]=new int[num];
				sample6[uid] = new String[num];
				sample7[uid] = new String[num];
				sample10[uid]=new int[num];
	        	usersq.put(uid, 0);
	        	
        	}else{
        		usersq.put(uid, usersq.get(uid)+1);
        	}
        	
        	Integer pid=Integer.parseInt(str[1]);
        	sample[uid][usersq.get(uid)]=pid;
        	Double tid=Double.parseDouble(str[2]);
        	sample1[uid][usersq.get(uid)]=tid*1.0/stampsum.get(uid);
        	sample2[uid][usersq.get(uid)]=wd;
        	sample3[uid][usersq.get(uid)]=cd;

        	//TODO 加入两个 主题分布
        	sample6[uid][usersq.get(uid)]=authord;
			sample7[uid][usersq.get(uid)] = pubd;

			long t1=Long.parseLong(str[2]);
        	int week=CalculateWeekDay(t1);
        	sample5[uid][usersq.get(uid)]=week;
        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String sd = sdf.format(new Date(t1));
        	double hour=Double.parseDouble(sd.split(" ")[1].split(":")[0]);
        	if(hour==0){
        		sample4[uid][usersq.get(uid)]=0.01;
        	}else{
        		sample4[uid][usersq.get(uid)]=hour/24;
        	}
        	int loc=Integer.parseInt(str[6]);
        	sample10[uid][usersq.get(uid)]=loc;
        	temp=br.readLine();
        }
         br.close();  
            t.trainI=sample;
            t.trainT=sample1;
            t.trainW=sample2;
            t.trainC=sample3;
            t.trainJimT=sample4;
            t.trainS=sample5;
			t.trainAu = sample6;
			t.trainPub = sample7;
			t.trainL=sample10;
            return t;
       		
}
	public static Map<Integer,List<Integer>> readTest(String fileName){// key用户id，value去过地点的id集合，用于后续推荐
		File file = new File(fileName);
        BufferedReader reader = null;
        Map<Integer,List<Integer>> test = new HashMap<Integer,List<Integer>>();
        Set<Integer> set = new HashSet<Integer>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split("\t");
            	Integer gid = Integer.parseInt(temp[0]);
            	Integer eid = Integer.parseInt(temp[1]);
            	set.add(eid);
            	if(test.containsKey(gid))
            		test.get(gid).add(eid);
            	else{
            		List<Integer> list = new ArrayList<Integer>();
            		list.add(eid);
            		test.put(gid, list);
            	}
            }
            reader.close();

            return test;
        }catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}


	/**
	 * 返回一个二维数组，key通过map已经有序，相当于行下标等于地点id，每行的两个元素是经纬度
	 * @param fileName
	 * @return
	 */
	public static double[][]  readGeo(String fileName){
	    Map<Integer,double[]>map= new HashMap<Integer,double[]>();
		File file = new File(fileName);
		Set<Integer> user= new HashSet<Integer>(); // 用户集合
		Set<Integer> item= new HashSet<Integer>(); // 位置集合
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split("\t");
            	Integer uid = Integer.parseInt(temp[0]);
            	user.add(uid);
            	Integer eid = Integer.parseInt(temp[1]);
            	item.add(eid);
            	double lat=Double.parseDouble(temp[5]);
            	double lon=Double.parseDouble(temp[6]);
            	if(!map.containsKey(eid)){
            		double[] loc= new double[2];
            		loc[0]=lat;
            		loc[1]=lon;
            		map.put(eid, loc);
            	}
            }
            reader.close();
            U=user.size(); // 800个用户

            I=item.size(); // 6697 个地点
            double[][] geo= new double[map.size()][2];

            for(Integer eid:map.keySet()){
//            	geo[eid][0]=map.get(eid)[0];
            	geo[eid]=null;
//            	geo[eid][1]=map.get(eid)[1];

            }

            return geo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}

	public static Map<Integer,List<Integer>> readPoiAuthor(String fileName) throws IOException {
		Map<Integer,List<Integer>> poiwd= new HashMap<Integer,List<Integer>>();
		File read = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(read));
		String temp=br.readLine();
		while(temp!=null){
			String[] str=temp.split("\t");
			int pid=Integer.parseInt(str[1]);
			if(!poiwd.containsKey(pid)){
				String word=str[5];
				String[] wset=word.split("\\.");
				List<Integer> tm=new ArrayList<Integer>();
				for(int i=0;i<wset.length;i++){
					int wid=Integer.parseInt(wset[i]);
					tm.add(wid);
				}
				poiwd.put(pid, tm);
			}

			temp=br.readLine();
		}
		br.close();
		return poiwd;
	}

	public static Map<Integer,List<Integer>> readPoiPublisher(String fileName) throws IOException {
		Map<Integer,List<Integer>> poiwd= new HashMap<Integer,List<Integer>>();
		File read = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(read));
		String temp=br.readLine();
		while(temp!=null){
			String[] str=temp.split("\t");
			int pid=Integer.parseInt(str[1]);
			if(!poiwd.containsKey(pid)){
				String word=str[6];
				String[] wset=word.split("\\.");
				List<Integer> tm=new ArrayList<Integer>();
				for(int i=0;i<wset.length;i++){
					int wid=Integer.parseInt(wset[i]);
					tm.add(wid);
				}
				poiwd.put(pid, tm);
			}

			temp=br.readLine();
		}
		br.close();
		return poiwd;
	}
}
