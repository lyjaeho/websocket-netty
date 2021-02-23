var WS_CLIENT_INSTANCE = [];//[{key:'ip:port:clientId',instance:null}]

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
function WsClient(ip,port,clientId,wsInstance) {
  this.state = 0;
  this.ip = ip;
  this.port = port;
  this.clientId = clientId;
  this.wsInstance = wsInstance;
  this.timer = null;
  /**
   * 
   */
  this.connect = function(onReceiveCallback) {
    console.log("Connection connect.");
    let _this = this;
    this.wsInstance.onopen = function(evt) { 
      console.log("Connection open ...");
      _this.state = 1;
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

    this.wsInstance.onmessage = function(evt) { 
      onReceiveCallback(evt);
    };
    
    this.wsInstance.onclose = function(evt) {
      console.log("Connection closed.", evt);

      if(_this.state != 2) {
          _this.connect(onReceiveCallback);
      }
    };

    this.wsInstance.onerror = function (err) {
      console.log("websocket error", err);
      _this.connect(onReceiveCallback);
    };
  }
  this.reconnect = function() {
      
  }
  this.disconnect = function() {
    console.log("Connection closed01.");
    this.state = 2;
    this.wsInstance.close();
  }
  this.send = function(message){
    if(!this.wsInstance) {
      return;
    }

    try
    {
      if (this.wsInstance.readyState == WebSocket.OPEN) {
        console.log('send message ', message);
        this.wsInstance.send(message);
      } else {
        console.log("WebSocket 连接没有建立成功！");
      }
    } catch(err) {
      var cdt = new Date();
		  console.log("WebSocket send message error: " + cdt.toLocaleTimeString() + " " + err);
    }
  }
}

function buildWsClient(ip, port, clientId, callback) {
  if(!ip || !port) {
    console.log("ip or port is null");
    return;
  }
  
  var key = ip + ":" + port + ":" + clientId;
  for(var i = 0;i<WS_CLIENT_INSTANCE.length;i++) {
    if(WS_CLIENT_INSTANCE[i].key == key) {
      callback(ip, port, clientId, WS_CLIENT_INSTANCE[i].instance);
    }
  }

  var wsNewInstance = new WebSocket("ws://"+ ip + ":" + port);
  WS_CLIENT_INSTANCE.push({ key: key, instance: wsNewInstance });
  callback(ip, port, clientId, wsNewInstance);
}