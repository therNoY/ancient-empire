package pers.mihao.ancient_empire.common.vo;


import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.util.Validate;

/**
 * String u = "https://127.0.0.1:8080/song/get?name=1&auther=2";
 */
public class URL {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String url; // url https://127.0.0.1:8080/song/get?name=1&auther=2
    private String path; // 路径 https://127.0.0.1:8080/song/get
    private String query; // 参数 name=1&author=2
    private String file; // 文件 song/get?name=1&author=2o
    private String protocol; // 协议 https
    private String domain; // 域名 127.0.0.1:8080
    private String host; // 主机名 127.0.0.1
    private String port; // 端口名 8080


    private Map<String, String> paramMap;

    public URL(String url) {
        this.url = url;
        paramMap = new LinkedHashMap<>();
        analyzeQuery(url);
    }

    /**
     * 解析URL 如果有参数 将URL解析成域名 + 参数
     * @param url
     */
    private void analyzeQuery(String url) {
        String [] mess = url.split("\\?");
        if (mess.length == 1) {
            if (Validate.isUrl(url)) {
                this.path = mess[0];
                analyzePath(path);
            }
            return;
        }
        if (mess.length > 2)
            throw new AncientEmpireException("URL 格式异常");
        // 解析出path
        this.path = mess[0];
        analyzePath(path);

        this.query = mess[1];
        String [] params = this.query.split("&");
        for (String param : params) {
            String[] parMess = param.split("=");
            if (parMess.length == 2) {
                paramMap.put(parMess[0], parMess[1]);
            }else if (parMess.length == 1) {
                paramMap.put(parMess[0], "");
            }else {
                throw new AncientEmpireException("URL 格式异常");
            }
        }
    }


    void analyzePath(String path) {
        // TODO 解析出path
    }

    public String getDomain() {
        return domain;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    /**
     * 获取完整的URI
     * @return
     */
    public String getURL() {
        StringBuffer newUrl = new StringBuffer(path);
        boolean isFirstAppend = true;
        for (Map.Entry entry : paramMap.entrySet()) {
            if (isFirstAppend) {
                newUrl.append("?");
                isFirstAppend = false;
            }else {
                newUrl.append("&");
            }
            newUrl.append(entry.getKey()).append("=").append(entry.getValue());
        }
        log.info("通过URL 工具重新获得的URL");
        return newUrl.toString();
    }


    public URL addParams(String key, String value) {
        paramMap.put(key, value);
        return this;
    }

    public URL addParams(String key, Integer value) {
        paramMap.put(key, String.valueOf(value));
        return this;
    }

    public void delParams(String key) {
        paramMap.remove(key);
    }

    public String getParams (String key) {
        return paramMap.get(key);
    }

    @Override
    public String toString() {
        return getURL();
    }
}
