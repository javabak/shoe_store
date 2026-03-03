//package com.example.shoe_store.service;
//
//import com.example.shoe_store.model.User;
//
//public class Session {
//
//    private static User currentUser;
//
//    public static void setCurrentUser(User user) {
//        currentUser = user;
//    }
//
//    public static User getCurrentUser() {
//        return currentUser;
//    }
//
//    public static void logout() {
//        currentUser = null;
//    }
//}

package com.example.shoe_store.service;

import com.example.shoe_store.model.User;

public class Session {

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}