package com.lyc.global;

import com.lyc.entity.ClientEntity;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;

public class GlobalUser {

    private static ClientEntity currentUser;

    public static void setCurrentUser(ClientEntity currentUser) {
        GlobalUser.currentUser = currentUser;
    }

    public static ClientEntity getCurrentUser(){
        return currentUser;
    }
}
