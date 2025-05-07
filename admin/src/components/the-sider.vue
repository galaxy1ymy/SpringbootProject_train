<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu :selectedKeys="selectedKeys" mode="inline">
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
        <a-menu-item key="/station">
          <router-link to="/station">
            <UserOutlined/>&nbsp;车站管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/train">
          <router-link to="/train">
            <UserOutlined/>&nbsp;火车管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/train-station">
          <router-link to="/train-station">
            <UserOutlined/>&nbsp;火车车站
          </router-link>
        </a-menu-item>
        <a-menu-item key="/train-carriage">
          <router-link to="/train-carriage">
            <UserOutlined/>&nbsp;火车车厢
          </router-link>
        </a-menu-item>
        <a-menu-item key="/train_seat">
          <router-link to="/train_seat">
            <UserOutlined/>&nbsp;火车座位
          </router-link>
        </a-menu-item>
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