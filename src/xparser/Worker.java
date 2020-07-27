package xparser;

import java.io.File;

public class Worker implements Runnable {
    File file;
    FileManager fm;
    public Worker(File file) {
        this.file = file;
        this.fm=new FileManager();
    }

    @Override
    public void run() {
        try {
            System.out.println(" Woker for " + file.getName() + " Started ");
            System.out.println("file size="+file.length()/1024);
            fm.parsingFile(file);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" Woker for " + file.getName() + " finshed ");

    }
}
