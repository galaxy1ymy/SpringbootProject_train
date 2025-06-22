SESSION_ALL_TRAIN="SESSION_ALL_TRAIN"

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