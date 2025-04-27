import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'main',
    component: () => import('../views/main.vue'),
    children: [
      {
        path: 'about',
        name: 'about',
        component: () => import('../views/main/about.vue')
      },
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('../views/main/welcome.vue')
      },
      {
        path: 'station',
        name: 'station',
        component: () => import('../views/main/station.vue')
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


export default router
