package com.lyc.service;

import com.lyc.entity.Client;
import com.lyc.entity.ClientEntity;
import com.lyc.entity.ClientFeedBack;
import com.lyc.entity.ClientHistory;
import com.lyc.mapper.ClientMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Service
@Component
public class ClientService implements UserDetailsService {

    @Resource
    ClientMapper clientMapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    public boolean isUsernameDup(String username) {
        if (clientMapper.selectByUsername(username) == null) {
            return false;
        }
        return true;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClientEntity clientEntity = clientMapper.selectByUsername(username);
        if (clientEntity == null) {
            throw new UsernameNotFoundException("username not exist");
        }
        System.out.println(clientEntity);
        Client client = Client.of(clientEntity);
        return client;
    }

    // 0: 点击 2:喜欢   3:不喜欢    1:加入书单   6 - 10： 以0.5 为一个维度的评分，其中0.5 = 6
    public void addToUserHistory(String bookid, int type) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClientHistory clientHistory = new ClientHistory();
        clientHistory.setType(type);
        clientHistory.setBookId(bookid);
        clientHistory.setUserName(userDetails.getUsername());

        logger.error("用户"+userDetails.getUsername()+"加入书单");


        clientHistory.setCreateTime(new Date());
        clientMapper.insertHistory(clientHistory);
    }

    public List<ClientHistory> getMyFavourateBooks() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ClientHistory> raw =  clientMapper.selectBooksByUsername(userDetails.getUsername());
        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy年MM月dd日 HH:mm:ss " );
        raw.forEach(o -> o.setChinaTime(sdf.format(o.getCreateTime())));
        raw.forEach(new Consumer<ClientHistory>() {
            @Override
            public void accept(ClientHistory clientHistory) {
                switch (clientHistory.getType()) {
                    case 1:
                        clientHistory.setStatus("书单");
                        break;
                    case 2:
                        clientHistory.setStatus("喜欢");
                        break;
                    case 3:
                        clientHistory.setStatus("无感");
                        break;
                }
            }
        });
        return raw;
    }

    public void deleteHistoryById(int id) {
        clientMapper.deleteHistoryById(id);
    }

    public void insertComments(ClientFeedBack clientFeedBack) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        clientFeedBack.setUserName(userDetails.getUsername());
        clientFeedBack.setCreateTime(new Date());
        clientMapper.insertComments(clientFeedBack);
    }

    public void insertLabels(ClientFeedBack clientFeedBack) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        clientFeedBack.setUserName(userDetails.getUsername());
        clientFeedBack.setCreateTime(new Date());

        try {
            clientMapper.insertLabels(clientFeedBack);
        } catch (Exception exception) {
            System.out.println(exception);
        }

    }
}
