var WS_CLIENT_INSTANCE = []; //[{key:'ip:port:clientId',instance:null}]

var WS_COMMAND_LOGIN_BEGIN = 1;
var WS_COMMAND_LOGIN_SUCCESS = 2;
var WS_COMMAND_LOGIN_ERROR = 3;

function WsDataHeader(id, command, uniqueId) {
  this.command = command;
  this.id = id;
  this.sessionId = null;
  this.uniqueId = uniqueId;
  this.replyHeader = null;
}

function WsData(header, content) {
  this.header = header;
  this.content = content;
}

/** 
0 ：对应常量WebSoCONNECTING (numeric value 0)，
正在建立连接连接，还没有完成。The connection has not yet been established.
1 ：对应常量OPEN (numeric value 1)，
连接成功建立，可以进行通信。The WebSocket connection is established and communication is possible.
2 ：对应常量CLOSING (numeric value 2)
连接正在进行关闭握手，即将关闭。The connection is going through the closing handshake.
3 : 对应常量CLOSED (numeric value 3)
连接已经关闭或者根本没有建立。The connection has been closed or could not be opened.
**/
function WsClient(ip, port, clientId) {
  this.state = 0;
  this.ip = ip;
  this.port = port;
  this.clientId = clientId;
  this.webSocket = null;
  this.onLoginCallback = null;
  /**
   * 
   */
  this.connect = function (onLoginCallback, onReceiveCallback) {
    if (this.webSocket != null) {
      console.log('has connected');
      return;
    }

    console.log("Connection connect.");
    let _this = this;
    this.onLoginCallback = onLoginCallback;
    this.webSocket = new WebSocket("ws://" + ip + ":" + port);
    this.webSocket.onopen = function (evt) {
      console.log("Connection open ...");
      _this.state = 1;

      // 发送登录请求
      var wsData = new WsData(new WsDataHeader(_this.clientId, WS_COMMAND_LOGIN_BEGIN, new Date().getTime()), _this.clientId);
      var wsDataJson = JSON.stringify(wsData);
      console.log('send:' + wsDataJson);
      _this.webSocket.send(wsDataJson);

        if(this.timer == null){
            var $this = this;
            this.timer = window.setInterval(function(){
                if($this.wsInstance != null){
                    try {
                        $this.wsInstance.send('HEARTBEAT');
                    } catch(e){
                        console.error(e);
                    }
                }
            }, 5000);
        }
    };

    this.webSocket.onmessage = function (evt) {
      try {
        var wsData = JSON.parse(evt.data);
        console.log(wsData);
        if (wsData.header.command == WS_COMMAND_LOGIN_ERROR) {
          // 登录失败
          _this.onLoginCallback(false, wsData);
        } else if (wsData.header.command == WS_COMMAND_LOGIN_SUCCESS) {
          // 登录成功
          _this.onLoginCallback(true, wsData);
        } else {
          // 其他消息
          onReceiveCallback(wsData);
        }
      } catch (e) {
        console.error(e);
      }
    };

    this.webSocket.onclose = function (evt) {
      console.log("Connection closed.", evt);

      if (_this.state != 2) {
        _this.webSocket = null;
        _this.connect(onReceiveCallback);
      }
    };

    this.webSocket.onerror = function (err) {
      console.log("websocket error", err);
      _this.connect(onReceiveCallback);
    };
  }

  this.disconnect = function () {
    console.log("Connection closed01.");
    this.state = 2;
    var webSocket = this.webSocket;
    this.webSocket = null;
    webSocket.close();
  }

  this.send = function (message) {
    if (!this.webSocket) {
      return;
    }

    try {
      if (this.webSocket.readyState == WebSocket.OPEN) {
        console.log('send message ', message);
        this.webSocket.send(message);
      } else {
        console.log("WebSocket 连接没有建立成功！");
      }
    } catch (err) {
      var cdt = new Date();
      console.log("WebSocket send message error: " + cdt.toLocaleTimeString() + " " + err);
    }
  }
}

function buildWsClient(ip, port, clientId) {
  if (!ip || !port) {
    console.log("ip or port is null");
    return null;
  }

  var key = ip + ":" + port + ":" + clientId;
  for (var i = 0; i < WS_CLIENT_INSTANCE.length; i++) {
    if (WS_CLIENT_INSTANCE[i].key == key) {
      return WS_CLIENT_INSTANCE[i].instance;
    }
  }

  var wsClient = new WsClient(ip, port, clientId);

  WS_CLIENT_INSTANCE.push({
    key: key,
    instance: wsClient
  });
  return wsClient;
}