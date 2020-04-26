package com.yc.mysicplayer.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 注册表操作
 * @company 源辰
 * @author navy
 *
 */
public class RegisterUtil {
	//将一个Map信息记录到注册表中
	public void recordRegistration(Map<String,String> entry) throws BackingStoreException{
		// 如果选的是systemNode...则保存在[HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs]
		// Preferences pre = Preferences.systemNodeForPackage(RegistrationOp.class);
		// 如果选的是userNode.... 则保存在[HKEY_CURRENT_USER\Software\JavaSoft\Prefs]
		Preferences pre = Preferences.userNodeForPackage(RegisterUtil.class);
		//也可以使用流的形式将一个xml导入进来
		//Preferences.importPreferences(is);
		if(entry != null){
			Set<String> keys = entry.keySet();
			Iterator<String> its = keys.iterator();
			String key = null;
			while(its.hasNext()){
				key = its.next();
				pre.put(key, entry.get(key));
			}
		}
		pre.flush();
	}

	/**
	 * 获取注册表中指定信息
	 * @param key
	 * @return
	 */
	public String find(String key){
		Preferences pre = Preferences.userNodeForPackage(RegisterUtil.class);
		return pre.get(key, null);
	}
	
	/**
	 * 获取所有信息
	 * @return
	 * @throws BackingStoreException 
	 */
	public Map<String, String> findAll() throws BackingStoreException {
		Preferences pre = Preferences.userNodeForPackage(RegisterUtil.class);
		String[] keys = pre.keys();
		if (keys == null || keys.length <= 0) {
			return Collections.emptyMap();
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String key : keys) {
			result.put(key, pre.get(key, ""));
		}
		return result;
	}
	
	/**
	 * 删除指定信息
	 * @param key
	 */
	public void delInfo(String key){
		Preferences now =Preferences.userNodeForPackage(RegisterUtil.class);
		now.remove(key);
	}
}