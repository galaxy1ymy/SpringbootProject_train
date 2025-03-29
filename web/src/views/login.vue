<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><CarTwoTone />12306售票系统</h1>
      <a-form
          :model="LoginForm"
          name="basic"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[
              { required: true, message: '请输入手机号!' },
              { pattern: /^1\d{10}$/, message: '请输入正确的手机号!' }
            ]"
        >
          <a-input v-model:value="LoginForm.mobile" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[
              { required: true, message: '请输入验证码！' },
              { pattern: /^\d{4}$/, message: '验证码必须是4位数字' }
            ]"
        >
          <a-input v-model:value="LoginForm.code" placeholder="请输入验证码">
            <template #addonAfter>
              <a @click="sendCode">获取验证码</a>
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
            <a-button type="primary" block html-type="submit">登录</a-button>
        </a-form-item>
      </a-form>
    </a-col>
  </a-row>
</template>

<script setup>
import { reactive } from 'vue';
const LoginForm = reactive({
  mobile:'',
  code:''
});
const onFinish = values => {
  console.log('Success:', values);
};
const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo);
};
</script>

<style>
.login{
  margin-top: 100px;
}
.login-main h1{
  font-size: 25px;
  font-weight: bold;
}
.login-main{
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}

/* 添加以下样式 */
.login-main input {
  height: 40px;  /* 设置输入框高度 */
  line-height: 40px;

}


</style>
