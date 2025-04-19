import axios from 'axios';
import { message } from 'ant-design-vue';

const instance = axios.create({
    baseURL: process.env.NODE_ENV === 'development' ? 'http://localhost:8000' : '',
    timeout: 5000,
    withCredentials: false,  // 修改为 false
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'Access-Control-Allow-Origin': '*'
    }
});

instance.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    response => response,
    error => {
        console.log('请求错误：', error);
        message.error('系统异常');
        return Promise.reject(error);
    }
);

export default instance;