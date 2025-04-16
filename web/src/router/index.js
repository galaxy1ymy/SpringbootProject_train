import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";


const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/',
    name: 'main',
    component: () => import('../views/main.vue'),
    meta:{
      loginRequire:true
    },
    children: [
      {
        path: 'passenger',
        name: 'passenger',
        component: () => import('../views/passenger.vue')
      },
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('../views/main/welcome.vue')
      },
    ]
  },
    //访问根域名直接跳转到welcome
  {
    path: '',
    redirect: '/welcome'
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

//路由登录拦截
router.beforeEach((to,from,next)=>{
  //要不要对meta.loginRequire属性做监控拦截
  if(to.matched.some(function (items){
    console.log(items,"是否需要登录校验：",items.meta.loginRequire || false);
    return items.meta.loginRequire;
    })){
    const _member=store.state.member;
    console.log("页面登录校验开始：",_member);
    if(!_member.token){
      console.log("用户未登录或登录超时");
      notification.error({description:"用户未登录或登录超时"});
      next('/login');
    }else{
      next();
    }
  }else{
    next();
  }
})

export default router
