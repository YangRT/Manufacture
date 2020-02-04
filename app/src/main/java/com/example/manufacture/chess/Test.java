package com.example.manufacture.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {
    private static List<String> successResult = new ArrayList<>();
    private static List<String> data = new ArrayList<>();
    /*
          A   B   C
          D   E   F
          G   H   I
     */
    public static void main(String[] args) {
        initData();
        String tmp = "";
        data.add("B");
        data.add("E");
        data.add("H");
        data.add("I");
        searchResult(data,tmp,0);
    }

    private static void initData(){
        successResult.add("ABC");
        successResult.add("DEF");
        successResult.add("GHI");
        successResult.add("ADG");
        successResult.add("BEH");
        successResult.add("CFI");
        successResult.add("AEI");
        successResult.add("CEG");

    }

    private static void searchResult(List<String> user2Selected,String tmp,int index) {
        if(tmp.length() == 3){
            System.out.println(tmp);
            if(successResult.contains(tmp)){
                System.out.println(tmp+" is true");
            }
            return;
        }
        for(int i = index;i < user2Selected.size();i++){
            tmp += user2Selected.get(i);
            searchResult(user2Selected,tmp,i+1);
            tmp = tmp.substring(0,tmp.length()-1);
        }

    }
}
