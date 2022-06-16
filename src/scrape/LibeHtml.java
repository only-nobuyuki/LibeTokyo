package scrape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**ライブHPの情報を扱う
 * @author unknown
 */
public class LibeHtml {
	/**Libe東京のtopページから個人ページ内のテキスト情報を取得する
	 * @return アクセス先URLのテキスト情報
	 */
	public List<String[]> getProfileTexts() {
		return ListingProfile(getUrls());
	}

	/**Libe東京のWebページにつなぐ
	 * @author unknown
	 * @param url アクセス先URL
	 * @return アクセス先URLのテキスト情報
	 */
	private Document connectLIBE(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO ステータスコードによって振り分ける
			e.printStackTrace();
		}
		return doc;
	}

	/**indexから個人ページのURL抽出して保管
	 * @author unknown
	 * @return 個人ページのURLリスト
	 */
	private List<String> getUrls() {
		/*LIBE東京 東京ポータル*/
		final String LIBETOPPAGE = "https://libe-tokyo.com/index2.html";
		List<String> urlList = new ArrayList<>();
		Elements castsColumLeft = connectLIBE(LIBETOPPAGE).select("div.girls-left a[href*=/profile-]");
		Elements castsColumRight = connectLIBE(LIBETOPPAGE).select("div.girls-right a[href*=/profile-]");
		for (Element inform : castsColumLeft) {
			urlList.add(inform.absUrl("href"));
		}
		for (Element inform : castsColumRight) {
			urlList.add(inform.absUrl("href"));
		}
		return urlList;
	}

	/**個人ページから属性値がプロフィールになっている要素からテキストを取得
	 * @author unknown
	 * @param urlS 個人ページのURLリスト
	 * @return 個人ごとのプロフィールリスト
	 */
	private List<String[]> ListingProfile(List<String> urlS) {
		String[] personalProfileArray = null;
		List<String[]> libeProfileList = new ArrayList<>();
		for (String url : urlS) {
			Elements profilesSet = connectLIBE(url).select("dd.profile");
			for (int i = 0; profilesSet.size() > i; i++) {
				// 各個人ページを処理する最初に初期化
				if (i == 0) {
					personalProfileArray = new String[profilesSet.size()];
				}
				Element attributeValueProfile = profilesSet.get(i);
				personalProfileArray[i] = attributeValueProfile.ownText();
				// 各個人ページを処理する最後にリストに格納
				if (i == profilesSet.size() - 1) {
					libeProfileList.add(personalProfileArray);
				}
			}
		}
		return libeProfileList;
	}
}
