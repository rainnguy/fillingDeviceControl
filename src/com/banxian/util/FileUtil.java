package com.banxian.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	private FileUtil() {
	}

	private static final FileUtil single = new FileUtil();

	// 静态工厂方法
	public static FileUtil getInstance() {
		return single;
	}
	
	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @param fileName
	 */
    public List<String> readFileByLines(String fileName) {
    	
    	List<String> dataList = new ArrayList<String>();
    	
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	dataList.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        return dataList;
    }
    
    public static void main(String[] args) {
        String fileName = "C:\\Users\\xk\\Documents\\stationDataCache\\123_20160426153816.csv";
//        readFileByBytes(fileName);
//        readFileByChars(fileName);
//        readFileByLines(fileName);
//        readFileByRandomAccess(fileName);
    }
}
