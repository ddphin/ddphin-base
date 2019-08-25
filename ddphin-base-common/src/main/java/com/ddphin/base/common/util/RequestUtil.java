package com.ddphin.base.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class RequestUtil {
	public static String getFullRequestUrl(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	public static String getHeader(HttpServletRequest request, String name) {
		return request.getHeader(name);
	}
	public static String getParameter(HttpServletRequest request, String name) {
		return request.getParameter(name);
	}

	public static String getParameter(NativeWebRequest request, String name) {
		return request.getParameter(name);
	}
	public static MultiValueMap<String, MultipartFile> getMultiFiles(NativeWebRequest request) {
		MultiValueMap<String, MultipartFile> value = ((MultipartHttpServletRequest) request.getNativeRequest()).getMultiFileMap();
		return value;
	}
	public static Object getAttribute(HttpServletRequest request, String name) {
		return request.getAttribute(name);
	}
	public static Object getAttribute(NativeWebRequest request, String name, int scope) {
		Object value = request.getAttribute(name, scope);
		return value;
	}
	public static String getHeader(NativeWebRequest request, String name) {
		String value = StringUtils.EMPTY;
		try {			
			value = request.getHeader(name);
			if (StringUtils.isNotBlank(value)) {
				value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
			}
		} catch (UnsupportedEncodingException e) {
			log.error("getHeader error", e);
		}
		return value;
	}
	public static String getIp(NativeWebRequest nativeReq) {
		HttpServletRequest request = nativeReq.getNativeRequest(HttpServletRequest.class);
		assert request != null;
		return getIp(request);
	}
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0,index);
			}else{
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	public static Object setAttribute(HttpServletRequest request, String name, Object value) {
		Object oldValue = RequestUtil.getAttribute(request, name);
		request.setAttribute(name, value);
		return oldValue;
	}
	
	public static JSONObject getJsonRequestBody(HttpServletRequest request) {
		String xml = getXMLRequestBody(request);
		return getJsonRequestBodyFromXml(xml);
	}
	
	public static String getXMLRequestBody(HttpServletRequest request) {
		try {
			String xml = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8.name()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while (null != (line = in.readLine())) {
				sb.append(line.trim());
			}
			xml = sb.toString();
			log.info("getXMLRequestBodyFrom xml:{}", xml);
			return xml;
		} catch (Exception e) {
			log.error("getXMLRequestBodyFrom error", e);
			return null;
		}
	}
	
	public static JSONObject getJsonRequestBodyFromXml(String xml) {
		JSONObject body = new JSONObject();
		try {
			Map<String, String> map = xmlToMap(xml);
			body.putAll(map);
		} catch (Exception e) {
			log.error("getRequestBodyFromXmlData error", e);
		}
		
		return body;
	}
	
	public static JSONObject getRequestBodyFromFormData(HttpServletRequest request, String... excludesURLDecode) {
		JSONObject body = new JSONObject();
		Enumeration<String> names = request.getParameterNames();
		List<String>  excludeList = null == excludesURLDecode ? new ArrayList<>() : Arrays.asList(excludesURLDecode);
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String[] values = request.getParameterValues(name);
			Object v = null;
			try {
				if (null != values && 1 == values.length) {
					String value = values[0].trim();
					if (0 < value.length()) {
						if (!excludeList.contains(name)) {
							v = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
						}
						else {
							v = value;
						}
					}
				}
				else if (null != values && 1 < values.length) {				
					JSONArray array = new JSONArray();
					for (String value : values) {
						String s = value.trim();
						if (0 < s.length()) {
							if (!excludeList.contains(name)) {
								array.add(URLDecoder.decode(value, StandardCharsets.UTF_8.name()));
							}
							else {
								array.add(value);
							}
						}
					}
					v = array;
				}
				if (null != v) {
					body.put(name, v);
				}
			}
			catch (Exception e) {
				log.error("getRequestBodyFromFormData error", e);
			}
		}
		return body;
	}
	
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        Map<String, String> data = new HashMap<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(strXML.getBytes(StandardCharsets.UTF_8.name()));
        org.w3c.dom.Document doc = documentBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int idx=0; idx<nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                data.put(element.getNodeName(), element.getTextContent());
            }
        }
        try {
            stream.close();
        }
        catch (Exception e) {
        	log.error("xmlToMap error" ,e);
        }
        return data;
    }
}
