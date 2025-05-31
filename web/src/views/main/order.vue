<template>
  <div class="order-train">
    <span class="order-train-main">{{dailyTrainTicket.date}}</span>&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.trainCode}}</span>次&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.start}}</span>站
    <span class="order-train-main">({{dailyTrainTicket.startTime}})</span>&nbsp;
    <span class="order-train-main">————</span>&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.end}}</span>站
    <span class="order-train-main">({{dailyTrainTicket.endTime}})</span>&nbsp;

    <div class="order-train-ticket">
      <span v-for="item in seatTypes" :key="item.type">
        <span>{{item.desc}}</span>:
        <span class="order-train-ticket-main">{{item.price}}￥</span>&nbsp;
        <span class="order-train-ticket-main">{{item.count}}</span>&nbsp;张票&nbsp;&nbsp;
      </span>
    </div>
  </div>
  <a-divider></a-divider>
  <b>勾选要购票的乘客：</b>&nbsp;
  <a-checkbox-group  v-model:value="passengerChecks" :options="passengerOptions" />
  <div class="order-tickets">
    <a-row class="order-tickets-header" v-if="tickets.length>0">
      <a-col :span="2">乘客</a-col>
      <a-col :span="6">身份证</a-col>
      <a-col :span="4">票种</a-col>
      <a-col :span="4">座位类型</a-col>
    </a-row>
    <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.id">
      <a-col :span="2">{{ticket.passengerName}}</a-col>
      <a-col :span="6">{{ticket.passengerIdCard}}</a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.passengerType" style="width: 100%">
          <a-select-option v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.seatTypeCode" style="width: 100%">
          <a-select-option v-for="item in seatTypes" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
    </a-row>
  </div>
  <div v-if="tickets.length>0">
    <a-button type="primary" size="middle" @click="finishCheckPassenger">提交订单</a-button>
  </div>
  <a-modal v-model:open="visible" title="请核对以下信息"
            style="top:50px;width: 800px"
            ok-texk="确认" cancel-text="取消">
    <div class="order-tickets">
      <a-row class="order-tickets-header" v-if="tickets.length>0">
        <a-col :span="3">乘客</a-col>
        <a-col :span="15">身份证</a-col>
        <a-col :span="3">票种</a-col>
        <a-col :span="3">座位类型</a-col>
      </a-row>
      <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.id">
        <a-col :span="3">{{ticket.passengerName}}</a-col>
        <a-col :span="15">{{ticket.passengerIdCard}}</a-col>
        <a-col :span="3">
          <span v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code">
             <span v-if="item.code===ticket.passengerType">
             {{item.desc}}
           </span>
          </span>
        </a-col>
        <a-col :span="3">
          <span v-for="item in seatTypes" :key="item.code">
             <span v-if="item.code===ticket.seatTypeCode">
             {{item.desc}}
           </span>
          </span>
        </a-col>
      </a-row>
      <br/>
      <div v-if="chooseSeatType===0" style="color: red">
        您购买的车票不支持选座
        <div>12306规则：只有全部是一等座或全部都是二等座才支持选座</div>
        <div>12306规则：余票小于一定数量时，不允许选座（本项目以20为例）</div>
      </div>
      <div v-else style="text-align: center">
        <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                  v-model:checked="chooseSeatObj[item.code+'1']" :checked-children="item.desc">
        </a-switch>
        <div v-if="tickets.length>1">
          <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                  v-model:checked="chooseSeatObj[item.code+'2']" :checked-children="item.desc">
          </a-switch>
        </div>
        <div style="color: #999999">提示：您可以选择{{tickets.length}}个座位</div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import {defineComponent,ref,onMounted,watch,computed} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "order-view",
  setup(){
    const passengers=ref([]);
    const passengerOptions=ref([]);
    const passengerChecks=ref([]);
    const dailyTrainTicket=SessionStorage.get(SESSION_ORDER) || {};
    console.log("下单的车次信息",dailyTrainTicket);
    const SEAT_TYPE=window.SEAT_TYPE;
    const seatTypes=[];
    for(let KEY in SEAT_TYPE){
      let key=KEY.toLowerCase();
      if(dailyTrainTicket[key]>=0){
        seatTypes.push({
          type:KEY,
          code:SEAT_TYPE[KEY]["code"],
          desc:SEAT_TYPE[KEY]["desc"],
          count:dailyTrainTicket[key],
          price:dailyTrainTicket[key+'Price'],
        })
      }
    }
    console.log("本次提供的座位：",seatTypes);

    const tickets=ref([]);
    const PASSENGER_TYPE_ARRAY=window.PASSENGER_TYPE_ARRAY;
    const visible=ref(false);
    //勾选或去掉某个乘客时在购票列表中加上或去掉一个车票
    watch(()=>passengerChecks.value,(newVal,oldVal)=>{
      console.log("勾选乘客发生变化",newVal,oldVal)
      //每次有变化时，清空购票列表，重新构造列表
      tickets.value=[];
      passengerChecks.value.forEach((item)=>tickets.value.push({
        passengerId:item.id,
        passengerName:item.name,
        passengerType:item.type,
        seatTypeCode:seatTypes[0].code,
        passengerIdCard:item.idCard
      }))
    },{immediate:true})

    //0不支持选座，1支持选一等座，2支持选二等座
    const chooseSeatType=ref(0);
    //根据选择的座位类型，计算出对应的列，比如要选的是一等座，就筛选出ACDF,要选的二等座，就筛选出ABCDF
    const SEAT_COL_ARRAY=computed(()=>{
      return window.SEAT_COL_ARRAY.filter(item=>item.type===chooseSeatType.value);
    });
    //选的座位
    //{
    //   A1:false,C1:true,D1:false,F1:false
    //   A2:false,C2:false,D2:false,F2:false
    //}
    const chooseSeatObj=ref({});
    watch(()=>SEAT_COL_ARRAY.value,()=>{
      for(let i=1;i<=2;i++){
        SEAT_COL_ARRAY.value.forEach((item)=>{
          chooseSeatObj.value[item.code+i]=false;
        })
      }
      console.log("初始化两排座位，都是未选中的：",chooseSeatObj.value);
    },{immediate:true});

    const handleQueryPassenger=()=>{
      axios.get('/member/passenger/query-mine').then((response)=>{
        let data = response.data;
        if(data.success){
          passengers.value = data.content;
          passengers.value.forEach(item=>passengerOptions.value.push({
            label: item.name,
            value: item
          }))
        }else{
          notification.error({description: data.message});
        }
      });
    }

    const finishCheckPassenger=()=>{
      console.log("购票列表：",tickets.value);
      if(tickets.value.length>5){
        notification.error({description: "最多只能购买5张票"});
        return
      }

      let seatTypesTemp=Tool.copy(seatTypes);
      for(let i=0;i<tickets.value.length;i++){
        let ticket=tickets.value[i];
        for(let j=0;j<seatTypesTemp.length;j++){
          let seatType=seatTypesTemp[j];
          if(ticket.seatTypeCode===seatType.code){
            seatType.count--;
            if(seatType.count<0){
              notification.error({description: seatType.desc+"余票不足"});
              return
            }
          }
        }
      }
      console.log("前端余票校验通过")

      //判断是否支持选座，只有纯一等座和纯二等座支持选座
      //先筛选出购票列表中的所有座位类型，比如四张表：【1，1，2，2】
      let ticketSeatTypeCodes=[];
      for(let i=0;i<tickets.value.length;i++){
        let ticket=tickets.value[i];
        ticketSeatTypeCodes.push(ticket.seatTypeCode);
      }
      //为购票列表中的所有座位类型去重：【1，2】
      const ticketSeatTypeCodeSet=Array.from(new Set(ticketSeatTypeCodes));
      console.log("选好的座位类型：",ticketSeatTypeCodeSet);
      if(ticketSeatTypeCodeSet.length!==1){
        console.log("选了多种座位，不支持选座");
        chooseSeatType.value=0;
      }else{
        if(ticketSeatTypeCodeSet[0]===SEAT_TYPE.YDZ.code){
          console.log("一等座选座");
          chooseSeatType.value=SEAT_TYPE.YDZ.code;
        }else if(ticketSeatTypeCodeSet[0]===SEAT_TYPE.EDZ.code){
          console.log("二等座选座");
          chooseSeatType.value=SEAT_TYPE.EDZ.code;
        }else{
          console.log("不是一二等座，不支持选座");
          chooseSeatType.value=0;
        }
        //当余票小于20时，不允许选座
        if(chooseSeatType.value!==0){
          for(let i=0;i<seatTypes.length;i++){
            let seatType=seatTypes[i];
            //找到同类型的座位
            if(ticketSeatTypeCodeSet[0]===seatType.code){
              //判断余票
              if(seatType.count<20){
                console.log("余票小于20张就不支持选座");
                chooseSeatType.value=0;
                break;
              }
            }
          }
        }
      }
      //弹出确认界面
        visible.value=true;
      };


    onMounted(()=>{
      handleQueryPassenger();
    })

    return{
      dailyTrainTicket,
      seatTypes,
      passengers,
      passengerOptions,
      passengerChecks,
      tickets,
      PASSENGER_TYPE_ARRAY,
      visible,
      finishCheckPassenger,
      chooseSeatType,
      SEAT_COL_ARRAY,
      chooseSeatObj,
    }
  }
})
</script>

<style>
.order-train .order-train-main{
  font-size: 18px;
  font-weight: bold;
}
.order-train-ticket {
  font-size: 16px;
  margin-top: 30px;
}
.order-train-ticket-main {
  font-size: 18px;
  color: red;
}
.order-tickets {
  margin: 10px 0;
}
.order-tickets .ant-col{
  padding: 5px 10px;
}
.order-tickets  .order-tickets-header{
  background-color: cornflowerblue;
  border:solid 1px cornflowerblue;
  color: white;
  font-size: 16px;
  padding: 5px 0;
}
.order-tickets .order-tickets-row{
  border:solid 1px cornflowerblue;
  border-top:none;
 vertical-align: middle;
  line-height: 30px;
}

.order-tickets .choose-seat-item{
  margin:5px 5px;
}

</style>