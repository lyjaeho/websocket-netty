package xstartup.websocket.data;

public class WsDataHeader {
    /**
     * 命令码
     */
    private Integer command;

    private String uniqueId;

    private String id;

    private String sessionId;

    /**
     * 数据回复的header数据,当回应数据时才会使用
     */
    private WsDataHeader replyHeader;

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public WsDataHeader getReplyHeader() {
        return replyHeader;
    }

    public void setReplyHeader(WsDataHeader replyHeader) {
        this.replyHeader = replyHeader;
    }
}
