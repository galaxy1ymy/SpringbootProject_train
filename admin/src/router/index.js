import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'main',
    component: () => import('../views/main.vue'),
    children:[
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('../views/main/welcome.vue')
      },
      {
        path: 'about',
        name: 'about',
        component: () => import('../views/main/about.vue')
      },
      {
        path: 'base/',
        children:[
          {
            path: 'station',
            name: 'station',
            component: () => import('../views/main/base/station.vue')
          },
          {
            path: 'train',
            name: 'train',
            component: () => import('../views/main/base/train.vue')
          },
          {
            path: 'train-station',
            name: 'train-station',
            component: () => import('../views/main/base/train-station.vue')
          },
          {
            path: 'train-carriage',
            name: 'train-carriage',
            component: () => import('../views/main/base/train-carriage.vue')
          },
          {
            path: 'train-seat',
            name: 'train-seat',
            component: () => import('../views/main/base/train-seat.vue')
          }
        ]
      },
      {
        path: 'batch/',
       children: [{
           path: 'job',
           component: () => import('../views/main/batch/job.vue')
         }]
      },
      {
        path: 'business/',
        children:[
          {
            path: 'daily-train',
            component: () => import('../views/main/business/daily-train.vue')
          },
          {
            path: 'daily-train-station',
            component: () => import('../views/main/business/daily-train-station.vue')
          },
        ]
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
