//所有的session key都在这里统一定义,避免多个功能使用同一个Key
SESSION_ORDER="SESSION_ORDER";
SESSION_TICKET_PARAMS="SESSION_TICKET_PARAMS";

SessionStorage={
    get:function (key){
        var v=sessionStorage.getItem(key);//浏览器关闭就没有了，localStorage浏览器关闭数据还有
        if(v && typeof(v)!=="undefined"&&v!=="undefined"){
            return JSON.parse(v);
        }
    },
    set:function (key,data){
        sessionStorage.setItem(key,JSON.stringify(data));
    },
    remove:function (key){
        sessionStorage.removeItem(key);
    },
    clearAll:function (){
        sessionStorage.clear();
    }
}