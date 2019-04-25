package com.lyc.algorithm.Time_Item_Recommender.book_lda;

import com.lyc.service.RedisService;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.python.google.common.primitives.Ints;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JIMVersion3 {
    int K;
    int S;//潜在时间间隔数
    int R;//潜在区域数量
    double alpha;
    double beta;
    double o = 0.01;
    double varepsilon = 0.01;
    double varepsilon_a = 0.01;
    double varepsilon_p = 0.01;


    double tau = 0.01;
    double phi = 0.01;

    int iterNum = 200;
    int U, I, W, T, A, P;
    int[][] Zu; //每个用户的主题
    int[][] Ru; //每个用户每次签到的区域
    int[] Reg;//每个区域的签到数量

    int[][] nzc; //每个主题z中每个标签的数量

    int[][] nzca; // 每个主题z 中每个作者 的数量
    int[][] nzcp; // 每个主题z 中每个出版商 的数量

    int[][] nrv;//每个区域含有的POI个数
    int[][] nzt;
    int[][] nzv;


    double[][] miuR;
    double[][] sigmaR;


    int[][] nuz;

    int[][] nur;

    int[] phisum; //  u 的 所有签到 记录数 ++
    int[] thetasum; //  u 的 所有签到 记录数 ++
    int[] jimphisum;//  z 的所有签到记录数
    int[] lamdasum; // z 的所有签到 记录数

    int[] etasum;// z 的 所有词汇 记录数
    int[] eta_asum; // z 的 所有作者 记录数
    int[] eta_psum; // z 的 所有出版商 记录数

    int[] omigasum; // r 的所有签到 记录 数

    Train train;
    Train test;
    Map<Integer, List<Integer>> trainmap;
    Map<Integer, List<Integer>> testmap;
    Map<Integer, String> poiCat;
    Map<Integer, List<Integer>> poiWd;
    Map<Integer, List<Integer>> poiAd;
    Map<Integer, List<Integer>> poiPd;


    double[][] geo; //每个位置的经纬度
    Input1 input;

    class Input1 {
        public String trainfile;
        public String testfile;
        public String geofile;
//	public String friendfile;

        public Input1(String matrix) {
            if ("bookdata".equals(matrix)) {
                trainfile = String.format("F:\\Data\\book\\train.txt", matrix);
                testfile = String.format("F:\\Data\\book\\test.txt", matrix);
                geofile = String.format("F:\\Data\\book\\data.txt", matrix);
//			friendfile=String.format("F:\\Data\\Yelp\\%s\\hometown\\friend.txt",matrix);
            }
        }
    }

    public JIMVersion3() throws IOException {
        this.K = 8;
        this.R = 220;
        this.alpha = 50.0 / K;
        this.beta = 50.0 / R;

        this.input = new Input1("bookdata");
        poiCat = Dataset1.readPoiCat(input.geofile);
        poiWd = Dataset1.readPoiWord(input.geofile);
        poiAd = Dataset1.readPoiAuthor(input.geofile); // 读所有作者信息
        poiPd = Dataset1.readPoiPublisher(input.geofile); // 读所有出版社信息


        geo = Dataset1.readGeo(input.geofile);
        train = Dataset1.readSamples(input.trainfile, input.geofile);
        trainmap = Dataset1.readTest(input.trainfile);
        U = Dataset1.U;
        I = Dataset1.I;
        W = Dataset1.W; // 标签数
        A = Dataset1.A; // 作者数目
        P = Dataset1.P; // 出版商数目


        T = 7;

        System.out.println("单词数" + W);
        Zu = new int[U][]; //每个用户的主题
        Ru = new int[U][]; //每个用户每次签到的区域

        Reg = new int[R];//每个区域的签到数量

        nzc = new int[K][W]; //每个主题z含有的标签的数量

        nzca = new int[K][A]; // 每个主题z含有的作者的数量
        nzcp = new int[K][P]; // 每个主题z含有的出版社的数量

        nzt = new int[K][T];

        nrv = new int[R][I];//每个区域含有的POI个数


        nzv = new int[K][I];
        miuR = new double[R][2];
        sigmaR = new double[R][2];


        nuz = new int[U][K];
        nur = new int[U][R];
        phisum = new int[U];
        lamdasum = new int[K];
        thetasum = new int[U];
        jimphisum = new int[K];

        etasum = new int[K];
        eta_asum = new int[K];
        eta_psum = new int[K];
        omigasum = new int[R];


        //test=Dataset.readstamp(input.testfile,input.geofile);
        test = Dataset1.readSamples1(input.testfile, input.geofile, U);
        testmap = Dataset1.readTest(input.testfile);


        initialize();
        for (int iter = 0; iter < iterNum; iter++) {
            gibbs1();
            gibbs2();
        }
        System.out.println("主题训练完成");

    }

    public void initialize() {
        Random rand = new Random();
        for (int u = 0; u < U; u++) {
            if (trainmap.containsKey(u)) {
                int[] Nu = train.trainI[u]; // 训练集  用户 u的所有 签到记录集合
                //  用户 的 一次签到肯定属于 第 0 ~ K - 1 号主题，同理 得到 R
                Zu[u] = new int[Nu.length]; // 为 该用户 u 的 每一个 签到记录搞一个 int 类型的主题
                Ru[u] = new int[Nu.length]; // 为 该用户 u 的 每一个 签到记录在R 隐含语义上搞一个主题
                for (int i = 0; i < Nu.length; i++) {
                    int z = rand.nextInt(K); // 随便初始化一个主题 z 是从 K 个topic 随机出来的 一个 主题
                    int r = rand.nextInt(R);// 随便初始化一个主题 r 是从 R 个topic 随机出来的 一个 区域（事先不知道是哪个主题）
                    Zu[u][i] = z; // 随便初始化一个主题

                    Ru[u][i] = r;// 随便初始化一个主题
                    Reg[r]++; // 每个 R　中签到的数量　


                    String[] cset = train.trainW[u][i].split("\\."); // 拆用户u在i的 内容标签，看看都有哪些内容
                    for (String cd : cset) {
                        nzc[z][Integer.parseInt(cd)]++; // nzc 代表了K个主题，每个主题有W个词汇，对应那个主题的词汇的数量++
                        etasum[z]++;  // z 主题上 总共的词汇数 ++。因为一个z主题可以有很多词汇的分布，etasum求其词汇总数
                    }

                    String[] aset = train.trainAu[u][i].split("\\.");
                    for (String ad : aset) {
                        nzca[z][Integer.parseInt(ad)]++;
                        eta_asum[z]++; // z 主题上 总共的作者 数目 ++ 。 因为一个z 主题可以有很多作者的分布，对用那个作者的数量++
                    }

                    String[] pset = train.trainPub[u][i].split("\\.");
                    for (String pd : pset) {
                        nzcp[z][Integer.parseInt(pd)]++;
                        eta_psum[z]++;
                    }


                    int interval = train.trainS[u][i]; // 去用户u 在 i 上是星期几 签的到
                    nzt[z][interval]++; // z 主题 上 总共有 几个 星期几 的 个数 ++

                    nrv[r][Nu[i]]++; // r 主题 上 总共地点的 个数 ++
                    nzv[z][Nu[i]]++; // z 主题 上 总共地点的 个数 ++

                    nur[u][r]++; // 用户u 在 r 主题上的个数 ++
                    nuz[u][z]++; // 用户 u 的 z 的 主题个数   ++

                    //FIXME 这个不就是相当于 phisum[u] = Nu.length 吗 ？
                    phisum[u]++;  // u 的 所有签到 记录数 ++
                    //FIXME 这个不就是相当于 thetasum[u] = Nu.length 吗 ？
                    thetasum[u]++;
                    //FIXME 这个不就是相当于 lamdasum[z] = Nu.length 吗 ？
                    lamdasum[z]++;
                    //FIXME 这个不就是相当于 lamdasum[z] = Nu.length 吗 ？
                    jimphisum[z]++;
                    //FIXME 这个不就是相当于 omigasum[r] = Nu.length 吗 ？
                    omigasum[r]++;

                }
            }
        }

        // 即然没有经纬度 了，也就用不着求 经纬度的均值和方差了
        for (int r = 0; r < R; r++) {
            miuR[r][0] = 33.7 + rand.nextFloat();
            miuR[r][1] = -118.3 + rand.nextFloat();
            sigmaR[r][0] = rand.nextFloat() * 10;
            sigmaR[r][1] = rand.nextFloat() * 10;
        }

    }

    public void gibbs1() {

        for (int u = 0; u < U; u++) {
            int[] Nu = train.trainI[u];
            for (int i = 0; i < Nu.length; i++) {
                double[] p = new double[K];
                int z = Zu[u][i];
                int interval = train.trainS[u][i];


                String word = train.trainW[u][i];
                String[] wset = word.split("\\.");
                for (int w = 0; w < wset.length; w++) {
                    int wid = Integer.parseInt(wset[w]);
                    nzc[z][wid]--;
                    etasum[z]--;
                }

                String author = train.trainAu[u][i];
                String[] aset = author.split("\\.");
                for (int w = 0; w < aset.length; w++) {
                    int wid = Integer.parseInt(aset[w]);
                    nzca[z][wid]--;
                    eta_asum[z]--;
                }

                String pub = train.trainPub[u][i];
                String[] pset = pub.split("\\.");
                for (int w = 0; w < pset.length; w++) {
                    int wid = Integer.parseInt(pset[w]);
                    nzcp[z][wid]--;
                    eta_psum[z]--;
                }


                nuz[u][z]--;
                nzv[z][Nu[i]]--;
                lamdasum[z]--;
                nzt[z][interval]--;
                jimphisum[z]--;

                phisum[u]--;
                for (int k = 0; k < K; k++) {
                    p[k] = (nuz[u][k] + alpha) / (phisum[u] + K * alpha)
                            * (nzt[k][interval] + phi) / (jimphisum[k] + T * phi)
                            * (nzv[k][Nu[i]] + tau) / (lamdasum[k] + I * tau);
                    for (int w = 0; w < wset.length; w++) {
                        int wid = Integer.parseInt(wset[w]);
                        p[k] *= (nzc[k][wid] + varepsilon) / (etasum[k] + W * varepsilon);

                    }

                    // FIXME pk 城等这么多不会特别小吗
                    // 以下是自己添加的
                    for (int w = 0; w < aset.length; w++) {
                        int wid = Integer.parseInt(aset[w]);
                        p[k] *= (nzca[k][wid] + varepsilon_a) / (eta_asum[k] + A * varepsilon_a);

                    }
                    for (int w = 0; w < pset.length; w++) {
                        int wid = Integer.parseInt(wset[w]);
                        p[k] *= (nzc[k][wid] + varepsilon_p) / (eta_psum[k] + P * varepsilon_p);

                    }

                }
                Util.norm(p);
                z = draw(p);
                Zu[u][i] = z;

                for (int w = 0; w < wset.length; w++) {
                    int wid = Integer.parseInt(wset[w]);
                    nzc[z][wid]++;
                    etasum[z]++;
                }
                for (int w = 0; w < aset.length; w++) {
                    int wid = Integer.parseInt(aset[w]);
                    nzca[z][wid]++;
                    eta_asum[z]++;
                }
                for (int w = 0; w < pset.length; w++) {
                    int wid = Integer.parseInt(pset[w]);
                    nzcp[z][wid]++;
                    eta_psum[z]++;
                }


                nzt[z][interval]++;
                jimphisum[z]++;
                phisum[u]++;
                nuz[u][z]++;
                nzv[z][Nu[i]]++;
                lamdasum[z]++;
            }
        }

    }


    public static double betasampler(double alpha, double beta) {
        BetaDistribution Beta = new BetaDistribution(alpha, beta);
        return Beta.sample();
    }

    public void gibbs2() {

        for (int u = 0; u < U; u++) { // 遍历每个用户 u
            int[] Nu = train.trainI[u]; // Nu 是每个用户 的所有签到记录
            for (int i = 0; i < Nu.length; i++) { // 遍历每个 签到 记录 i
                double[] p = new double[R]; // 生成 R　个潜在主题　ｐ
                int r = Ru[u][i]; //  获得 u-i 的 主题 r
                Reg[r]--;

                nrv[r][Nu[i]]--;

                nur[u][r]--;
                thetasum[u]--;
                omigasum[r]--;
                for (int k = 0; k < R; k++) {
                    p[k] = (nur[u][k] + beta) / (thetasum[u] + R * beta) *
                            (nrv[k][Nu[i]] + o) / (omigasum[k] + I * o);
                }
                // 训练这个R ，更新 ui 所属于的 R 隐含主题
                Util.norm(p);
                r = draw(p);
                Ru[u][i] = r;
                Reg[r]++;

                nrv[r][Nu[i]]++;
                nur[u][r]++;
                omigasum[r]++;
                thetasum[u]++;
            }
        }

//		 for(int r=0;r<R;r++)
//			 updateRegGaussian(r);
    }

    public void updateRegGaussian(int r) {

        List<Integer> lr = new ArrayList<Integer>();
        for (int u = 0; u < train.trainI.length; u++) {
            for (int or = 0; or < train.trainI[u].length; or++) {
                if (Ru[u][or] == r)
                    lr.add(train.trainI[u][or]);
            }
        }

        if (lr.size() <= 1) {
            return;
        }
        miuR[r][0] = 0;
        miuR[r][1] = 0;
        sigmaR[r][0] = 0;
        sigmaR[r][1] = 0;
        for (int e : lr) {
            miuR[r][0] += geo[e][0];
            miuR[r][1] += geo[e][1];
        }
        miuR[r][0] /= lr.size();
        miuR[r][1] /= lr.size();
        for (int e : lr) {
            sigmaR[r][0] += (geo[e][0] - miuR[r][0]) * (geo[e][0] - miuR[r][0]);
            sigmaR[r][1] += (geo[e][1] - miuR[r][1]) * (geo[e][1] - miuR[r][1]);
        }
        sigmaR[r][0] /= lr.size();
        sigmaR[r][1] /= lr.size();
        if (sigmaR[r][0] == 0) {
            sigmaR[r][0] = 1;
            sigmaR[r][1] = 1;
        }
    }

    public int draw(double[] a) {
        double r = Math.random();
        for (int i = 0; i < a.length; i++) {
            r = r - a[i];
            if (r < 0) return i;
        }
        return a.length - 1;
    }

    public double pdf(int e, int r) {
        double x = geo[e][0] - miuR[r][0];
        double y = geo[e][1] - miuR[r][1];
        double temp = Math.exp(-0.5 * ((x * x) / (sigmaR[r][0] * sigmaR[r][0]) + (y * y) / (sigmaR[r][1] * sigmaR[r][1])));
        return temp / (2 * 3.142 * Math.sqrt(sigmaR[r][0] * sigmaR[r][1]));
    }


    public int[] returnReg(double[][] omiga) {
        int[] pReg = new int[I];
        for (int p = 0; p < I; p++) {
            double max = 0;
            int r = 0;
            for (int i = 0; i < R; i++) {
                if (max < omiga[i][p]) {
                    max = omiga[i][p];
                    r = i;
                }
            }
            pReg[p] = r;
        }

        return pReg;
    }


    public double[][] estParameter(int[][] m, int[] sum, double sp) {
        // 将 m 进行归一化处理，其中 每个 元素等于 其本身 除以一行元素的总和 ，底下一行元素每个数字都 加 sp
        double[][] p = new double[m.length][m[0].length];

        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++) {
                p[i][j] = (m[i][j] + sp) / (sum[i] + m[i].length * sp);
            }

        return p;
    }

    public Model getModel() throws IOException {
        Model model = new Model();


        model.eta = estParameter(nzc, etasum, varepsilon);
        model.eta_a = estParameter(nzca, eta_asum, varepsilon_a);
        model.eta_p = estParameter(nzcp, eta_psum, varepsilon_p);
        model.Jimphi = estParameter(nzt, jimphisum, phi);
        model.lamda = estParameter(nzv, lamdasum, tau);
        model.omiga = estParameter(nrv, omigasum, o);

        model.phi = estParameter(nuz, phisum, alpha);

        model.theta = estParameter(nur, thetasum, beta);

        model.miuR = miuR;
        model.sigmaR = sigmaR;

//        for (int r = 0; r < R; r++) {
//            System.out.println("r" + r + "\t" + model.miuR[r][0] + "\r");
//            System.out.println("r" + r + "\t" + model.miuR[r][1] + "\r");
//            System.out.println("r" + r + "\t" + model.sigmaR[r][0] + "\r");
//            System.out.println("r" + r + "\t" + model.sigmaR[r][1] + "\r");
//        }

        return model;


    }

    public Map<Integer, Map<Integer, int[]>> recommend(Model model, int topn) throws IOException {

        double[][][] score = new double[U][][];
        int[] pReg = returnReg(model.omiga);
        String filePath = "F:\\Data\\Yelp\\outoftown\\outoftown\\prMap.txt";
        File write = new File(filePath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(write));
        for (int p = 0; p < I; p++) {
            bw.write(p + "\t" + pReg[p] + "\r");
        }
        bw.close();
        for (int i : testmap.keySet()) {
            score[i] = new double[test.trainT[i].length][I];
            for (int m = 0; m < testmap.get(i).size(); m++) {
                int ur = pReg[testmap.get(i).get(m)]; // 用户i 在
                int interval = test.trainS[i][m];
                for (int p = 0; p < I; p++) {
                    if (trainmap.get(i) == null || trainmap.get(i).contains(p)) continue;
                    int reg = pReg[p];
                    if (ur == reg) {
                        List<Integer> wset = poiWd.get(p);
                        List<Integer> aset = poiAd.get(p);
                        List<Integer> pset = poiPd.get(p);
                        double sscore = 0;
                        for (int k = 0; k < K; k++) {

                            double wscore = 1;
                            for (int w = 0; w < wset.size(); w++) {
                                wscore *= model.eta[k][w];
                            }
                            wscore = Math.pow(wscore, 1.0 / wset.size());

                            for (int w = 0; w < aset.size(); w++) {
                                wscore *= model.eta_a[k][w];
                            }
                            wscore = Math.pow(wscore, 1.0 / aset.size());

//                            for(int w = 0 ;  w < pset.size() ; w++) {
//                                wscore *= model.eta_p[k][w];
//                            }
//                            wscore = Math.pow(wscore, 1.0 / pset.size());

                            wscore *= model.phi[i][k] * model.Jimphi[k][interval] * model.lamda[k][p];
                            sscore += wscore;
                        }
                        double rscore = 0;
                        for (int r = 0; r < R; r++) {
                            rscore += model.omiga[r][p] * 0.002;
                        }
                        score[i][m][p] = sscore * rscore;
                    }
                }
            }

//            System.out.println("user" + i + " " + "complete");
        }


        Map<Integer, Map<Integer, int[]>> list = new HashMap<Integer, Map<Integer, int[]>>();
        for (int i : testmap.keySet()) {
            for (int j = 0; j < test.trainT[i].length; j++) {
                int[] index = Util.argSort(score[i][j], topn); // 返回 用户所有 的 top 评分
                if (!list.containsKey(i)) { // i 代表了 测试集 里面的用户 id
                    Map<Integer, int[]> tem = new HashMap<Integer, int[]>();
                    tem.put(j, index); // j 代表了测试集合里面 的 每个用户 的 签到id
                    list.put(i, tem);
                } else {
                    Map<Integer, int[]> tem = list.get(i);
                    tem.put(j, index);
                }
            }

        }

        return list;
    }


    public void evaluate(Map<Integer, Map<Integer, int[]>> lists, Integer userId, List<Integer> recommendedbooks) {

        for (int uid : lists.keySet()) {
            for (int order : lists.get(uid).keySet()) {
                if (order == 0 && userId == uid) {
                    for (int i : lists.get(uid).get(order)) {
                        recommendedbooks.add(i);
                    }
                }
            }
        }
    }


    public List<Integer> main_(int userId) throws IOException {
        Map<Integer, Map<Integer, int[]>> list = recommend(getModel(), 10);
        List<Integer> recommendedBooks = new ArrayList<>();
        evaluate(list, userId, recommendedBooks);
        return recommendedBooks;
    }

    public List<String> getRecommendedResult(String userId) throws IOException {
        RedisService redisService = new RedisService();
        String seq = redisService.hget("userId_to_seq", userId);

        System.out.println(seq);

        List<Integer> list = main_(Integer.parseInt(seq));
        List<String> books = list.stream().map(o -> redisService.hget("seq_to_bookId", o + "")).collect(Collectors.toList());
        return books;
    }

    @Deprecated
    private static void getMaps(String fileName1, String fileName2) throws IOException {

        File file1 = new File(fileName1);
        File file2 = new File(fileName2);

        Map<String, String> usermap = new HashMap<>();
        Map<String, String> bookmap = new HashMap<>();

        BufferedReader reader1 = null;
        BufferedReader reader2 = null;

        try {
            reader1 = new BufferedReader(new FileReader(file1));
            reader2 = new BufferedReader(new FileReader(file2));

            String line1 = null;
            String line2 = null;

            while (true) {
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                if (line1 == null || line2 == null) {
                    break;
                }

                String[] temp1 = line1.split("\t");
                String[] temp2 = line2.split("\t");

                usermap.put(temp2[0], temp1[0]);
                bookmap.put(temp1[1], temp2[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader1.close();
            reader2.close();
        }

        System.out.println("usermap:" + usermap.size());
        System.out.println("bookmap:" + bookmap.size());

        RedisService redisService = new RedisService();
        redisService.addToMap(usermap, "userId_to_seq");
        redisService.addToMap(bookmap, "seq_to_bookId");

    }


//     阿里云服务器redis数据丢失的时候运行以下方法将 用户id映射到seq，并将图书id映射到seq
    public static void main(String[] args) throws IOException {

        File file = new File("F:\\data_origin.txt");

        Map<String, String> usermap = new HashMap<>();
        Map<String, String> bookmap = new HashMap<>();

        BufferedReader reader = null;

        String line;
        int useq = 0;
        int bseq = 0;

        try {
            reader = new BufferedReader(new FileReader(file));

            while ( ( line = reader.readLine() )  != null) {

                String[] temp = line.split("\t");

                if (usermap.get(temp[0]) == null) {
                    usermap.put(temp[0],  String.valueOf(useq++ ));
                }
                if (bookmap.get(temp[1]) == null) {
                    bookmap.put(temp[1] , String.valueOf(bseq++ ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        RedisService redisService = new RedisService();
        Jedis jedis = null;
        try {
            jedis = new Jedis("39.106.39.216", 6379);

            for (Map.Entry<String ,String> e : usermap.entrySet()) {
                jedis.hset("userId_to_seq", e.getKey(), e.getValue());
            }

            for (Map.Entry<String ,String> t : bookmap.entrySet()) {
                jedis.hset("seq_to_bookId", t.getValue(), t.getKey());
            }


        }finally {
            jedis.close();
        }

    }

}
