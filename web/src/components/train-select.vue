<template>
  <a-select
      v-model:value="trainCode"
      show-search
      allow-clear
      optionFilterProp="children"
      :filterOption="filterOption"
      @change="onChange"
      placeholder="请选择车次"
      :style="'width:' + localWidth"
  >
    <a-select-option
        v-for="item in trains"
        :key="item.code"
        :value="item.code"
    >
      <span class="option-content">
        {{ item.code }} | {{ item.start }} ~ {{ item.end }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
import { ref, defineComponent, onMounted } from 'vue';
import { notification } from 'ant-design-vue';
import axios from 'axios';

export default defineComponent({
  name: 'TrainSelectView',
  props: ['modelValue', 'width'],
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const trains = ref([]);
    const trainCode = ref(props.modelValue);
    const localWidth = ref(props.width || '100%');

    const filterOption = (input, option) => {
      return option.children[0].children
          .toLowerCase()
          .includes(input.toLowerCase());
    };

    const onChange = (value) => {
      emit('update:modelValue', value);
      const train = trains.value.find(item => item.code === value) || {};
      emit('change', train);
    };

    onMounted(async () => {
      try {
        const response = await axios.get("/business/train/query-all");
        if (response.data.success) {
          trains.value = response.data.content;
        } else {
          notification.error({ description: response.data.message });
        }
      } catch (error) {
        notification.error({ description: '请求失败，请检查网络' });
      }
    });

    return {
      trainCode,
      trains,
      filterOption,
      onChange,
      localWidth
    };
  }
});
</script>

<style scoped>
.option-content {
  display: inline-block;
  width: 100%;
}
</style>
