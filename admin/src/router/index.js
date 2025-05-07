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
      {
        path: 'train',
        name: 'train',
        component: () => import('../views/main/train.vue')
      },
      {
        path: 'train-station',
        name: 'train-station',
        component: () => import('../views/main/train-station.vue')
      },
      {
        path: 'train-carriage',
        name: 'train-carriage',
        component: () => import('../views/main/train-carriage.vue')
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
