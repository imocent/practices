package com.fit.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.*;

/**
 * 基于JDK的微信XML工具类
 * 仅使用JDK原生API，无第三方依赖
 */
public class WechatXmlUtil {

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 设置安全特性，防止XXE攻击
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }

    public static String buildTextResponse(String toUser, String fromUser, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA[").append(toUser).append("]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[").append(fromUser).append("]]></FromUserName>");
        sb.append("<CreateTime>").append(System.currentTimeMillis() / 1000).append("</CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA[").append(content).append("]]></Content>");
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 将XML字符串转换为Map对象
     *
     * @param xmlString XML字符串
     * @return Map对象
     */
    public static Map<String, Object> xml2Map(String xmlString) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            InputSource source = new InputSource(new StringReader(xmlString));
            Document doc = newDocumentBuilder().parse(source);
            // 获取根元素
            Element root = doc.getDocumentElement();
            // 处理根元素的子节点
            processNodeList(root.getChildNodes(), map);

            return map;
        } catch (Exception e) {
            throw new RuntimeException("XML解析失败", e);
        }
    }

    /**
     * 处理节点列表
     *
     * @param nodeList  节点列表
     * @param resultMap 结果Map
     */
    private static void processNodeList(NodeList nodeList, Map<String, Object> resultMap) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // 只处理元素节点
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String elementName = element.getNodeName();
                // 检查是否已存在相同的键
                if (resultMap.containsKey(elementName)) {
                    Object existingValue = resultMap.get(elementName);
                    // 如果是List，直接添加
                    if (existingValue instanceof List) {
                        ((List<Object>) existingValue).add(element2MapOrString(element));
                    } else {
                        // 创建List并添加现有值和新值
                        List<Object> list = new ArrayList<>();
                        list.add(existingValue);
                        list.add(element2MapOrString(element));
                        resultMap.put(elementName, list);
                    }
                } else {
                    resultMap.put(elementName, element2MapOrString(element));
                }
            }
        }
    }

    /**
     * 将元素转换为Map或String
     *
     * @param element 元素
     * @return Map或String对象
     */
    private static Object element2MapOrString(Element element) {
        NodeList childNodes = element.getChildNodes();
        List<String> elementNames = getElementNames(childNodes);
        // 如果没有子元素，返回文本内容
        if (elementNames.isEmpty()) {
            return getElementText(element);
        }

        Map<String, Object> result = new HashMap<>();
        Set<String> distinctNames = new HashSet<>(elementNames);

        if (distinctNames.size() == 1) {
            // 所有子元素同名，转换为List
            String name = elementNames.iterator().next();
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    list.add(element2MapOrString((Element) node));
                }
            }
            result.put(name, list);
        } else if (distinctNames.size() == elementNames.size()) {
            // 所有子元素不同名，直接放入Map
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) node;
                    result.put(childElement.getNodeName(), element2MapOrString(childElement));
                }
            }
        } else {
            // 混合情况：有些同名，有些不同名
            Map<String, Integer> nameCountMap = new HashMap<>();
            for (String name : elementNames) {
                nameCountMap.put(name, nameCountMap.getOrDefault(name, 0) + 1);
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) node;
                    String nodeName = childElement.getNodeName();
                    if (nameCountMap.get(nodeName) == 1) {
                        // 只有一个同名元素，直接放入Map
                        result.put(nodeName, element2MapOrString(childElement));
                    } else {
                        // 有多个同名元素，放入List
                        List<Object> values;
                        if (result.containsKey(nodeName)) {
                            values = (List<Object>) result.get(nodeName);
                        } else {
                            values = new ArrayList<>();
                            result.put(nodeName, values);
                        }
                        values.add(element2MapOrString(childElement));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取元素下的所有子元素名称
     *
     * @param nodeList 节点列表
     * @return 元素名称列表
     */
    private static List<String> getElementNames(NodeList nodeList) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                names.add(node.getNodeName());
            }
        }
        return names;
    }

    /**
     * 获取元素的文本内容（处理CDATA等）
     *
     * @param element 元素
     * @return 文本内容
     */
    private static String getElementText(Element element) {
        StringBuilder text = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                text.append(node.getNodeValue());
            }
        }
        return text.toString().trim();
    }

    /**
     * 简单测试方法
     */
    public static void main(String[] args) {
        String xml = buildTextResponse("", "", "");
        Map<String, Object> map = xml2Map(xml);
        System.out.println(map);
    }
}