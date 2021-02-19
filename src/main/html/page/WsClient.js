function WsClient() {
    this.state = 0;
    this.ip = null;
    this.port = null;
    this.onReceiveCallback = null;
    this.wsInstance = null;
    /**
     * 
     */
    this.connect = function(ip, port, clientId, onReceiveCallback) {
      console.log("Connection connect.");
      let _this = this;
      this.ip = ip;
      this.port = port;
      this.wsInstance = new WebSocket("ws://"+ ip + ":" + port);
      this.wsInstance.onopen = function(evt) { 
        console.log("Connection open ..."); 
        _this.state = 1;
      };

      this.wsInstance.onmessage = function(evt) { 
        onReceiveCallback(evt);
      };

      
      this.wsInstance.onclose = function(evt) {
        console.log("Connection closed.", evt);

        if(_this.state != 2) {
           _this.connect(ip, port, clientId, onReceiveCallback);
        }
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
      console.log('send message ', message);
      this.wsInstance.send(message);
    }
}