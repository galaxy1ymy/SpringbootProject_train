<template>
  <a-select v-model:value="name" show-search allow-clear
            :filter-option="filterNameOption"
            @change="onChange" placeholder="请选择车站"
            :style="'width:'+localwidth">
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name" :label="item.name+item.namePinyin+item.namePy">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import { ref ,defineComponent,onMounted,watch} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
export default defineComponent({
  name:"station-select-view",
  props:["modelValue","width"],
  emits:["update:modelValue",'change'],
  setup(props,{emit}){
    const name=ref();
    const stations=ref([]);

    const localwidth=ref(props.width);
    if(Tool.isEmpty(props.width)){
      localwidth.value="100%";
    }
    //利用watch，动态获取父组件的值，如果放在onMounted或其他方法中，则只有第一次有效
    watch(()=>props.modelValue,()=>{
      console.log("props.modelValue",props.modelValue);
      name.value=props.modelValue;
    },{immediate:true});

    const queryAllStation=()=>{
      axios.get("/business/admin/station/query-all").then((response) => {
        let data = response.data;
        if(data.success){
          stations.value=data.content;
        }else{
          notification.error({description: data.message});
        }
      });
    }
    //下拉框筛选
    const filterNameOption=(input,option)=>{
      console.log(input,option);
      return option.label.toLowerCase().indexOf(input.toLowerCase())>=0;
    };

    const onChange=(value)=>{
      emit("update:modelValue",value);
      let station=stations.value.filter(item=>item.code===value)[0];
      if(Tool.isEmpty(station)){
        station={};
      }
      emit ("change",station);
    };

    onMounted(()=>{
      queryAllStation();
    });
    return{
      name,
      stations,
      filterNameOption,
      onChange,
      localwidth
    }
  }

})
</script>

<style scoped>

</style>