package com.kince.util.sensitivewordsfilter;

import android.content.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SensitiveWordFilter {

	public String[] mIllegalWords;
	public Map sensitiveWordMap = null;

	public static int minMatchTYpe = 1;
	public static int maxMatchType = 2;

	public SensitiveWordFilter(Context context, int resId){
		sensitiveWordMap = initKeyWord(context,resId);
	}

	private Map initKeyWord(Context context, int resId){
		try {
			Set<String> keyWordSet = readSensitiveWord(context,resId);
			addSensitiveWordToHashMap(keyWordSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}

	private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
		sensitiveWordMap = new HashMap(keyWordSet.size());
		String key = null;
		Map nowMap = null;
		Map<String, String> newWorMap = null;
		Iterator<String> iterator = keyWordSet.iterator();
		while(iterator.hasNext()){
			key = iterator.next();
			nowMap = sensitiveWordMap;
			for(int i = 0 ; i < key.length() ; i++){
				char keyChar = key.charAt(i);
				Object wordMap = nowMap.get(keyChar);
				if(wordMap != null){
					nowMap = (Map) wordMap;
				}
				else{
					newWorMap = new HashMap<String,String>();
					newWorMap.put("isEnd", "0");
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}
				if(i == key.length() - 1){
					nowMap.put("isEnd", "1");
				}
			}
		}
	}

	private Set<String> readSensitiveWord(Context context, int resId) throws Exception {
		Set<String> set = new HashSet<>();
		mIllegalWords = context.getResources().getStringArray(resId);
		for (int i = 0; i < mIllegalWords.length; i++) {
			set.add(mIllegalWords[i]);
		}
		return set;
	}

	public boolean isContaintSensitiveWord(String txt, int matchType){
		boolean flag = false;
		for(int i = 0 ; i < txt.length() ; i++){
			int matchFlag = this.CheckSensitiveWord(txt, i, matchType);
			if(matchFlag > 0){
				flag = true;
			}
		}
		return flag;
	}

	public Set<String> getSensitiveWord(String txt , int matchType){
		Set<String> sensitiveWordList = new HashSet<String>();
		for(int i = 0 ; i < txt.length() ; i++){
			int length = CheckSensitiveWord(txt, i, matchType);
			if(length > 0){
				sensitiveWordList.add(txt.substring(i, i+length));
				i = i + length - 1;
			}
		}
		return sensitiveWordList;
	}

	public String replaceSensitiveWord(String txt, int matchType, String replaceChar){
		String resultTxt = txt;
		Set<String> set = getSensitiveWord(txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word = null;
		String replaceString = null;
		StringBuilder sb =new StringBuilder();
		while (iterator.hasNext()) {
			word = iterator.next();
			sb.append(word);

			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);

			if(sb.toString().length()<txt.length()){
				return txt;
			}
		}

		return resultTxt;
	}

	private String getReplaceChars(String replaceChar, int length){
		String resultReplace = replaceChar;
		for(int i = 1 ; i < length ; i++){
			resultReplace += replaceChar;
		}
		return resultReplace;
	}

	public int CheckSensitiveWord(String txt, int beginIndex, int matchType){
		boolean  flag = false;
		int matchFlag = 0;
		char word = 0;
		Map nowMap = sensitiveWordMap;
		for(int i = beginIndex; i < txt.length() ; i++){
			word = txt.charAt(i);
			nowMap = (Map) nowMap.get(word);
			if(nowMap != null){
				matchFlag++;
				if("1".equals(nowMap.get("isEnd"))){
					flag = true;
					if(SensitiveWordFilter.minMatchTYpe == matchType){
						break;
					}
				}
			}
			else{
				break;
			}
		}
		if(matchFlag < 2 || !flag){
			matchFlag = 0;
		}
		return matchFlag;
	}

}
