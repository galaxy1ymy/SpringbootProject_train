<template>
    <p>
      <a-space>
        <a-button type="primary" @click="handleQuery()">刷新</a-button>
        <#if !readOnly><a-button type="primary" @click="onAdd">新增</a-button></#if>
      </a-space>
    </p>
    <a-table :dataSource="${domain}s"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading">
      <template #bodyCell="{column,record}">
        <template v-if="column.dataIndex === 'operation'">
          <#if !readOnly>
          <a-space>
            <a-popconfirm
                title="删除后不可恢复，是否确认删除？"
                @confirm="onDelete(record)"
                ok-text="确认" cancel-text="取消">
              <a style="color: red">删除</a>
            </a-popconfirm>
            <a @click="onEdit(record)">编辑</a>
          </a-space>
          </#if>
        </template>
          <#list fieldList as field>
              <#if field.enums>
        <template v-else-if="column.dataIndex === '${field.nameHump}'">
          <span v-for="item in ${field.enumsConst}_ARRAY" :key="item.code">
            <span v-if="item.code===record.${field.nameHump}">
              {{item.desc}}
            </span>
          </span>
        </template>
              </#if>
          </#list>
      </template>
    </a-table>
    <#if !readOnly>
    <a-modal v-model:open="visible" title="乘车人" @ok="handleOk"
              ok-text="确认" cancel-text="取消">
      <a-form :model="${domain}" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
          <#list fieldList as field>
              <#if field.name!="id"&& field.nameHump!="createTime"&& field.nameHump!="updateTime">
        <a-form-item label="${field.nameCn}">
            <#if field.enums>
            <a-select v-model:value="${domain}.${field.nameHump}">
                <a-select-option v-for="item in ${field.enumsConst}_ARRAY" :key="item.code" :value="item.code">
                    {{item.desc}}
                </a-select-option>
            </a-select>
            <#elseif field.javaType=='Date'>
            <#if field.type=="time">
            <a-time-picker v-model:value="${domain}.${field.nameHump}" valueFormat="HH:mm:ss" placeholder="请选择时间" />
            <#elseif field.type=="date">
            <a-date-picker v-model:value="${domain}.${field.nameHump}" valueFormat="YYYY-MM-DD" placeholder="请选择日期" />
            <#else>
            <a-date-picker v-model:value="${domain}.${field.nameHump}" valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择日期时间" />
            </#if>
            <#else>
            <a-input v-model:value="${domain}.${field.nameHump}" />
            </#if>
        </a-form-item>
            </#if>
          </#list>
      </a-form>
    </a-modal>
    </#if>
</template>

<script>
import { ref ,defineComponent,onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "${do_main}-view",
  setup() {
      <#list fieldList as field>
          <#if field.enums>
    const ${field.enumsConst}_ARRAY = window.${field.enumsConst}_ARRAY;
          </#if>
      </#list>
    const visible = ref(false);
    let ${domain} = ref({
        <#list fieldList as field>
        ${field.nameHump}: undefined,
        </#list>
    });
    const ${domain}s = ref([]);
    //分页的两个属性名使固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    //防止用户频繁的点击提交按钮，导致多次请求
    let loading = ref(false);
    const columns = [
        <#list fieldList as field>
            <#if field.name!="id"&& field.nameHump!="createTime"&& field.nameHump!="updateTime">
       {
         title: '${field.nameCn}',
         dataIndex: '${field.nameHump}',
         key: '${field.nameHump}',
       },
            </#if>
        </#list>
        <#if !readOnly>
       {
         title:'操作',
         dataIndex: 'operation'
       }
        </#if>
      ];

    <#if !readOnly>
    const onAdd = () => {
      ${domain}.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      ${domain}.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete =(record)=>{
      axios.delete("/${module}/admin/${do_main}/delete/"+record.id).then((response)=>{
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
          axios.post('/${module}/admin/${do_main}/save', ${domain}.value).then(response => {
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
    </#if>

    const handleQuery=(param)=>{
      if(!param){
        param = {
          page:1,
          size:pagination.value.pageSize
        }
      }
      loading.value = true;
      axios.get("/${module}/admin/${do_main}/query-list",{
        params:{
          page:param.page,
          size:param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if(data.success){
          ${domain}s.value = data.content.list;
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
        <#list fieldList as field>
          <#if field.enums>
      ${field.enumsConst}_ARRAY,
          </#if>
        </#list>
      visible,
      ${domain},
      ${domain}s,
      columns,
      pagination,
      handleTableChange,
      handleQuery,
      loading,
      <#if !readOnly>
      onAdd,
      onEdit,
      handleOk,
      onDelete
      </#if>
    }
  }
})

</script>

<style scoped>

</style>