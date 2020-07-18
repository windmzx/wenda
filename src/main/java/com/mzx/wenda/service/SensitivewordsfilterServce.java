package com.mzx.wenda.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mengz on 2017/5/22.
 */
@Service
@Slf4j
public class SensitivewordsfilterServce implements InitializingBean {

    public static final String REPALCESTRING = "*敏感*";

    TiredTreeNode rootNode = new TiredTreeNode();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始初始化敏感词检查");
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Sensiveword.text");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                addWord(line);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("敏感词初始化失败" + e.getMessage());
        }
    }

    class TiredTreeNode {
        private boolean isEnd = false;
        private Map<Character, TiredTreeNode> subNodes = new HashMap<>();

        void addNode(Character key, TiredTreeNode value) {
            subNodes.put(key, value);
        }

        TiredTreeNode getNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return isEnd;
        }

        void setEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }

        int getNodeCount() {
            return subNodes.size();
        }

    }

    void addWord(String word) {
        int size = word.length();
        TiredTreeNode temp = rootNode;
        for (int i = 0; i < size; i++) {
            Character c = word.charAt(i);
            TiredTreeNode p = temp.getNode(c);
            if (p == null) {
                p = new TiredTreeNode();
                temp.addNode(c, p);
            }
            temp = p;
            if (i == size - 1)
                temp.setEnd(true);
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text))
            return text;
        String replacestr = REPALCESTRING;
        StringBuilder sb = new StringBuilder();
        TiredTreeNode tempnode = rootNode;

        int begin = 0;
        int position = 0;
        while (position < text.length()) {
            Character c = text.charAt(position);
            tempnode = tempnode.getNode(c);

            if (tempnode == null) {
                sb.append(c);
                position = begin + 1;
                begin = position;
                tempnode = rootNode;
            } else if (tempnode.isKeywordEnd()) {
                sb.append(replacestr);
                position = position + 1;
                begin = position;
                tempnode = rootNode;
            } else {
                ++position;
            }
        }

        sb.append(text.substring(begin));
        return sb.toString();

    }

//    public static void main(String[] args) {
//      //  InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Sensiveword.text");
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Sensiveword.text"))));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        String line;
//        SensitivewordsfilterServce sensitivewordsfilterServce = new SensitivewordsfilterServce();
//        try {
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                sensitivewordsfilterServce.addWord(line);
//            }
//
//            System.out.println(sensitivewordsfilterServce.filter("你好"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }

}
