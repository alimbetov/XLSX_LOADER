package xparser;

import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FlowManager {


public void flowGenerate(List<File> lf){
        System.out.println(" lf size"+lf.size());
    List<File> light= lf.stream().filter(f->  (f.length()/1024)<10000).collect(Collectors.toList());
    System.out.println(" light size"+light.size());
    List<File> heavy= lf.stream().filter(f->  (f.length()/1024)>=10000).collect(Collectors.toList());
    System.out.println(" heavy size"+heavy.size());
    flowstart  (light,4);
    flowstart  (heavy,1);
}

public  void flowstart(List<File> lf,int nThreads){
    System.out.println(" flow started nThreads= "+ nThreads);
    ExecutorService es= Executors.newFixedThreadPool(nThreads);
    try {
        lf.stream().parallel().forEach(
                f-> es.submit(new Worker(f))
        );
        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}



    public void jflowGenerate(List<File> lf){



        System.out.println(" lf size"+lf.size());
        List<File> light= lf.stream().filter(f->  (f.length()/1024)<10000).collect(Collectors.toList());
        System.out.println(" light size"+light.size());
        List<File> heavy= lf.stream().filter(f->  (f.length()/1024)>=10000).collect(Collectors.toList());
        System.out.println(" heavy size"+heavy.size());
        jflowstart  (light,10);
        jflowstart  (heavy,2);
    }


    public  void jflowstart(List<File> lf,int nThreads){
        System.out.println(" flow started nThreads= "+ nThreads);
        ExecutorService es= Executors.newFixedThreadPool(nThreads);
        try {
            lf.stream().parallel().forEach(
                    f-> es.submit(new Jocker(f))
            );
            es.shutdown();
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
