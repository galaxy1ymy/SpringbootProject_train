<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <div style="float: right;color: white">
      您好,{{member.mobile}}！&nbsp;&nbsp;
      <router-link to="/login" style="color: white">退出登录</router-link>
    </div>
    <a-menu theme="dark" mode="horizontal" :style="{ lineHeight: '64px' }" :selectedKeys="selectedKeys1">
      <a-menu-item key="1">
        <router-link to="/welcome" style="color: inherit;">
          <CoffeeOutlined style="padding-right: 8px"/>欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="2">
        <router-link to="/passenger" style="color: inherit;">
          <UserOutlined style="padding-right: 8px"/> 乘车人管理
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import { useRoute } from 'vue-router';

export default defineComponent({
  name:"the-header-view",
  setup() {
    const route = useRoute();
    const selectedKeys1 = ref([]);
    let member=store.state.member
    watch(() => route.path, (path) => {
      selectedKeys1.value = path === '/passenger' ? ['2'] : ['1'];
    }, { immediate: true });


    return{
      selectedKeys1,
      member

    };
  }
})
</script>


<style scoped>
.logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}
</style>
