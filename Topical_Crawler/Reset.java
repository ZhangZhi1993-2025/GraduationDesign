import java .io.*;
public class Reset {
  public static void  main(String[] args) throws Exception {
    BufferedWriter bw = new BufferedWriter(new FileWriter("doc/Init/visitedurl.txt"));
    bw.flush();
    bw.close();
    bw = new BufferedWriter(new FileWriter("doc/Init/priorqueue.txt"));
    bw.flush();
    bw.close();
    File file = new File("doc/创客网页");
    File[] fs = file.listFiles();
    for (File e : fs) {
      Delete(e.getPath());
    }

  }
  public static void Delete(String path) {
    File file = new File(path);
    if (file.isDirectory()) {
      File[] fs = file.listFiles();
      for (File e : fs) {
        Delete(e.getPath());
      }
      file.delete();
    } else {
      file.delete();
    }
  }
}
