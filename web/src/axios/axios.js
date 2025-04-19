import axios from 'axios';
import { message } from 'ant-design-vue';

axios.defaults.baseURL = process.env.VUE_APP_SERVER || 'http://localhost:8000';

axios.interceptors.response.use(
    response => {
        return response;
    }, 
    error => {
        console.log('响应拦截器错误：', error);
        message.error(error.message);
        return Promise.reject(error);
    }
);

export default axios;