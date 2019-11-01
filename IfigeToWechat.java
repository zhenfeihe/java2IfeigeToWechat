package wechat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ifeigeToWechat
 * 
 * @author hezhenfei
 *
 */
public class IfigeToWechat {

	/**
	 * 公共方法
	 * @param url 消息发送网址
	 * @param map 消息发送的相关内容
	 * @return
	 */
	public static String publicMethod(String url, Map<String, Object> map) {
		// 创建一个httpclient对象
		CloseableHttpClient client = HttpClients.createDefault();
		// 创建一个post对象
		HttpPost post = new HttpPost(url);
		// 创建一个Entity，模拟表单数据
		List<NameValuePair> formList = new ArrayList<NameValuePair>();
		// 添加表单数据
		for (String key : map.keySet()) {
			formList.add(new BasicNameValuePair(key,map.get(key).toString()));
		}

		// 包装成一个Entity对象
		StringEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formList, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 设置请求的内容
		post.setEntity(entity);
		// 设置请求的报文头部的编码
		post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
		// 设置期望服务端返回的编码
		post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
		// 执行post请求
		CloseableHttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String resStr = null;
		// 获取响应码
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			// 获取数据
			try {
				resStr = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// 发送不成功，输出状态码
			System.out.println(statusCode);
		}
        System.out.println(resStr);
		return resStr;
	}

	
	/**
	 * 获取当前所有用户
	 * @param secret 必选，飞鸽快信系统分配给您的密钥，在用户中心查看
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String findUsers(String secret) throws ClientProtocolException, IOException, JSONException {
		// url
		String url = "http://u.ifeige.cn/api/userlist";
		// map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secret", secret==null?"":secret);
		String resStr = publicMethod(url, map);
		// 创建JSONObject对象，解析json格式的字符串
		JSONObject jsonObj = new JSONObject(resStr);
		// 获取用户信息
		String users = jsonObj.getString("list");
		return users;
	}

	
	/**
	 * 给群组添加接受消息的用户
	 * @param secret 必选，飞鸽快信系统分配给您的密钥，在用户中心查看
	 * @param token  必选，飞鸽快信群组app_key，可以群组详情里查看
	 * @param uid 必选，用户在飞鸽快信的ID，唯一，通过用户列表接口获得
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void addUsers(String secret, String token, String uid) throws ParseException, IOException {
		// url
		String url = "http://u.ifeige.cn/api/group_adduser";
		// map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secret", secret==null?"":secret);
		map.put("token", token==null?"":token);
		map.put("uid", uid==null?"":uid);
		publicMethod(url, map);
	}

	
	/**
	 * 给群组删除接收消息的用户
	 * @param secret 必选，飞鸽快信系统分配给您的密钥，在用户中心查看
	 * @param token  必选，飞鸽快信群组app_key，可以群组详情里查看
	 * @param uid  必选，用户在飞鸽快信的ID，唯一，通过用户列表接口获得
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void delUsers(String secret, String token, String uid) throws ParseException, IOException {
		// url
		String url = "http://u.ifeige.cn/api/group_deleteuser";
		// map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secret", secret==null?"":secret);
		map.put("token", token==null?"":token);
		map.put("uid", uid==null?"":uid);
		publicMethod(url, map);
	}

	
	/**
	 * 旧版的群发消息
	 * @param secret 必选，飞鸽快信系统分配给您的密钥，在用户中心查看，获取方式查看README.md
	 * @param token 必选，群组发消息对应Token，获取方式查看README.md
	 * @param key 可选，消息模板KEY，不传即视为故障通报通知
	 * @param title 可选，内容标题
	 * @param content 可选，发送内容正文
	 * @param remark 可选，内容备注
	 * @throws Exception
	 */
	public static void sendMsgOld(String secret, String token,String key, String title, String content, String remark)
			throws Exception {
		// url
		String url = "https://u.ifeige.cn/api/send_message";
		// map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secret", secret==null?"":secret);
		map.put("token", token==null?"":token);
		map.put("key", key==null?"notice":key); //"notice"
		map.put("title", title==null?"":title);
		map.put("content", content==null?"":content);
		map.put("remark", remark==null?"":remark);
		publicMethod(url, map);
	}
	
	/**
	 * 新版群消息发送暂时不能处理java的json类型字符串
	 * @param secret
	 * @param app_key
	 * @param template_id
	 * @param data
	 */
//	public static void sendMsgNew(String secret,String app_key,String template_id,String data) {
//		String url = "https://u.ifeige.cn/api/message/send";
//		// map
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("secret", secret);
//		map.put("app_key", app_key);
//		map.put("template_id", template_id);
//		map.put("data", data);
//		publicMethod(url, map);
//	}
	
	
	/**
	 * 给指定个人发送消息（老版）
	 * 各参数查找方法请到README.md中查看
	 * @param secret  必选，系统分配给您的密钥，在用户中心查看
	 * @param uid  必选，接收消息人员的ID，通过用户列表获得
	 * @param key  可选，消息模板KEY，不传即视为待处理通知
	 * @param title  必选，消息标题，请使用您自己的内容替换
	 * @param content  必选，消息内容，请使用您自己的内容替换
	 * @param remark  必选，消息详细说明，请使用您自己的内容替换
	 */
	public static void sendMsgPersonOld(String secret,String uid,String key, String title, String content, String remark) {
		String url = "http://u.ifeige.cn/api/user_sendmsg";
		// map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secret", secret==null?"":secret);
		map.put("uid", uid==null?"":uid);
		map.put("key", key==null?"":key);
		map.put("title", title==null?"":title);
		map.put("content", content==null?"":content);
		map.put("remark", remark==null?"":remark);
		publicMethod(url, map);
	}
}
