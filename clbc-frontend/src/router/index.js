import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../store/auth';

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/LoginView.vue'),
        meta: { public: true }
    },
    {
        path: '/',
        name: 'Dashboard',
        component: () => import('../views/DashboardView.vue'),
        children: [
            {
                path: '',
                name: 'ResidentList',
                component: () => import('../views/ResidentListView.vue')
            },
            {
                path: 'review-queue',
                name: 'ReviewQueue',
                component: () => import('../views/ReviewQueueView.vue')
            },
            {
                path: 'residents/:id',
                name: 'ResidentDetail',
                component: () => import('../views/ResidentDetailView.vue')
            },
            {
                path: 'mar',
                name: 'ShiftMAR',
                component: () => import('../views/MARView.vue')
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (!to.meta.public && !authStore.isAuthenticated) {
        next('/login');
    } else if (to.path === '/login' && authStore.isAuthenticated) {
        next('/');
    } else {
        next();
    }
});

export default router;
