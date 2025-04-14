<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu>
        <a-menu-item key="welcome"><CoffeeOutlined style="padding-right: 8px" />欢迎</a-menu-item>
        <a-menu-item key="passenger">
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
    const selectedKeys = ref([]);

    watch(() => route.name, (newName) => {
      selectedKeys.value = newName === 'passenger' ? ['passenger'] : ['welcome']
    }, { immediate: true });
    return{
      selectedKeys
    };
  }
})

</script>

<style scoped>

</style>