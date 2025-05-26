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
</template>

<script>
import {defineComponent} from "vue";

export default defineComponent({
  name: "order-view",
  setup(){
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

    return{
      dailyTrainTicket,
      seatTypes
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



</style>