<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu :selectedKeys="selectedKeys" mode="inline"
            :openKeys="['batch','base','business']">
        <a-menu-item key="/welcome">
          <router-link to="/welcome">
            <CoffeeOutlined/>&nbsp; 欢迎
          </router-link>
        </a-menu-item>
        <a-menu-item key="/about">
          <router-link to="/about">
            <UserOutlined/>&nbsp;关于
          </router-link>
        </a-menu-item>
      <a-sub-menu key="batch">
        <template #title>
          <span>
            <UnorderedListOutlined/>跑批管理
          </span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined/>&nbsp;任务管理
          </router-link>
        </a-menu-item>
      </a-sub-menu>
       <a-sub-menu key="base">
         <template #title>
          <span>
            <UnorderedListOutlined/>基础数据
          </span>
         </template>
         <a-menu-item key="/base/station">
           <router-link to="/base/station">
             <UserOutlined/>&nbsp;车站管理
           </router-link>
         </a-menu-item>
         <a-menu-item key="/base/train">
           <router-link to="/base/train">
             <UserOutlined/>&nbsp;火车管理
           </router-link>
         </a-menu-item>
         <a-menu-item key="/base/train-station">
           <router-link to="/base/train-station">
             <UserOutlined/>&nbsp;火车车站
           </router-link>
         </a-menu-item>
         <a-menu-item key="/base/train-carriage">
           <router-link to="/base/train-carriage">
             <UserOutlined/>&nbsp;火车车厢
           </router-link>
         </a-menu-item>
         <a-menu-item key="/base/train-seat">
           <router-link to="/base/train-seat">
             <UserOutlined/>&nbsp;火车座位
           </router-link>
         </a-menu-item>
       </a-sub-menu>
      <a-sub-menu key="business">
        <template #title>
          <span>
            <UnorderedListOutlined/>业务管理
          </span>
        </template>
        <a-menu-item key="/business/daily-train-ticket">
          <router-link to="/business/daily-train-ticket">
            <UserOutlined/>&nbsp;余票信息
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <UserOutlined/>&nbsp;每日车次
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-station">
          <router-link to="/business/daily-train-station">
            <UserOutlined/>&nbsp;每日车站
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-carriage">
          <router-link to="/business/daily-train-carriage">
            <UserOutlined/>&nbsp;每日车厢
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-seat">
          <router-link to="/business/daily-train-seat">
            <UserOutlined/>&nbsp;每日座位
          </router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref,watch} from 'vue';
import router from "@/router";

export default defineComponent({
  name:"the-sider-view",
  setup() {
    const selectedKeys = ref([]);
    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch',newValue);
      selectedKeys.value=[];
      selectedKeys.value.push(newValue);
    }, { immediate: true });
    return{
      selectedKeys
    };
  }

})

</script>

<style scoped>

</style>