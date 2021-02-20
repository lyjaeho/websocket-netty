package xstartup.websocket.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WsData {
    public final static int SYS_COMMAND_LOGIN_BEGIN = 1;
    public final static int SYS_COMMAND_LOGIN_SUCCESS = 2;
    public final static int SYS_COMMAND_LOGIN_ERROR = 3;

    public final static String SimpleDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 命令码
     */
    private WsDataHeader header;
    /**
     * 数据内容
     */
    private String content;

    public WsData() {
    }

    public static WsData build(String id, String sessionId, int command, String content) {
        WsData data = new WsData();
        data.content = content;
        data.header = new WsDataHeader();
        data.header.setId(id);
        data.header.setSessionId(sessionId);
        data.header.setCommand(command);
        data.header.setUniqueId(String.valueOf(new Date().getTime()));
        return data;
    }

    public WsDataHeader getHeader() {
        return header;
    }

    public void setHeader(WsDataHeader header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static String toJson(WsData data) throws Exception {
        ObjectMapper objectMapper = getObjectMapper();

        return objectMapper.writeValueAsString(data);
    }

    public static WsData fromJson(String json) throws Exception {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.readValue(json, WsData.class);
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
//        module.addDeserializer(Long.class, new CustomLongDeserializer());
//        module.addDeserializer(Integer.class, new CustomIntegerDeserializer());
//        module.addDeserializer(Date.class, new CustomDateDeserializer());
        mapper.registerModule(module);
        mapper.setDateFormat(new SimpleDateFormat(SimpleDateTimeFormat));
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return mapper;
    }
}
