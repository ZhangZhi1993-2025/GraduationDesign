package retrive;
import java.util.*;
import java.io.*;
public class PriorQueue {
	private static PriorityQueue<UrlValue> pq = new PriorityQueue<UrlValue>();
	private static Set<String> visitedUrl = new HashSet<String>();
	public static void addVisitedUrl(String[] urls) {
		for (String s : urls) {
			visitedUrl.add(s);
		}
	}
	public static int size() {
		return pq.size();
	}
	public static void print() {
		while (pq.peek() != null) {
			System.out.println(pq.remove().url);
		}
	}
	public static void visitedPrint() {
		// System.out.println("visitedPrint");
		for (String s : visitedUrl)
			System.out.println(s);
		//  System.out.println("visitedPrintend");
	}
	public static void add(UrlValue url) {
		if (url != null && !url.url.trim().equals("") && !visitedUrl.contains(url.url)) {
			url.url = url.url.trim();
			pq.offer(url);
		}
	}

	//初始化优先队列
	public static void InitQueue(String filepath) {
		try {
			SetVisitedUrl(filepath + "/visitedurl.txt");
			SetSeeds(filepath + "/seeds.txt");
			SetPriorQueue(filepath + "/priorqueue.txt");
		} catch (Exception e) {
			System.out.println(e);
		} finally {

		}
	}
	public static void end(String filepath) {
		try {
			SaveVisitedUrl(filepath + "/visitedurl.txt");
			SavePriorQueue(filepath + "/priorqueue.txt");
		} catch (Exception e) {
			System.out.println(e);
		} finally {

		}
	}

	//清除所有优先队列的数据，设置优先种子
	private static void SetSeeds(String filepath) throws Exception {
		pq.clear();
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = null;
		UrlValue cur = null;
		visitedPrint();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			// System.out.println(line);
			if (!line.equals("")) {
				if (!visitedUrl.contains(line)) {
					cur = new UrlValue();
					cur.url = line;
					cur.value = 1;
					pq.offer(cur);
				} else {
					System.out.println("contain");
				}
			}
		}
		br.close();
	}

	//加载已经访问过的url
	private static void SetVisitedUrl(String filepath) throws Exception {
		System.out.println("visitedUrl=" + filepath);
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.equals(""))
				visitedUrl.add(line.trim());
		}
		br.close();
	}

	//保存已经访问过的URL
	private static void SaveVisitedUrl(String filepath) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
		Iterator<String> it = visitedUrl.iterator();
		while (it.hasNext()) {
			bw.write(it.next());
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	//加载待访问的url
	private static void SetPriorQueue(String filePath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		Scanner sc = null;
		String line = null;
		String url = null;
		UrlValue cur = null;

		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.equals("")) {
				sc = new Scanner(line);
				url = sc.next();
				if (!visitedUrl.contains(url)) {
					cur = new UrlValue();
					cur.url = url;
					cur.value = sc.nextDouble();
					pq.offer(cur);
				}
			}
		}
		br.close();

	}

	//保存待访问的URL及其优先级
	private static void SavePriorQueue(String filepath) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
		PriorityQueue<UrlValue> temp = new PriorityQueue<UrlValue>();
		UrlValue cur = null;
		while (pq.peek() != null) {
			cur = pq.remove();
			temp.offer(cur);
			bw.write(cur.url + " " + cur.value);
			bw.newLine();
		}
		pq = temp;
		bw.flush();
		bw.close();
	}
	public static String next() {
		if (pq.peek() != null) {
			System.out.println("queue pop");
			String curUrl = pq.remove().url;
			if (!visitedUrl.contains(curUrl)) {
				visitedUrl.add(curUrl);
				return curUrl;
			} else
				return null;
		} else
			return null;
	}
	public static boolean isEmpty() {
		if (pq.peek() != null)
			return false;
		else
			return true;
	}
	public static void clear() {
		pq.clear();
	}
	public static void main(String[] args) {
		UrlValue uv = null;
		while (true) {
			uv = new UrlValue();
			uv.url = "12345678901234567890";
			uv.value = 1;
			add(uv);
			System.out.println(size());
		}
	}
}
class UrlValue implements Comparable {
	public String url;
	public double value;

	@Override
	public int compareTo(Object o) {
		UrlValue uv = (UrlValue)o;
		int result = value <= uv.value ? 1 : 0;
		if (value > uv.value) {
			return -1;
		}
		if (value == uv.value) {
			return 0;
		} else {
			return 1;
		}

	}

}
