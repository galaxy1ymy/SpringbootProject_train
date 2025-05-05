import axios from 'axios';
import { message } from 'ant-design-vue';

axios.defaults.baseURL = process.env.VUE_APP_SERVER || 'http://localhost:8000';

// 响应拦截器
axios.interceptors.response.use(
    response => {
        // 直接返回成功响应
        return response;
    },
    error => {
        // 捕获错误
        console.log('响应拦截器错误：', error);

        // 错误类型处理
        if (error.response) {
            // 后端返回的错误信息
            const status = error.response.status;
            const statusText = error.response.statusText;
            const messageText = error.response.data?.message || '请求失败，请稍后再试！';

            if (status === 404) {
                message.error('请求的资源未找到！');
            } else if (status === 500) {
                message.error('服务器出错，请稍后再试！');
            } else {
                message.error(messageText);
            }
        } else if (error.request) {
            // 请求没有响应
            message.error('网络异常，请检查您的网络连接！');
        } else {
            // 请求设置错误
            message.error(`请求失败：${error.message}`);
        }

        return Promise.reject(error);
    }
);

export default axios;
