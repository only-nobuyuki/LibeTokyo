package parse;

import java.util.List;
import java.util.regex.Matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import constant.LibeAttributeConstant;
import util.ExtractionPattern;

/**	プロフィールを加工する
 * @author unknown
 *
 */
public class Formatter {
	/*独自パターン*/
	ExtractionPattern extractionPattern = new ExtractionPattern();

	//TODO:DBに格納するための型で戻すように修正
	/**
	 * @author unknown
	 * @param libeProfileList htmlから抽出したままの個人ページ情報
	 * @return
	 */
	public String editToHJson(List<String[]> libeProfileList) {
		/*Node生成用*/
		ObjectMapper mapper = new ObjectMapper();
		/*全キャストのプロフィール*/
		ObjectNode allCastProfiles = mapper.createObjectNode();
		for (String[] personalProfileArray : libeProfileList) {
			// 全キャストのプロフィールのkey
			String allCastProfileskey = "";
			/*キャストのプロフィール*/
			ObjectNode castProfile = null;
			for (int i = 0; personalProfileArray.length > i; i++) {
				//ループ処理の最初に初期化
				if (i == 0) {
					castProfile = mapper.createObjectNode();
				}
				String profile = personalProfileArray[i];
				if (profile.length() < 25) {
					Matcher result = extractAttribute(LibeAttributeConstant.NAME, profile);
					if (result.find()) {
						allCastProfileskey = extractAttributeValue(profile);
					}
					//TODO:enumはforで回すように書き換える
					//					for(LibeAttributeConstant e:LibeAttributeConstant.values()) {
					//						checkProfile(e.getAttribute(), profile, castProfile);
					//					}
					checkProfile(LibeAttributeConstant.NAME, profile, castProfile);
					checkProfile(LibeAttributeConstant.AGE, profile, castProfile);
					checkProfile(LibeAttributeConstant.HEIGHT_HALFWIDTH, profile, castProfile);
					checkProfile(LibeAttributeConstant.BUST_HALFWIDTH, profile, castProfile);
					checkProfile(LibeAttributeConstant.STICK, profile, castProfile);
					checkProfile(LibeAttributeConstant.BALL, profile, castProfile);
					checkProfile(LibeAttributeConstant.PENIS_SIZE_HALFWIDTH, profile, castProfile);
					checkProfile(LibeAttributeConstant.TYPE, profile, castProfile);
				}
				if (i == personalProfileArray.length - 1) {
					allCastProfiles.putPOJO(allCastProfileskey, castProfile);
				}
			}
		}
		try {
			System.out.println(allCastProfiles.size());
			String json = mapper.writeValueAsString(allCastProfiles);
			System.out.println(json);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 属性名前の独自処理<br>
	 * 属性が名前の属性値には[]の特殊な情報が追記されているため
	 * @author unknown
	 * @param profile
	 * @return 属性「名前」の属性値
	 */
	private String extractAttributeValue(String profile) {
		Matcher result = extractionPattern.buildBracketPattern().matcher(profile);
		//WHYDO:属性名前は「名前:」で固定されているため
		int startIndex = 3;
		if (result.find()) {
			//属性が名前のプロフィールに[]があった場合はspecialProfile(大かっこなし)を除去
			String withBracket = result.group();
			profile = profile.replace(withBracket, "");
		}
		//属性が名前のみの場合
		return profile.substring(startIndex);
	}

	//TODO 取り出しと設定を分けた方が良いかも
	/**属性ごとに属性値を取り出しObjectNodeに設定する
	 * @author unknown
	 * @param attribute
	 * @param profile
	 * @param castProfile
	 */
	private void checkProfile(LibeAttributeConstant attribute, String profile, ObjectNode castProfile) {
		Matcher result = extractAttribute(attribute, profile);
		if (result.find()) {
			//属性が名前＆[]があった場合の処理
			String value = null;
			Matcher re = extractionPattern.buildBracketPattern().matcher(profile);
			if (extractionPattern.buildAttributePattern(LibeAttributeConstant.NAME).matcher(result.group()).find()
					&& re.find()) {
				//specialProfile(大かっこなし)を切り出して
				String withBracket = re.group();
				int startIndex = 1;
				int endIndex = withBracket.length() - 1;
				String specialProfile = withBracket.substring(startIndex, endIndex);
				castProfile.put("特記事項", specialProfile);
				//名前の値を切り出して
				String inProgress = profile.replace(withBracket, "");
				startIndex = 3;
				endIndex = inProgress.length();
				value = inProgress.substring(startIndex, endIndex);
			} else {
				final int INDEX = attribute.getAttribute().length() + 1;
				final int LASTINDEX = profile.length();
				value = profile.substring(INDEX, LASTINDEX);
			}
			castProfile.put(attribute.getAttribute(), value);
		} else {
			//TODO 属性取得できないエラー処理
		}
	}

	/**
	 * オリジナルプロフィールと属性パターンのマッチング
	 * @author unknown
	 * @param attribute
	 * @param profile
	 * @return マッチングの結果
	 */
	private Matcher extractAttribute(LibeAttributeConstant attribute, String profile) {
		return extractionPattern.buildAttributePattern(attribute).matcher(profile);
	}

}