
import java.util.List;

import parse.Formatter;
import scrape.LibeHtml;;

//TODO アプリにする
public class Main {

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		LibeHtml html = new LibeHtml();
		List<String[]> list = html.getProfileTexts();
		//		List<List<String>>  list=html.testProfile("https://libe-shinjuku.com/profile-yuri.html");
		//
		//		List<String> list= new ArrayList<String>();
		//		list.add("https://libe-shinjuku.com/profile-yuri.html");
		//		list.add("https://libe-shinjuku.com/profile-marin.html");
		//		list.add("https://libe-shinjuku.com/profile-sora.html");
		//		List<List<String> > e=html.ListingProfile(list);
		Formatter f = new Formatter();
		f.editToHJson(list);
		long endTime = System.currentTimeMillis();

		System.out.println("全体処理時間：" + (endTime - startTime) + " ms");
	}

}
