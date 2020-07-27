package xparser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Jocker implements Runnable {
    File file;
    FileManager fm;

    public Jocker(File file) {
        this.file = file;
        this.fm=new FileManager();
    }

    @Override
    public void run() {

        try {
            Thread.sleep(10);

            int [] result = fm.executeSqlBatches(fm.getSqlPacks(this.file),this.file);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
