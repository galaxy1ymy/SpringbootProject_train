<template>
    <p>
      <a-space>
        <a-button type="primary" @click="handleQuery()">刷新</a-button>
<!--        <a-button type="primary" @click="onAdd">新增</a-button>-->
      </a-space>
    </p>
    <a-table :dataSource="skTokens"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading">
      <template #bodyCell="{column,record}">
        <template v-if="column.dataIndex === 'operation'">
          <a-space>
<!--            <a-popconfirm
                title="删除后不可恢复，是否确认删除？"
                @confirm="onDelete(record)"
                ok-text="确认" cancel-text="取消">
              <a style="color: red">删除</a>
            </a-popconfirm>-->
            <a @click="onEdit(record)">修改令牌余量</a>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="visible" title="秒杀令牌" @ok="handleOk"
              ok-text="确认" cancel-text="取消">
      <a-form :model="skToken" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="日期">
            <a-date-picker v-model:value="skToken.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期" disabled/>
        </a-form-item>
        <a-form-item label="车次编号">
            <a-input v-model:value="skToken.trainCode" disabled/>
        </a-form-item>
        <a-form-item label="令牌数量">
            <a-input v-model:value="skToken.count" />
        </a-form-item>
      </a-form>
    </a-modal>
</template>

<script>
import { ref ,defineComponent,onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "sk-token-view",
  setup() {
    const visible = ref(false);
    let skToken = ref({
        id: undefined,
        date: undefined,
        trainCode: undefined,
        count: undefined,
        createTime: undefined,
        updateTime: undefined,
    });
    const skTokens = ref([]);
    //分页的两个属性名使固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    //防止用户频繁的点击提交按钮，导致多次请求
    let loading = ref(false);
    const columns = [
       {
         title: '日期',
         dataIndex: 'date',
         key: 'date',
       },
       {
         title: '车次编号',
         dataIndex: 'trainCode',
         key: 'trainCode',
       },
       {
         title: '令牌数量',
         dataIndex: 'count',
         key: 'count',
       },
       {
         title:'操作',
         dataIndex: 'operation'
       }
      ];

    const onAdd = () => {
      skToken.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      skToken.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete =(record)=>{
      axios.delete("/business/admin/sk-token/delete/"+record.id).then((response)=>{
        const data=response.data;
        if(data.success){
          notification.success({description:"删除成功！"});
          handleQuery({
            page:pagination.value.current,
            size:pagination.value.pageSize
          });
        }else{
          notification.error({description:data.message});
        }
      });
    };

      const handleOk = () => {
          axios.post('/business/admin/sk-token/save', skToken.value).then(response => {
              let data = response.data;
              if (data.success) {
                  notification.success({ description: '保存成功' });
                  visible.value = false;
                  // 保存成功后重新查询数据
                  handleQuery({
                      page: pagination.value.current,
                      size: pagination.value.pageSize
                  });
              } else {
                  notification.error({ description: data.message });
              }
          });
      };

    const handleQuery=(param)=>{
      if(!param){
        param = {
          page:1,
          size:pagination.value.pageSize
        }
      }
      loading.value = true;
      axios.get("/business/admin/sk-token/query-list",{
        params:{
          page:param.page,
          size:param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if(data.success){
          skTokens.value = data.content.list;
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

    //界面渲染好后执行
    onMounted(()=>{
      handleQuery({
        page:1,
        size:pagination.value.pageSize
      });
    })



    return{
      visible,
      skToken,
      skTokens,
      columns,
      pagination,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      onEdit,
      handleOk,
      onDelete
    }
  }
})

</script>

<style scoped>

</style>