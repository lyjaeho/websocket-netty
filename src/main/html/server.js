'use strict'

const browserSync = require('browser-sync').create();
const { createProxyMiddleware } = require('http-proxy-middleware');

// 获取本地ip
function getIP() {
    const ifaces = require('os').networkInterfaces();
    let ip = '127.0.0.1';
    for (let dev in ifaces) {
        if (ifaces.hasOwnProperty(dev)) {
            ifaces[dev].forEach(details => {
                if (ip === '127.0.0.1' && details.family === 'IPv4') {
                    ip = details.address;
                }
            });
        }
    }
    return ip;
}

function getBaseDir(){
    return [
        "./page/"
    ];
}

function getWatchFiles(dirs){
    let watchFiles = new Array();
    for(var index = 0; index < dirs.length; index++){
        watchFiles.push(dirs[index] + '/**/*.*');
    }
    return watchFiles;
}

let proxyArray = []


/* browser-sync
 * 更多配置参考 http://www.browsersync.cn/docs/api/
*/
browserSync.init({
    // 配置server
    server: {
        // server的基础路径
        baseDir: getBaseDir(),
        // 使用中间件
        middleware: [...proxyArray]
    },
    // 定义host
    host: getIP(),
    // 设置端口
    port: 20201,
    // 项目启动时使用定义的host
    open: 'Local',
    // 项目启动时打开的页面路径
    startPath: '/demo.html',
    // 修改那些文件的时候浏览器页面会自动刷新
    files: getWatchFiles(getBaseDir())
})