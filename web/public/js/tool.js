Tool={
    isEmpty:(obj)=>{
        if((typeof obj==='string')){
            return !obj || obj.replace(/\s+/g,"")===""
        }else{
            return (!obj||JSON.stringify(obj)==="{}" || obj.length===0);
        }
    },
    isNotEmpty:(obj)=>{
        return !Tool.isEmpty(obj);
    },

    //对象复制
    copy:(obj)=>{
        if(Tool.isNotEmpty(obj)){
            return JSON.parse(JSON.stringify(obj));
        }
    },

    //使用递归将数组转为树形结构
    array2Tree:(array,parentId)=>{
        if(Tool.isEmpty(array)){
            return [];
        }
        const result=[];
        for(let i=0;i<array.length;i++){
            const c=array[i];
            if(Number(c.parent)===Number(parentId)){
                result.push(c);
                const children=Tool.array2Tree(array,c.id);
                if(Tool.isNotEmpty(children)){
                    c.children=children;
                }
            }
        }
        return result;
    },

    uuid:(len,radix=62)=>{
        const chars='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        const uuid=[];
        radix=radix || chars.length;
        for(let i=0;i<len;i++){
            uuid[i]=chars[0 | Math.random()*radix];
        }
        return uuid.join('');
    }
}