<template>
  <a-layout-header class="header">
    <div class="logo">
      <router-link to="/welcome" style="color: white">
        12306
      </router-link>
    </div>
    <div style="float: right;color: white">
      您好,{{member.mobile}}！&nbsp;&nbsp;
      <router-link to="/login" style="color: white">退出登录</router-link>
    </div>
    <a-menu theme="dark" mode="horizontal" :style="{ lineHeight: '64px' }" :selectedKeys="selectedKeys">
      <a-menu-item key="/welcome">
        <router-link to="/welcome" style="color: inherit;">
          <CoffeeOutlined/>&nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger" style="color: inherit;">
          <UserOutlined/>&nbsp; 乘车人管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <UserOutlined/>&nbsp; 余票查询
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import router from "@/router";

export default defineComponent({
  name:"the-header-view",
  setup() {

    const selectedKeys = ref([]);
    let member=store.state.member
    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch',newValue);
      selectedKeys.value=[];
      selectedKeys.value.push(newValue);
    }, { immediate: true });


    return{
      selectedKeys,
      member

    };
  }
})
</script>


<style scoped>
.logo {
  float: left;
  width: 150px;
  height: 31px;
  color: white;
  font-size: 20px;

}
</style>
