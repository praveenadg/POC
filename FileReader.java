/** To read files in a directory line by line and match words
  **/
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReadTest {

    class FileObject {
        private String module;
        private String fileName;
        private String line;

        FileObject(String module, String fileName, String line) {
            this.module = module;
            this.fileName = fileName;
            this.line = line;
        }
    }

    List<FileObject> fileObjectList = new ArrayList<>();

    private void readFIle(File file) {
        try {
            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("daodef")) {
                Scanner scanner = new Scanner(file);

                //now read the file line by line...
                int lineNum = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    lineNum++;
                    if (line.toLowerCase().contains("custom-")) {
                        System.out.println("line=" + line + " lineNum=" + lineNum + " onFile=" + file.getName());
                        System.out.println(" in Module=" + file.getAbsolutePath().substring(28, 58));
                        FileObject fileObject = new FileObject(file.getAbsolutePath().substring(28, 58), file.getName(), line);

                        fileObjectList.add(fileObject);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            //handle this
        }
    }

    /**
     * Recursively traverse the directory and read the files
     *
     * @param path
     */
    public void traverse(String path) {

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                traverse(f.getAbsolutePath());
                // System.out.println( "Dir:" + f.getAbsoluteFile() );
            } else {
                //System.out.println( "File:" + f.getAbsoluteFile() );
                readFIle(f);
            }
        }
    }

    @Test
    public void testRun() throws IOException {
        traverse("/Users/prao2/dev/qbo/src/qbo/");

        FileWriter out = new FileWriter("book_new.csv");
        String[] HEADERS = {"path", "file"};
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {
            System.out.println("fileObjectList=" + fileObjectList.size());
            fileObjectList.forEach((fileObject) -> {
                try {
                    printer.printRecord(fileObject.module, fileObject.fileName, fileObject.line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
