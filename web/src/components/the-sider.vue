<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu :selectedKeys="selectedKeys2">
        <a-menu-item key="1">
          <router-link to="/welcome">
            <CoffeeOutlined style="padding-right: 8px" />欢迎
          </router-link>
        </a-menu-item>
        <a-menu-item key="2">
          <router-link to="/passenger">
            <UserOutlined style="padding-right: 8px"/> 乘车人管理
          </router-link>

        </a-menu-item>
    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref,watch} from 'vue';
import { useRoute } from 'vue-router';

export default defineComponent({
  name:"the-sider-view",
  setup() {
    const route = useRoute();
    const selectedKeys2 = ref([]);
    watch(() => route.path, (path) => {
      selectedKeys2.value = path === '/passenger' ? ['2'] : ['1'];
    }, { immediate: true });
    return { selectedKeys2 };
  }

})

</script>

<style scoped>

</style>