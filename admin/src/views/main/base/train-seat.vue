<template>
    <p>
      <a-space>
        <train-select-view v-model:value="params.trainCode" width="200px"></train-select-view>
        <a-button type="primary" @click="handleQuery()">查找</a-button>
      </a-space>
    </p>
    <a-table :dataSource="trainSeats"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading">
      <template #bodyCell="{column,record}">
        <template v-if="column.dataIndex === 'operation'">
        </template>
        <template v-else-if="column.dataIndex === 'col'">
          <span v-for="item in SEAT_COL_ARRAY" :key="item.code">
            <span v-if="item.code===record.col&& item.type===record.seatType">
              {{item.desc}}
            </span>
          </span>
        </template>
        <template v-else-if="column.dataIndex === 'seatType'">
          <span v-for="item in SEAT_TYPE_ARRAY" :key="item.code">
            <span v-if="item.code===record.seatType">
              {{item.desc}}
            </span>
          </span>
        </template>
      </template>
    </a-table>
</template>

<script>
import { ref ,defineComponent,onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";


export default defineComponent({
  name: "train-seat-view",
  components: {TrainSelectView},
  setup() {
    const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
    const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
    const visible = ref(false);
    let trainSeat = ref({
        id: undefined,
        trainCode: undefined,
        carriageIndex: undefined,
        row: undefined,
        col: undefined,
        seatType: undefined,
        carriageSeatIndex: undefined,
        createTime: undefined,
        updateTime: undefined,
    });
    const trainSeats = ref([]);
    //分页的两个属性名使固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    //防止用户频繁的点击提交按钮，导致多次请求
    let loading = ref(false);
    let params=ref({
      trainCode:null
    });
    const columns = [
       {
         title: '车次编号',
         dataIndex: 'trainCode',
         key: 'trainCode',
       },
       {
         title: '厢序',
         dataIndex: 'carriageIndex',
         key: 'carriageIndex',
       },
       {
         title: '排号',
         dataIndex: 'row',
         key: 'row',
       },
       {
         title: '列号',
         dataIndex: 'col',
         key: 'col',
       },
       {
         title: '座位类型',
         dataIndex: 'seatType',
         key: 'seatType',
       },
       {
         title: '同车厢座序',
         dataIndex: 'carriageSeatIndex',
         key: 'carriageSeatIndex',
       },
      ];


    const handleQuery=(param)=>{
      if(!param){
        param = {
          page:1,
          size:pagination.value.pageSize
        }
      }
      loading.value = true;
      axios.get("/business/admin/train-seat/query-list",{
        params:{
          page:param.page,
          size:param.size,
          trainCode:params.value.trainCode
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if(data.success){
          trainSeats.value = data.content.list;
          //设置分页控件的值,随着点击时间改变页码
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        }else{
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange=(page)=>{
      pagination.value.pageSize = page.pageSize;
      handleQuery({
        page:page.current,
        size:page.pageSize
      })
    }

    //界面渲染好后执行
    onMounted(()=>{
      handleQuery({
        page:1,
        size:pagination.value.pageSize
      });
    })

    return{
      SEAT_COL_ARRAY,
      SEAT_TYPE_ARRAY,
      visible,
      trainSeat,
      trainSeats,
      columns,
      pagination,
      handleTableChange,
      handleQuery,
      loading,
      params
    }
  }
})

</script>

<style scoped>

</style>