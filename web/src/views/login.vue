<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><CarTwoTone style="padding-right: 8px"/>12306售票系统</h1>
      <a-form
          :model="LoginForm"
          name="basic"
          autocomplete="off"
          @finish="login"
          @finishFailed="loginFailed"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[
              { required: true, message: '请输入手机号!' },
            ]"
        >
          <a-input v-model:value="LoginForm.mobile" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[
              { required: true, message: '请输入验证码！' },
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

<script>
import axios from 'axios';
import { defineComponent, reactive } from 'vue';
import { notification } from 'ant-design-vue';
import { useRouter } from "vue-router";
import store from "@/store";

export default defineComponent({
  name: "login-view",
  setup() {
    const LoginForm = reactive({
      mobile: '13000000000',
      code: ''
    });
    const router = useRouter();

    const login = () => {
        axios.post('/member/member/login', {
            mobile: LoginForm.mobile,
            code: LoginForm.code
        }).then(response => {
            let data = response.data;
            if (data.success) {
                store.commit('setMember', data.content);
                console.log('登录成功，获取到的token:', data.content.token);  // 添加这行
                notification.success({description: '登录成功',duration:3});
                router.push("/welcome");
            } else {
                notification.error({description: data.message});
            }
        });
    };

    const loginFailed = (errorInfo) => {
      console.log('Failed:', errorInfo);
    };

    const sendCode = () => {
      if (!LoginForm.mobile) {
        notification.error({description:'请输入手机号'});
        return;
      }
      axios.post('/member/member/send-code', {
        mobile: LoginForm.mobile
      }).then(response => {
        if (response.data.success) {
          notification.success({description:'验证码已发送',duration:2});
          LoginForm.code="8888";
        } else {
          notification.error({description:response.data.message});
        }
      });
    };

    return {
      LoginForm,
      sendCode,
      login,
      loginFailed
    };
  }
})
</script>

<style>
.login{
  margin-top: 100px;
  width: 1000px;
  height: 50px;
  margin-left: 200px;
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

.login-main input {
  height: 40px;
  line-height: 40px;

}


</style>
