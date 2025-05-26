<template>
    <p>
      <a-space>
        <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD"  placeholder="请选择日期"/>
        <station-select-view v-model:value="params.start" width="200px"></station-select-view>
        <station-select-view v-model:value="params.end" width="200px"></station-select-view>
        <a-button type="primary" @click="handleQuery()">查找</a-button>
      </a-space>
    </p>
    <a-table :dataSource="dailyTrainTickets"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading">
      <template #bodyCell="{column,record}">
        <template v-if="column.dataIndex === 'operation'">
          <a-button type="primary" @click="toOrder(record)">预订</a-button>
        </template>
        <template v-else-if="column.dataIndex ==='station'">
          {{record.start}}<br/>
          {{record.end}}
        </template>
        <template v-else-if="column.dataIndex ==='time'">
          {{record.startTime}}<br/>
          {{record.endTime}}
        </template>
        <template v-else-if="column.dataIndex ==='duration'">
          {{calDuration(record.startTime,record.endTime)}}<br/>
          <div v-if="record.startTime.replaceAll(':','') >= record.endTime.replaceAll(':','')">
            次日到达
          </div>
          <div v-else>
            当日到达
          </div>
        </template>
        <template v-else-if="column.dataIndex ==='ydz'">
          <div v-if="record.ydz >=0">
            {{record.ydz}}<br/>
            {{record.ydzPrice}}￥
          </div>
          <div v-else>
            --
          </div>
        </template>
        <template v-else-if="column.dataIndex ==='edz'">
          <div v-if="record.edz >=0">
            {{record.edz}}<br/>
            {{record.edzPrice}}￥
          </div>
          <div v-else>
            --
          </div>
        </template>
        <template v-else-if="column.dataIndex ==='rw'">
          <div v-if="record.rw >=0">
            {{record.rw }}<br/>
            {{record.rwPrice}}￥
          </div>
          <div v-else>
            --
          </div>
        </template>
        <template v-else-if="column.dataIndex ==='yw'">
          <div v-if="record.yw >=0">
            {{record.yw}}<br/>
            {{record.ywPrice}}￥
          </div>
          <div v-else>
            --
          </div>
        </template>
      </template>
    </a-table>
</template>

<script>
import { ref ,defineComponent,onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";
import router from "@/router";

export default defineComponent({
  name: "ticket-view",
  components: {StationSelectView},
  setup() {
    const visible = ref(false);
    let dailyTrainTicket = ref({
        id: undefined,
        date: undefined,
        trainCode: undefined,
        start: undefined,
        startPinyin: undefined,
        startTime: undefined,
        startIndex: undefined,
        end: undefined,
        endPinyin: undefined,
        endTime: undefined,
        endIndex: undefined,
        ydz: undefined,
        ydzPrice: undefined,
        edz: undefined,
        edzPrice: undefined,
        rw: undefined,
        rwPrice: undefined,
        yw: undefined,
        ywPrice: undefined,
        createTime: undefined,
        updateTime: undefined,
    });
    const dailyTrainTickets = ref([]);
    //分页的两个属性名使固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    //防止用户频繁的点击提交按钮，导致多次请求
    let loading = ref(false);
    let params=ref({
      trainCode:null,
      date:null,
      start:null,
      end:null
    });
    const columns = [
       {
         title: '车次编号',
         dataIndex: 'trainCode',
         key: 'trainCode',
       },
      {
        title:'车站',
        dataIndex: 'station'
      },
      {
        title:'时间',
        dataIndex: 'time'
      },
      {
        title:'历时',
        dataIndex: 'duration'
      },
       {
         title: '一等座',
         dataIndex: 'ydz',
         key: 'ydz',
       },
       {
         title: '二等座',
         dataIndex: 'edz',
         key: 'edz',
       },
       {
         title: '软卧',
         dataIndex: 'rw',
         key: 'rw',
       },
       {
         title: '硬卧',
         dataIndex: 'yw',
         key: 'yw',
       },
      {
        title: '操作',
        dataIndex: 'operation',
      }
      ];


    const handleQuery=(param)=>{
      if(Tool.isEmpty(params.value.date)){
        notification.error({description: "请输入日期"});
        return;
      }
      if(Tool.isEmpty(params.value.start)){
        notification.error({description: "请输入始发站"});
        return;
      }
      if(Tool.isEmpty(params.value.end)){
        notification.error({description: "请输入终点站"});
        return;
      }
      if(!param){
        param = {
          page:1,
          size:pagination.value.pageSize
        }
      }
      //保存查询参数
      SessionStorage.set(SESSION_TICKET_PARAMS,params.value);

      loading.value = true;
      axios.get("/business/daily-train-ticket/query-list",{
        params:{
          page:param.page,
          size:param.size,
          date:params.value.date,
          trainCode:params.value.trainCode,
          start:params.value.start,
          end:params.value.end
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if(data.success){
          dailyTrainTickets.value = data.content.list;
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

      const calDuration=(startTime,endTime)=>{
      let diff=dayjs(endTime,'HH:mm:ss').diff(dayjs(startTime,'HH:mm:ss'),'seconds');
      return dayjs('00:00:00','HH:mm:ss').second(diff).format('HH:mm:ss');
      }

      const toOrder=(record)=>{
      dailyTrainTicket.value=Tool.copy(record);
      SessionStorage.set(SESSION_ORDER,dailyTrainTicket.value);
      router.push("/order")
      }

    //界面渲染好后执行
    onMounted(()=>{
      // '|| {}'避免空指针异常
      params.value = SessionStorage.get(SESSION_TICKET_PARAMS)||{};
      if(Tool.isNotEmpty(params.value)){
        handleQuery({
            page:1,
            size:pagination.value.pageSize
      });
      }
    })



    return{
      visible,
      dailyTrainTicket,
      dailyTrainTickets,
      columns,
      pagination,
      handleTableChange,
      handleQuery,
      loading,
      params,
      calDuration,
      toOrder
    }
  }
})

</script>

<style scoped>

</style>