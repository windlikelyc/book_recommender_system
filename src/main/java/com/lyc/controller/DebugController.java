package com.lyc.controller;

import com.github.pagehelper.Page;
import com.lyc.entity.Dbook;
import com.lyc.mapper.BookMapper;
import com.lyc.service.DBookService;
import com.lyc.service.RedisService;
import com.lyc.tmp.testAspectJ.User;
import com.lyc.tmp.testAspectJ.UserService;
import com.lyc.tmp.testAspectJ.UserVaildator;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/20
 * TIME 17:28
 */
@Controller
public class DebugController {

    @Autowired
    RedisService redisService;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    DBookService dBookService;

    @Autowired
    UserService userService;

    @RequestMapping("/debug")
    @ResponseBody
    public String debug(ModelMap modelMap) {
        redisService.filterDislikeBooks("fengliulyc", "1007760");
        return "这是用来测试的页面";
    }

    @RequestMapping("/testap")
    @ResponseBody
    public User printUser(Long id, String userName, int age) {
        User user = new User();
        user.setId(id);
        user.setName(userName);
        user.setAge(age);
        userService.printUser(null);
        return user;
    }

    @RequestMapping("/testap2")
    @ResponseBody
    public User printUser2(Long id, String name, int age) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        UserVaildator userVaildator = (UserVaildator) userService;
        if ((userVaildator).validate(null)) {
            userService.printUser(user);
        }
        return user;
    }

    @RequestMapping("/para")
    public String testPara() {
        return "para";
    }

    @RequestMapping("/testPageHelper")
    @ResponseBody
    public String testPH() {
        Page<Dbook> page = dBookService.findByPage(100, 10);
        System.out.println(page);
        return "123";
    }


    public static String rec() throws TasteException, IOException {

        DataModel dataModel = new FileDataModel(new File("src/main/java/com/lyc/doubandata/out.dat"));

        UserSimilarity userSimilarity =
                new PearsonCorrelationSimilarity(dataModel);

        Factorizer factorizer = new ALSWRFactorizer(dataModel, 10, 0.05 , 10);

        Recommender recommender =
                new SVDRecommender(dataModel, factorizer);

        List<RecommendedItem> recommendedItems =
                recommender.recommend(123, 10);

        RecommenderEvaluator recommenderEvaluator
                = new AverageAbsoluteDifferenceRecommenderEvaluator();

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return recommender;
            }
        };

        DataModelBuilder dataModelBuilder = new DataModelBuilder() {
            @Override
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                return dataModel;
            }
        };

        double eval = recommenderEvaluator.evaluate(recommenderBuilder, dataModelBuilder,dataModel, 0.9, 1.0);
        System.out.println("经过测试结果是 " + eval);

        System.out.println(recommendedItems);


        return null;
    }

    public void rec2() throws IOException {

        DataModel dataModel = new FileDataModel(new File("src/main/java/com/lyc/doubandata/out.dat"));

        RecommenderIRStatsEvaluator ev = new GenericRecommenderIRStatsEvaluator();

    }

    public static void main(String[] args) throws IOException, TasteException {
        rec();
    }


}
