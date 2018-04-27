package com.irootech.runner;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Yan
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static String FILE_LOCATION = "/data/111.xlsx";

    public static void main(String[] args) throws Exception {

        if (args.length != 3){
            return;
        }

        int concurrecy = Integer.parseInt(args[0]);
        String startDate = args[1];
        String endDate = args[2];

        FileInputStream file = new FileInputStream(new File(FILE_LOCATION));
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(1);

        Map<String, List<String>> services = new HashMap<>();
        String serviceName = "";
        String interfaceName = "";
        for (Row row : sheet) {
            if (row.getCell(2).toString().length() > 0) {

                serviceName = row.getCell(2).toString();
                interfaceName = row.getCell(5).toString();
                if (interfaceName.length() == 0) {
                    interfaceName = serviceName;
                }
                List<String> interfaces = new ArrayList<>();
                interfaces.add(interfaceName);
                services.put(serviceName, interfaces);
            } else {
                interfaceName = row.getCell(5).toString();
                List<String> interfaces = services.get(serviceName);
                if (interfaceName.length() == 0) {
                    interfaceName = serviceName;
                }
                interfaces.add(interfaceName);
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(concurrecy);

        Iterator iter = services.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String service = (String)entry.getKey();
            List interfaces = (List)entry.getValue();
            pool.submit(new Job(service, interfaces, startDate, endDate));
            System.out.println(service + " - " + String.join(",", interfaces));
        }
        pool.shutdown();
    }

}

class Job implements Runnable{

    public String startDate;
    public String endDate;
    public String serviceName;
    public List interfaces;

    public Job(String serviceName, List interfaces, String startDate, String endDate) {
        this.serviceName = serviceName;
        this.interfaces = interfaces;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        System.out.println(serviceName + " -- " + String.join(",", interfaces));
        try {
            new Task().run(serviceName, interfaces, startDate, endDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

