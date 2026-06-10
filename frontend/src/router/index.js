import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue'),
      },
      {
        path: 'blog',
        name: 'BlogList',
        component: () => import('../views/blog/BlogList.vue'),
      },
      {
        path: 'blog/:id',
        name: 'BlogDetail',
        component: () => import('../views/blog/BlogDetail.vue'),
      },
      {
        path: 'blog/create',
        name: 'BlogCreate',
        component: () => import('../views/blog/BlogEdit.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'blog/edit/:id',
        name: 'BlogEdit',
        component: () => import('../views/blog/BlogEdit.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'forum',
        name: 'ForumHome',
        component: () => import('../views/forum/ForumHome.vue'),
      },
      {
        path: 'forum/thread/:id',
        name: 'ThreadDetail',
        component: () => import('../views/forum/ThreadDetail.vue'),
      },
      {
        path: 'forum/create',
        name: 'ThreadCreate',
        component: () => import('../views/forum/ThreadCreate.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('../views/auth/Login.vue'),
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('../views/auth/Register.vue'),
      },
      {
        path: 'profile/:userId?',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
